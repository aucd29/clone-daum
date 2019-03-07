package com.example.common

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 6. <p/>
 */

/** item 비교 인터페이스 */
interface IRecyclerDiff {
    fun compare(item: IRecyclerDiff): Boolean
}

/** Item 타입 비교 인터페이스 */
interface IRecyclerItem {
    fun type(): Int
}

/** 아이템 위치 정보 인터페이스 */
interface IRecyclerPosition {
    fun position(pos: Int)
    fun position(): Int
}

/** view holder */
class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var mBinding: ViewDataBinding
}

/**
 * xml 에서 event 와 data 를 binding 하므로 viewModel 과 출력할 데이터를 내부적으로 알아서 설정 하도록
 * 한다.
 *
 *
 */
class RecyclerAdapter<T: IRecyclerDiff>(val mLayouts: Array<String>)
    : RecyclerView.Adapter<RecyclerHolder>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(RecyclerAdapter::class.java)

        const private val METHOD_NAME_VIEW_MODEL = "setModel"
        const private val METHOD_NAME_ITEM       = "setItem"
        const private val METHOD_NAME_BIND       = "bind"

        fun bindingClassName(context: Context, layoutId: String): String {
            var classPath = context.packageName
            classPath += ".databinding."
            classPath += Character.toUpperCase(layoutId[0])

            var i = 1
            while (i < layoutId.length) {
                var c = layoutId[i]

                if (c == '_') {
                    c = layoutId[++i]
                    classPath += Character.toUpperCase(c)
                } else {
                    classPath += c
                }

                ++i
            }

            classPath += "Binding"

            return classPath
        }

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

    var items: List<T> = arrayListOf()
    lateinit var viewModel: ViewModel

    constructor(layoutId: String) : this(arrayOf(layoutId)) {
    }

    /**
     * 전달 받은 layout ids 와 IRecyclerItem 을 통해 화면에 출력해야할 layout 를 찾고
     * 해당 layout 의 RecyclerHolder 를 생성 한다.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val context = parent.context
        val layoutId = context.resources.getIdentifier(mLayouts.get(viewType), "layout", context.packageName)
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)

        if (mLog.isDebugEnabled) {
            mLog.debug("LAYOUT ID : ${mLayouts.get(viewType)} (${layoutId})")
        }

        val classPath = bindingClassName(context, mLayouts.get(viewType))

        if (mLog.isTraceEnabled()) {
            mLog.trace("BINDING CLASS ${classPath}")
        }

        val bindingClass = Class.forName(classPath)
        val method = bindingClass.getDeclaredMethod(METHOD_NAME_BIND, *arrayOf(View::class.java))
        val binding = method.invoke(null, *arrayOf(view)) as ViewDataBinding
        val vh = RecyclerHolder(view)
        vh.mBinding = binding

        return vh
    }

    /**
     * view holder 에 view model 과 item 을 설정 시킨다.
     */
    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        viewModel.let { invokeMethod(holder.mBinding, METHOD_NAME_VIEW_MODEL, it.javaClass, it, false) }

        items.let {
            it.get(position).let { item ->
                when (item) {
                    is IRecyclerPosition -> item.position(position)
                }

                invokeMethod(holder.mBinding, METHOD_NAME_ITEM, item.javaClass, item, true)
            }
        }

        holder.mBinding.executePendingBindings()
    }

    /**
     * 화면에 출력해야할 아이템의 총 개수를 반환 한다.
     */
    override fun getItemCount() = items.size

    /**
     * 특정 위치의 item 타입을 반환 한다.
     */
    override fun getItemViewType(position: Int): Int {
        val item = items.get(position)
        when (item) {
            is IRecyclerItem -> return item.type()
        }

        return 0
    }

    /**
     * 아이템을 설정 한다. 이때 DiffUtil.calculateDiff 를 통해 데이터의 변동 지점을
     * 알아서 찾아 변경 시켜 준다.
     */
    fun setItems(recycler: RecyclerView, newItems: List<T>) {
        if (items.size == 0) {
            items = newItems
            notifyItemRangeChanged(0, items.size)

            return
        }

        val oldItems = items
        val result = DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            override fun getOldListSize() = oldItems.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldItems[oldItemPosition] == newItems[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]

                oldItem.compare(newItem)

                return oldItem == newItem
            }
        })

        // https://stackoverflow.com/questions/43458146/diffutil-in-recycleview-making-it-autoscroll-if-a-new-item-is-added
        result.dispatchUpdatesTo(object: ListUpdateCallback {
            private var mFirstInsert = -1

            // 데이터가 추가되었다면
            override fun onInserted(position: Int, count: Int) {
                if (mFirstInsert == -1 || mFirstInsert > position) {
                    mFirstInsert = position
                    recycler.smoothScrollToPosition(mFirstInsert)
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
open class RecyclerViewModel<T: IRecyclerDiff>(app: Application): AndroidViewModel(app) {
    val items   = ObservableField<List<T>>()
    val adapter = ObservableField<RecyclerAdapter<T>>()

    fun initAdapter(id: String) {
        val adapter = RecyclerAdapter<T>(id)
        adapter.viewModel = this

        this.adapter.set(adapter)
    }

    fun initAdapter(ids: Array<String>) {
        val adapter = RecyclerAdapter<T>(ids)
        adapter.viewModel = this

        this.adapter.set(adapter)
    }
}