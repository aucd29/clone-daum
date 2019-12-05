package brigitte.widget.viewpager

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.MotionEventCompat

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-09 <p/>
 *
 * https://gogorchg.tistory.com/entry/Android-Viewpager-swipe-disable
 */


class NoSwipeViewPager @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : WrapContentViewPager(context, attr) {
    var mSwipe: Boolean = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (mSwipe) {
            return super.onInterceptTouchEvent(ev)
        }

        if (MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_MOVE) {
            // IGNORE
        } else {
            if (super.onInterceptTouchEvent(ev)) {
                super.onTouchEvent(ev)
            }
        }

        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (mSwipe) {
            return super.onTouchEvent(ev)
        }

        return MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_MOVE && super.onTouchEvent(ev)
    }
}