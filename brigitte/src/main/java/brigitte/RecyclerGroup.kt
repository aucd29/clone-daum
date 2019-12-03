@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.app.Application
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.*
import brigitte.viewmodel.CommandEventViewModel
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 6. <p/>
 *
 * ====
 * recycler 에 animation 을 사용하지 않을려면
 *
 * val itemAnimator = ObservableField<RecyclerView.ItemAnimator?>()
 *
 * 를 viewmodel 에 선언하고 xml 에
 *
 * app:bindItemAnimator="@{model.itemAnimator}"
 *
 * 를 선언한다. 이때 itemAnimator 에 별도의 RecyclerView.ItemAnimator 를 지정하지 않으면 된다.
 * ====
 */

/** item 비교 인터페이스 */
interface IRecyclerDiff {
    fun itemSame(item: IRecyclerDiff): Boolean
    fun contentsSame(item: IRecyclerDiff): Boolean
}

/** Item 타입 비교 인터페이스 */
interface IRecyclerItem {
    var type: Int
}

/** 아이템 위치 정보 인터페이스 */
interface IRecyclerPosition {
    var position: Int
}

/** 아이템 확장 관련 인터페이스 */
interface IRecyclerExpandable<T> : IRecyclerItem, IRecyclerDiff {
    var status: ObservableBoolean
    var childList: List<T>

    /**
     * @param list 확장할 아이템 정보
     * @param adapter 확장 대상의 adapter
     */
    fun toggle(list: List<T>?, adapter: RecyclerView.Adapter<*>?) {
        var i = 0
        if (list is ArrayList) {
            if (!this.status.get()) {
                i = findIndex(list)

                list.addAll(i, childList)
                adapter?.notifyItemRangeInserted(i, childList.size)
            } else {
                if (adapter != null) {
                    i = findIndex(list)
                }

                list.removeAll(childList)
                adapter?.notifyItemRangeRemoved(i, childList.size)
            }
        }

        this.status.set(!status.get())
    }

    private fun findIndex(list: List<T>): Int {
        var i = 0
        if (list is ArrayList) {
            for (it in list) {
                ++i

                if (it == this) {
                    break
                }
            }
        }

        return i
    }
}

/** view holder */
class RecyclerHolder constructor (itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var mBinding: ViewDataBinding
}

/**
 * xml 에서 event 와 data 를 binding 하므로 obtainViewModel 과 출력할 데이터를 내부적으로 알아서 설정 하도록
 * 한다.
 */
class RecyclerAdapter<T: IRecyclerDiff> constructor (
    val mLayouts: Array<Int>
) : RecyclerView.Adapter<RecyclerHolder>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(RecyclerAdapter::class.java)

        private const val METHOD_NAME_VIEW_MODEL = "setModel"
        private const val METHOD_NAME_ITEM       = "setItem"
        private const val METHOD_NAME_BIND       = "bind"
        private const val CLASS_DATA_BINDING     = ".databinding."
        private const val CLASS_BINDING          = "Binding"

        fun invokeMethod(binding: ViewDataBinding, methodName: String, argType: Class<*>, argValue: Any, log: Boolean) {
            try {
                val method = binding.javaClass.getDeclaredMethod(methodName, *arrayOf(argType))
                method.invoke(binding, *arrayOf(argValue))
            } catch (e: Exception) {
                if (log) {
                    e.printStackTrace()
                    mLog.debug("NOT FOUND : ${e.message}")
                }
            }
        }
    }

    var items: ArrayList<T> = arrayListOf()
    lateinit var viewModel: ViewModel
    var viewHolderCallback: ((RecyclerHolder, Int) -> Unit)? = null
    var isScrollToPosition = true
    var isNotifySetChanged = false

    constructor(layoutId: Int) : this(arrayOf(layoutId))

    /**
     * 전달 받은 layout ids 와 IRecyclerItem 을 통해 화면에 출력해야할 layout 를 찾고
     * 해당 layout 의 RecyclerHolder 를 생성 한다.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val layoutId = mLayouts[viewType]
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater,
            layoutId, parent, false)

        val vh = RecyclerHolder(binding.root)
        vh.mBinding = binding

        return vh
    }

    /**
     * view holder 에 view model 과 item 을 설정 시킨다.
     *
     */
    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        viewModel.let { invokeMethod(holder.mBinding, METHOD_NAME_VIEW_MODEL, it.javaClass, it, false) }

        items[position].let { item ->
            when (item) {
                is IRecyclerPosition -> item.position = position
            }

            invokeMethod(holder.mBinding, METHOD_NAME_ITEM, item.javaClass, item, true)
        }

        holder.mBinding.executePendingBindings()
        viewHolderCallback?.invoke(holder, position)
    }

    /**
     * 화면에 출력해야할 아이템의 총 개수를 반환 한다.
     */
    override fun getItemCount() = items.size

    /**
     * 특정 위치의 item 타입을 반환 한다.
     */
    override fun getItemViewType(position: Int): Int {
        when (val item = items.get(position)) {
            is IRecyclerItem -> return item.type
        }

        return 0
    }

    /**
     * 아이템을 설정 한다. 이때 DiffUtil.calculateDiff 를 통해 데이터의 변동 지점을
     * 알아서 찾아 변경 시켜 준다.
     *
     * FIXME [aucd29][2019. 4. 1.]
     * ----
     * 현재 문제점
     * 1. recycler item 에 ObservableBoolean 형태로 CheckBox 를 둔 상태
     * 2. checkbox 를 통해 아이템 하나를 삭제 하면 디비에서 새로운 list 를 생성해서 setItems 을 호출 함
     * 3. DiffUtil 을 통해 해당 위치이 view 를 갱신 시킴
     * 4. 이때 onBindViewHolder 의 setItem 이 호출되지는 않기에 xml 에 item 값이 이전의 데이터를 바라보고 있게 됨
     * 5. 이후 view model 의 데이터를 수정하면 view model 의 item 과 xml 의 item 이 다르기에 원하는 동작을 하지 않음
     * ----
     * 해결 방법?
     * ----
     * -> 임시로 일단 checkbox 를 호출하기 전에 notifyDataSetChanged 를 호출 함 다른 방법이 있는지 찾아봐야할 듯
     * ----
     * 잠시 생각한게 diff util 로 삭제될 데이터 위치와 추가해야할 데이터 위치를 알 수 있으므로 현재 데이터를
     * items = newItems 할게 아니고 items 내에 특정 위치를 삭제 또는 추가한 뒤 inserted 의 경우
     * notifyDataSetChanged(position) 을 호출해주면 부하가 좀 적지 않을까? 라는 생각이 듬
     * 일단 arrayList 로 하는데 이런 구조라면 linked list 가 더 적합할 듯 한 ?
     * ----
     */
    fun setItems(recycler: RecyclerView, list: ArrayList<T>) {
        if (items.size == 0 || isNotifySetChanged) {
            items = list
            notifyDataSetChanged()

            return
        }

        val oldItems = items
        val newItems = if (oldItems.hashCode() == list.hashCode()) { ArrayList(list) } else { list }

        // FIXME 이곳의 구현 방식이 일반적이지 않다라고 들었고 관련 내용을 다시 찾아봄
        // https://blog.kmshack.kr/RecyclerView-DiffUtil%EB%A1%9C-%EC%84%B1%EB%8A%A5-%ED%96%A5%EC%83%81%ED%95%98%EA%B8%B0/
        val result = DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            // 이전 목록 개수 반환
            override fun getOldListSize() = oldItems.size
            // 새로운 목록 개수 반환
            override fun getNewListSize() = newItems.size

            // 두 객체가 같은 항목인지 여부 결정 (레퍼런스 비교가 아님) 이번에 얻은 지식 중 === 이 있음 레퍼런스 비교시 === 으로 비교할 수 있음
            // 인터넷상으로는 객체 자체를 비교하는것과 객체 내부에 id 를 따로 생성해서 비교하는것 이렇게 크게 2가지 형태로
            // 구현되고 있으며 diff util 이 알려지기전 일부 샘플에 객체를 비교하던것에서
            // 현재는 id 를 비교하고 있는게 많다. 그렇다면 IRecyclerDIff 에 id 를 추가해서 해당 값을
            // 비교 하는 형태로 진행할 수도 있다.
            // https://qiita.com/kubode/items/92c1190a6421ba055cc0
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldItems[oldItemPosition].itemSame(newItems[newItemPosition])

            // 두 항목의 데이터가 같은지 비교 (이곳에서 데이터 비교를 위해 IRecycerDiff 인터페이스 이용)
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]

                return oldItem.contentsSame(newItem)
            }

            // https://medium.com/mindorks/diffutils-improving-performance-of-recyclerview-102b254a9e4a
            // areItemsTheSame 이 true 인데, areContentsTheSame 이 false 이면 변경 내용에 대한 페이로드를 가져옴
//            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//                return super.getChangePayload(oldItemPosition, newItemPosition)
//            }
        })

        if (mLog.isDebugEnabled) {
            mLog.debug("OLD ${oldItems.hashCode()}")
            mLog.debug("NEW ${newItems.hashCode()}")
            mLog.debug("DISPATCH UPDATES TO $this")
        }

        // https://stackoverflow.com/questions/43458146/diffutil-in-recycleview-making-it-autoscroll-if-a-new-item-is-added
        result.dispatchUpdatesTo(object: ListUpdateCallback {
            private var mFirstInsert = -1

            // 데이터가 추가되었다면
            override fun onInserted(position: Int, count: Int) {
                if (mFirstInsert == -1 || mFirstInsert > position) {
                    mFirstInsert = position

                    if (isScrollToPosition) {
                        recycler.smoothScrollToPosition(mFirstInsert)
                    }
                }

                if (mLog.isDebugEnabled) {
                    mLog.debug("INSERTED (pos: $position) (cnt: $count)")
                }

                notifyItemRangeInserted(position, count)
            }

            // 데이터가 삭제 되었다면
            override fun onRemoved(position: Int, count: Int) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("REMOVED (pos: $position) (cnt: $count)")
                }

                notifyItemRangeRemoved(position, count)
            }

            // 데이터 위치가 변화 되었다면
            override fun onMoved(fromPosition: Int, toPosition: Int) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("MOVED (from: $fromPosition) (to: $toPosition)")
                }

                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("CHANGED (pos: $position) (cnt: $count)")
                }

                notifyItemRangeChanged(position, count, payload)
            }
        })

        items = newItems
    }
}

/**
 * Recycler View 에 사용될 items 정보와 adapter 를 쉽게 설정하게 만드는 ViewModel
 */
open class RecyclerViewModel<T: IRecyclerDiff> constructor (app: Application)
    : CommandEventViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(RecyclerViewModel::class.java)
    }

    protected var mThreshold   = 1
    protected var mDataLoading = false

    val items           = ObservableField<List<T>>()
    val adapter         = ObservableField<RecyclerAdapter<T>>()
    val itemTouchHelper = ObservableField<ItemTouchHelper>()

    /**
     * adapter 에 사용될 layout 을 설정한다.
     *
     * @param ids 문자열 형태의 layout 이름
     */
    fun initAdapter(vararg ids: Int) {
        val adapter = RecyclerAdapter<T>(ids.toList().toTypedArray())
        adapter.viewModel = this

        this.adapter.set(adapter)
    }

    // https://developer88.tistory.com/102
    // https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf
    // recycler 에서 item 를 long touch 하거나 특정 view 를 drag 했을때 화면 전환을 위한

    fun initItemTouchHelper(callback: ItemMovedCallback, bindingCallback: ((ViewDataBinding) -> View?)? = null) {
        itemTouchHelper.set(ItemTouchHelper(callback))

        bindingCallback?.let {
            adapter.get()?.viewHolderCallback = { holder, _ ->
                it.invoke(holder.mBinding)?.setOnTouchListener { v, ev ->
                    if (ev.action == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.get()?.startDrag(holder)
                    }

                    false
                }
            }
        }
    }

    fun errorLog(e: Throwable) = errorLog(e, mLog)

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // DragCallback
    //
    ////////////////////////////////////////////////////////////////////////////////////

    inner class ItemMovedCallback(val mItemMovedListener:((fromPos: Int, toPos: Int) -> Unit)? = null)
        : ItemTouchHelper.Callback() {
        private var mLongPressDrag = false
        private var mSwipeDrag     = false

//        private var dragFrom = -1
//        private var dragTo   = -1

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags  = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END

            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean {

            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition

            if (mLog.isTraceEnabled) {
                mLog.trace("MOVE RECYCLER ITEM : $fromPos -> $toPos")
            }

//            if (dragFrom == -1) {
//                dragFrom = fromPos
//            }
//            dragTo = toPos

            Collections.swap(items.get(), fromPos, toPos)
            mItemMovedListener?.invoke(fromPos, toPos)
            adapter.get()?.notifyItemMoved(fromPos, toPos)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // swipe 로 삭제 할때
        }

        override fun isLongPressDragEnabled() = mLongPressDrag
        override fun isItemViewSwipeEnabled() = mSwipeDrag

        // https://stackoverflow.com/questions/35920584/android-how-to-catch-drop-action-of-itemtouchhelper-which-is-used-with-recycle
        // 최종적으로 drop 되었을때 디비를 바꿔볼까 싶었는데 이게 0 -> 4 값이 서로 변경되는 형태가 아니므로
        // 불가함을 =_ = 깨닳고 이동 할때마다 변경됨을 mItemMovedListener 를 통해 전달하도록 변경

//        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//            super.clearView(recyclerView, viewHolder)
//
//            if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
//                if (mLog.isDebugEnabled) {
//                    mLog.debug("ITEM MOVED FROM $dragFrom TO $dragTo")
//                }
//
//                mItemMovedListener?.invoke(dragFrom, dragTo)
//            }
//
//            dragFrom = -1
//            dragTo   = -1
//        }
    }

    fun isNextLoad(lastVisiblePos: Int): Boolean {
        if (lastVisiblePos == -1) return false

        return items.get()?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("DataLoading : $mDataLoading")
                mLog.debug("list.size : ${it.size}")
                mLog.debug("lastVisiblePos : $lastVisiblePos")
                mLog.debug("mThreshold : $mThreshold")
                mLog.debug("${it.size - lastVisiblePos <= mThreshold}")
            }

            !mDataLoading && it.size - lastVisiblePos <= mThreshold
        } ?: false
    }
}

open class RecyclerExpandableViewModel<T: IRecyclerExpandable<T>> constructor (app: Application)
    : RecyclerViewModel<T>(app) {

    fun toggle(item: T) {
        item.toggle(items.get(), adapter.get())
    }
}

inline fun <T: IRecyclerExpandable<T>> List<T>.toggleExpandableItems(type: Int,
    targetCallback: (T) -> ObservableBoolean) {

    forEach {
        if (it is IRecyclerItem && it.type == type) {
            it.childList.toggleItems(targetCallback)
        }
    }
}

class InfiniteScrollListener constructor (val callback: (Int) -> Unit) : RecyclerView.OnScrollListener() {
    companion object {
        private val mLog = LoggerFactory.getLogger(InfiniteScrollListener::class.java)
    }

    lateinit var recycler: RecyclerView

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        if (mLog.isDebugEnabled) {
            mLog.debug("SCROLL STATE : $newState")
        }

    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val manager = recycler.layoutManager

        if (mLog.isDebugEnabled) {
            mLog.debug("SCROLLED : $dy")
        }

        if (dy <= 0) {
            return ;
        }

        val lastVisibleItemPosition = if (manager is LinearLayoutManager) {
            manager.findLastVisibleItemPosition()
        } else if (manager is StaggeredGridLayoutManager) {
            val positions = manager.findLastVisibleItemPositions(null)
            var position = positions[0]
            for (i in 1 until positions.size) {
                if (position < positions[i]) {
                    position = positions[i]
                }
            }

            position
        } else { -1 }

        callback.invoke(lastVisibleItemPosition)
    }
}

inline fun StaggeredGridLayoutManager.findLastVisibleItemPosition(): Int {
    val positions = findLastVisibleItemPositions(null)
    var position = positions[0]
    for (i in 1 until positions.size) {
        if (position < positions[i]) {
            position = positions[i]
        }
    }

    return position
}

inline fun RecyclerView.removeItemDecorationAll() {
    val count = itemDecorationCount - 1
    val list = arrayListOf<RecyclerView.ItemDecoration>()
    (0..count).forEach {
        list.add(getItemDecorationAt(it))
    }

    list.forEach(::removeItemDecoration)
}