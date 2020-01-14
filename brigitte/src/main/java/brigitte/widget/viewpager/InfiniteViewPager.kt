package brigitte.widget.viewpager

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import brigitte.widget.InfinitePagerAdapter
import org.slf4j.LoggerFactory
import java.lang.reflect.Field

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-19 <p/>
 *
 * https://github.com/antonyt/InfiniteViewPager/blob/master/library/src/main/java/com/antonyt/infiniteviewpager/InfinitePagerAdapter.java
 * https://github.com/antonyt/InfiniteViewPager/blob/master/library/src/main/java/com/antonyt/infiniteviewpager/InfiniteViewPager.java
 */

class ViewPagerScroller @JvmOverloads constructor(
    context: Context,
    interpolator: Interpolator? = null,
    flywheel: Boolean = context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.HONEYCOMB
) : Scroller(context, interpolator, flywheel) {
    var userDuration = 1500

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, userDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, duration)
    }
}

fun ViewPager.setViewPagerScroller(scroller: ViewPagerScroller) {
    try {
        val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
        mScroller.isAccessible = true
        mScroller.set(this, scroller)
    } catch (e: NoSuchFieldException) {
    } catch (e: IllegalArgumentException) {
    } catch (e: IllegalAccessException) {
    }
}

class InfiniteViewPager constructor(
    context: Context,
    attr: AttributeSet? = null
) : WrapContentViewPager(context, attr) {
    companion object {
        private val logger = LoggerFactory.getLogger(InfiniteViewPager::class.java)

        const val H_START_SCROLL  = 0

        const val DIRECTION_START = 0
        const val DIRECTION_END   = 1
    }

    private var scrollDelay: Long = 0L
    private var pageChangeListener: SimpleOnPageChangeListener? = null
    private var scroller = ViewPagerScroller(context)
    private var isScroll = false

    var duration: Int
        get() = scroller.duration
        set(value) {
            scroller = ViewPagerScroller(context).apply { userDuration = value }
            setViewPagerScroller(scroller)
        }

    var direction: Int = DIRECTION_START
        get() = field
        set(value) {
            field = value
        }

    init {
        setViewPagerScroller(scroller)
        initPageChangeListener()
    }

    // HANDLER

    private val eventHandler = Handler(Looper.getMainLooper(), ProcessHandler());
    private inner class ProcessHandler : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                H_START_SCROLL -> {
                    if (direction == DIRECTION_START) showNextView() else showPreviewView()
                }
                else -> {}
            }

            return true
        }
    }

    private fun sendMessage(type: Int, delay: Long, any: Any? = null)  = eventHandler.run {
        sendMessageDelayed(obtainMessage().apply {
            what = type
            obj  = any
        }, delay)
    }

    private fun removeMessages(type: Int) = eventHandler.removeMessages(type)

    // HANDLER END

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        setCurrentItem(0)
    }

    override fun setCurrentItem(item: Int) {
        setCurrentItem(0, false)
    }

    // https://stackoverflow.com/questions/7801954/how-to-programmatically-show-next-view-in-viewpager
    fun showNextView() {
        arrowScroll(View.FOCUS_RIGHT)
    }

    fun showPreviewView() {
        arrowScroll(View.FOCUS_LEFT)
    }

    fun startScroll(delay: Long) {
        removeMessages(H_START_SCROLL)

        isScroll    = true
        scrollDelay = delay
        sendMessage(H_START_SCROLL, delay)
    }

    fun stopScroll() {
        isScroll = false
        removeMessages(H_START_SCROLL)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        adapter?.let {
            if (it.count == 0) {
                super.setCurrentItem(item, smoothScroll)
                return
            }

            val newItem = if (item <= 0) {
                offsetAmount() + (item % it.count)
            } else {
                item % it.count
            }

            if (logger.isTraceEnabled) {
                logger.trace("NEW ITEM : $newItem")
            }

            super.setCurrentItem(newItem, smoothScroll)
        } ?: super.setCurrentItem(item, smoothScroll)
    }

    private fun offsetAmount(): Int {
        return adapter?.let {
            if (it is InfinitePagerAdapter) {
                it.realCount * 100
            } else {
                0
            }
        } ?: 0
    }

    private fun initPageChangeListener() {
        if (pageChangeListener == null) {
            pageChangeListener = object: SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    if (logger.isTraceEnabled) {
                        logger.trace("PAGE SELECTED : $position")
                    }

                    if (!isScroll) {
                        return
                    }

                    startScroll(scrollDelay)
                }
            }

            if (logger.isDebugEnabled) {
                logger.debug("ADD PAGE LISTENER $pageChangeListener")
            }

            pageChangeListener?.let { addOnPageChangeListener(it) }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> stopScroll()
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_UP -> startScroll(scrollDelay)
        }

        return super.onTouchEvent(ev)
    }
}
