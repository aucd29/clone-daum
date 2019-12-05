package brigitte.widget

import android.app.Application
import android.database.DataSetObserver
import android.graphics.Color
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.PagerAdapter
import brigitte.drawable
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-03 <p/>
 */

interface IBannerItem

abstract class IBannerPagerAdapter : PagerAdapter()

open class BannerViewModel<T: IBannerItem> constructor(
    val app: Application
) : ViewModel() {

    val items              = ObservableField<List<T>>()
    val adapter            = ObservableField<IBannerPagerAdapter>()
    val pageChangeCallback = ObservableField<(Int) -> Unit>()
    val disposable         = CompositeDisposable()

    fun initAdapter(layout: Int) {
        adapter.set(BannerPagerAdapter<T>(layout, this))
    }

    fun initInfiniteAdapter(layout: Int, autoScroll: Long = 0) {
        adapter.set(InfinitePagerAdapter(BannerPagerAdapter<T>(layout, this)))
    }

    fun convertColor(str: String) = Color.parseColor(str)
    fun convertImage(str: String) = app.drawable(str)
}

class BannerPagerAdapter <T: IBannerItem> (
    private val mLayoutId: Int,
    private val mViewModel: ViewModel
) : IBannerPagerAdapter() {

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

    fun setBannerItems(items: List<T>, invalidate: Boolean = true) {
        mItems = items

        if (invalidate) {
            notifyDataSetChanged()
        }
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

////////////////////////////////////////////////////////////////////////////////////
//
// InfinitePagerAdapter
//
// https://github.com/antonyt/InfiniteViewPager/blob/master/library/src/main/java/com/antonyt/infiniteviewpager/InfinitePagerAdapter.java
// https://github.com/antonyt/InfiniteViewPager/blob/master/library/src/main/java/com/antonyt/infiniteviewpager/InfiniteViewPager.java
//
////////////////////////////////////////////////////////////////////////////////////

class InfinitePagerAdapter(private val mAdapter: PagerAdapter): IBannerPagerAdapter() {
    companion object {
        private val mLog = LoggerFactory.getLogger(InfinitePagerAdapter::class.java)
    }

    val realCount: Int
        get() = mAdapter.count

    override fun getCount() =
        if (realCount == 0) 0 else Int.MAX_VALUE

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val virtualPos = position % realCount

        if (mLog.isTraceEnabled) {
            mLog.trace("INSTANTIATE ITEM : $virtualPos ($position)")
        }

        return mAdapter.instantiateItem(container, virtualPos)
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val virtualPos = position % realCount

        if (mLog.isTraceEnabled) {
            mLog.trace("DESTROY ITEM : $virtualPos ($position)")
        }

        mAdapter.destroyItem(container, virtualPos, obj)
    }

    fun setBannerItems(items: List<*>) {
        if (mAdapter is BannerPagerAdapter<*>) {
            mAdapter.setBannerItems(items as List<Nothing>, false)
        }

        notifyDataSetChanged()
    }

    override fun finishUpdate(container: ViewGroup) =
        mAdapter.finishUpdate(container)

    override fun isViewFromObject(view: View, obj: Any) =
        mAdapter.isViewFromObject(view, obj)

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) =
        mAdapter.restoreState(state, loader)

    override fun saveState() =
        mAdapter.saveState()

    override fun startUpdate(container: ViewGroup) =
        mAdapter.startUpdate(container)

    override fun getPageTitle(position: Int) =
        mAdapter.getPageTitle(position % realCount)

    override fun getPageWidth(position: Int) =
        mAdapter.getPageWidth(position)

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) =
        mAdapter.setPrimaryItem(container, position, obj)

    override fun unregisterDataSetObserver(observer: DataSetObserver) =
        mAdapter.unregisterDataSetObserver(observer)

    override fun registerDataSetObserver(observer: DataSetObserver) =
        mAdapter.registerDataSetObserver(observer)

    override fun notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged()
        super.notifyDataSetChanged()
    }

    override fun getItemPosition(obj: Any) =
        mAdapter.getItemPosition(obj)
}