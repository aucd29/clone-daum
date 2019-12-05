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
    var mDuration = 1500

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, mDuration)
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
        private val mLog = LoggerFactory.getLogger(InfiniteViewPager::class.java)

        const val H_START_SCROLL  = 0

        const val DIRECTION_START = 0
        const val DIRECTION_END   = 1
    }

    private var mScrollDelay: Long = 0L
    private var mPageChangeListener: SimpleOnPageChangeListener? = null
    private var mScroller = ViewPagerScroller(context)
    private var mIsScroll = false

    var duration: Int
        get() = mScroller.mDuration
        set(value) {
            mScroller = ViewPagerScroller(context).apply { mDuration = value }
            setViewPagerScroller(mScroller)
        }

    var direction: Int = DIRECTION_START
        get() = field
        set(value) {
            field = value
        }

    init {
        setViewPagerScroller(mScroller)
        initPageChangeListener()
    }

    // HANDLER

    private val mHandler = Handler(Looper.getMainLooper(), ProcessHandler());
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

    private fun sendMessage(type: Int, delay: Long, any: Any? = null)  = mHandler.run {
        sendMessageDelayed(obtainMessage().apply {
            what = type
            obj  = any
        }, delay)
    }

    private fun removeMessages(type: Int) = mHandler.removeMessages(type)

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

        mIsScroll    = true
        mScrollDelay = delay
        sendMessage(H_START_SCROLL, delay)
    }

    fun stopScroll() {
        mIsScroll = false
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

            if (mLog.isTraceEnabled) {
                mLog.trace("NEW ITEM : $newItem")
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
        if (mPageChangeListener == null) {
            mPageChangeListener = object: SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    if (mLog.isTraceEnabled) {
                        mLog.trace("PAGE SELECTED : $position")
                    }

                    if (!mIsScroll) {
                        return
                    }

                    startScroll(mScrollDelay)
                }
            }

            if (mLog.isDebugEnabled) {
                mLog.debug("ADD PAGE LISTENER $mPageChangeListener")
            }

            mPageChangeListener?.let { addOnPageChangeListener(it) }
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
            MotionEvent.ACTION_UP -> startScroll(mScrollDelay)
        }

        return super.onTouchEvent(ev)
    }
}
