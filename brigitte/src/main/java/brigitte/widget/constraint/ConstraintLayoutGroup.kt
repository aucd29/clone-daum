package brigitte.widget.constraint

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-22 <p/>
 */

class ConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.constraintlayout.widget.ConstraintLayout(context, attrs, defStyleAttr) {
    var dispatchTouchEvent : ((MotionEvent) -> Boolean)? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        dispatchTouchEvent?.let {
            if (it.invoke(ev)) {
                return true
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}