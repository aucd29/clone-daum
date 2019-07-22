package brigitte.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import brigitte.widget.BannerPagerAdapter
import brigitte.widget.IBannerItem
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object ViewPagerBindingAdapter {
    private val mLog = LoggerFactory.getLogger(ViewPagerBindingAdapter::class.java)
//    @JvmStatic
//    @InverseBindingAdapter(attribute = "currentItem")
//    fun currentItem(viewpager: ViewPager) {
//        viewpager.currentItem
//    }

    @JvmStatic
    @BindingAdapter("bindOffscreenPageLimit")
    fun bindOffscreenPageLimit(viewpager: ViewPager, limit: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindOffscreenPageLimit : $limit")
        }
        viewpager.offscreenPageLimit = limit
    }

    @JvmStatic
    @BindingAdapter("bindPagerAdapter", "bindViewPagerLoaded", requireAll = false)
    fun bindAdapter(viewpager: ViewPager, adapter: PagerAdapter?, viewPagerLoadedCallback: (() -> Unit)? = null) {
        adapter?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("bindPagerAdapter")
            }

            viewpager.adapter = it
            viewPagerLoadedCallback?.invoke()
        }
    }

    @JvmStatic
    @BindingAdapter("bindBannerAdapter", "bindBannerItems", requireAll = false)
    fun <T: IBannerItem> bindBannerItems(viewpager: ViewPager, adapter: BannerPagerAdapter<T>?, items: List<T>?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindBannerItems ${items?.size}")
        }

        try {
            var myadapter: BannerPagerAdapter<T>? = null
            adapter?.let {
                if (viewpager.adapter == null) {
                    myadapter = it
                    viewpager.adapter = myadapter
                } else {
                    myadapter = viewpager.adapter as BannerPagerAdapter<T>
                }
            }

            items?.let {
                myadapter?.setBannerItems(items)
            }
        } catch (e: Exception) {
            if (mLog.isDebugEnabled) {
                e.printStackTrace()
            }

            mLog.error("ERROR: ${e.message}")
        }
    }

    @JvmStatic
    @BindingAdapter("bindPageChangeListener")
    fun bindPageChangeListener(viewpager: ViewPager, listener: ViewPager.OnPageChangeListener?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindPageChangeListener")
        }

        listener?.let { viewpager.addOnPageChangeListener(it) }
    }

    @JvmStatic
    @BindingAdapter("bindPageChangeCallback")
    fun bindPageChangeCallback(viewpager: ViewPager, callback: ((Int) -> Unit)?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("bindPageChangeListener")
        }

        callback?.let {
            viewpager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    it.invoke(position)
                }
            })
        }
    }
}