package com.example.common

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 6. <p/>
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

class RecyclerAdapter<T: IRecyclerDiff>(val mLayouts: Array<String>): RecyclerView.Adapter<RecyclerHolder>() {
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

    override fun getItemCount() = items.size
    override fun getItemViewType(position: Int): Int {
        val item = items.get(position)
        when (item) {
            is IRecyclerItem -> return item.type()
        }

        return 0
    }

    // 이론상으로는 맞는데 제대로 안도는 현상?
    fun setItems(recycler: RecyclerView, newItems: List<T>) {
        if (items.size == 0) {
            //items.addAll(newItems)
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

            override fun onRemoved(position: Int, count: Int) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("REMOVED (pos: $position) (cnt: $count)")
                }

                notifyItemRangeRemoved(position, count)
            }

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