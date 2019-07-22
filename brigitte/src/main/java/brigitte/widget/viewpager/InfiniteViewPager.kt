package brigitte.widget.viewpager

import android.content.Context
import android.os.Handler
import android.os.Looper
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
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-19 <p/>
 *
 * https://github.com/antonyt/InfiniteViewPager/blob/master/library/src/main/java/com/antonyt/infiniteviewpager/InfinitePagerAdapter.java
 * https://github.com/antonyt/InfiniteViewPager/blob/master/library/src/main/java/com/antonyt/infiniteviewpager/InfiniteViewPager.java
 */

class ViewPagerScroller: Scroller {
    var myDuration = 1500

    constructor(context: Context): super(context)
    constructor(context: Context, interpolator: Interpolator): super(context, interpolator)
    constructor(context: Context, interpolator: Interpolator, flywheel: Boolean) : super(context, interpolator, flywheel)

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, myDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, myDuration)
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

class InfiniteViewPager : WrapContentViewPager {
    companion object {
        private val mLog = LoggerFactory.getLogger(InfiniteViewPager::class.java)
    }

    constructor(context: Context): super(context)
    constructor(context: Context, attr: AttributeSet): super(context, attr)

    private var mActionDown = false
    private var mAutoScrollDelay: Long = 0L
    private var mHandler = Handler(Looper.getMainLooper())
    private var mScrollRunnable: Runnable? = null
    private var mPageChangeListener: SimpleOnPageChangeListener? = null

    override fun initLayout() {
        setViewPagerScroller(ViewPagerScroller(context))
    }

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

            if (mLog.isDebugEnabled) {
                mLog.debug("NEW ITEM : $newItem")
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

    fun runAutoScroll(delay: Long) {
        mAutoScrollDelay = delay

        if (mAutoScrollDelay == 0L) {
            mPageChangeListener?.let { removeOnPageChangeListener(it) }
            mPageChangeListener = null
        } else {
            if (mPageChangeListener == null) {
                mPageChangeListener = object: SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        if (mActionDown) {
                            mActionDown = false
                            return
                        }

                        doAutoScroll()
                    }
                }

                mPageChangeListener?.let { addOnPageChangeListener(it) }
            }
        }

        doAutoScroll()
    }

    @Synchronized
    private fun doAutoScroll() {
        freeRunnable()

        if (mAutoScrollDelay == 0L) {
            return
        }

        mScrollRunnable = Runnable { showNextView() }
        mHandler.postDelayed(mScrollRunnable, mAutoScrollDelay)
    }

    fun stopAutoScroll() = freeRunnable()

    @Synchronized
    private fun freeRunnable() {
        mScrollRunnable?.let {
            mHandler.removeCallbacks(it)
            mScrollRunnable = null
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                mActionDown = true
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_UP -> {
                if (mScrollRunnable == null) {
                    doAutoScroll()
                }
            }
        }

        return super.onTouchEvent(ev)
    }
}
