package brigitte.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-01-23 <p/>
 *
 * https://developer.android.com/training/animation/vp2-migration
 */

object ViewPager2BindingAdapter {
    private val logger = LoggerFactory.getLogger(ViewPager2BindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindOffscreenPageLimit")
    fun bindOffscreenPageLimit(viewpager: ViewPager2, limit: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("bindOffscreenPageLimit : $limit")
        }

        viewpager.offscreenPageLimit = limit
    }

    @JvmStatic
    @BindingAdapter("bindItems")
    fun <T> bindItems(viewpager: ViewPager2, items: List<T>?) {
        if (items == null) {
            // items 이 없으면 넘어가기
            return
        }

        if (logger.isDebugEnabled) {
            logger.debug("bindItems")
        }

        viewpager.adapter?.let {

        }
    }

//    @JvmStatic
//    @BindingAdapter("bindOrientation")
//    fun bindOrientation(viewpager: ViewPager2, orientation: Int?) {
//        if (orientation == null) {
//            return
//        }
//
//        if (logger.isDebugEnabled) {
//            logger.debug("bindOrientation : $orientation")
//        }
//
//        viewpager.orientation = orientation
//    }

    // view 가 있네 그려
//    @JvmStatic
//    @BindingAdapter("bindPageTransformer")
//    fun bindItemAnimator(viewpager: ViewPager2, transformer: ViewPager2.PageTransformer?) {
//        if (transformer == null) {
//            return
//        }
//
//        if (logger.isDebugEnabled) {
//            logger.debug("bindPageTransformer")
//        }
//
//    }

    @JvmStatic
    @BindingAdapter("bindPageChangeCallback")
    fun bindPageChangeCallback(viewpager: ViewPager2, callback: ((Int) -> Unit)?) {
        if (callback == null) {
            // callback 이 없으면 그냥 넘어가기
            return
        }

        if (logger.isDebugEnabled) {
            logger.debug("bindPageChangeCallback")
        }

        viewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                callback.invoke(position)
            }
        })
    }

    @JvmStatic
    @BindingAdapter("bindCurrentItem", "bindSmoothScroll", requireAll = false)
    fun bindCurrentItem(viewpager: ViewPager2, pos: Int, smoothScroll: Boolean?) {
        if (smoothScroll == null) {
            viewpager.currentItem = pos
        } else {
            viewpager.setCurrentItem(pos, smoothScroll)
        }
    }
}