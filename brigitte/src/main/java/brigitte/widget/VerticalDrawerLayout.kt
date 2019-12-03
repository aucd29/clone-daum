package brigitte.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import org.slf4j.LoggerFactory
import kotlin.math.abs

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 26. <p/>
 *
 * https://stackoverflow.com/questions/34136178/swiperefreshlayout-blocking-horizontally-scrolled-recyclerview
 */

open class VerticalDrawerLayout(
    context: Context,
    attrs: AttributeSet
) : DrawerLayout(context, attrs) {
    companion object {
        private val mLog = LoggerFactory.getLogger(VerticalDrawerLayout::class.java)

        const val START = 0
        const val END   = 1
    }

    private var touchSlop: Int
    private var prevX: Float = 0f
    private var decliend: Boolean = false

    var touchSlopDirection = START

    init {
        this.initLayout()
        touchSlop = (ViewConfiguration.get(context).scaledTouchSlop * 1.3F).toInt()
    }

    open fun initLayout() {

    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                prevX = MotionEvent.obtain(ev).x
                decliend = false
            }
            MotionEvent.ACTION_MOVE -> {
                val evX = ev.x
                val xDiff = abs(evX - prevX)
                val direction = evX - prevX
                var ignoreTouchSlop = false

                if (touchSlopDirection == START) {
                    if (direction > 0) {
                        ignoreTouchSlop = true
                    }
                } else if (touchSlopDirection == END) {
                    if (direction < 0) {
                        ignoreTouchSlop = true
                    }
                }

                if (!ignoreTouchSlop) {
                    if (decliend || xDiff > touchSlop) {
                        decliend = true

                        if (mLog.isTraceEnabled) {
                            mLog.trace("INGORE TOUCH EVENT ${xDiff}")
                        }

                        // 터치 이벤트 무시
                        return false
                    }
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }
}