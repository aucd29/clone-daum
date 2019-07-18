package brigitte.widget

import android.app.Application
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.PagerAdapter
import brigitte.drawable
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-03 <p/>
 */

interface IBannerItem

open class BannerViewModel<T: IBannerItem> constructor(application: Application
) : AndroidViewModel(application) {

    val items               = ObservableField<List<T>>()
    val adapter             = ObservableField<BannerPagerAdapter<T>>()
    val pageChangeCallback  = ObservableField<(Int) -> Unit>()

    fun initAdapter(layout: Int) {
        adapter.set(BannerPagerAdapter(layout, this))
    }

    fun convertColor(str: String) =
        Color.parseColor(str)

    fun convertImage(str: String) =
        drawable(str)
}

class BannerPagerAdapter <T: IBannerItem> (
    private val mLayoutId: Int,
    private val mViewModel: ViewModel
) : PagerAdapter() {

    companion object {
        private val mLog = LoggerFactory.getLogger(BannerPagerAdapter::class.java)

        private const val METHOD_NAME_VIEW_MODEL = "setModel"
        private const val METHOD_NAME_ITEM       = "setItem"

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

    private var mItems: List<T> = arrayListOf()

    fun setBannerItems(items: List<T>) {
        mItems = items
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater   = LayoutInflater.from(container.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater,
            mLayoutId, container, true)

        val item = mItems[position]

        invokeMethod(binding, METHOD_NAME_VIEW_MODEL, mViewModel.javaClass, mViewModel, false)
        invokeMethod(binding, METHOD_NAME_ITEM, item.javaClass, item, true)

        return binding.root
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
    override fun getCount(): Int = mItems.size

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        if (obj is View) {
            container.removeView(obj)
        }
    }
}