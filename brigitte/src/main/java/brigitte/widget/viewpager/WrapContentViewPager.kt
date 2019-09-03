package brigitte.widget.viewpager

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 11. <p/>
 */

open class WrapContentViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    // HEIGHT WRAP_CONTENT
    // https://stackoverflow.com/questions/8394681/android-i-am-unable-to-have-viewpager-wrap-content
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val firstChild = getChildAt(0)
        firstChild?.let {
            it.measure(widthMeasureSpec, heightMeasureSpec)

            super.onMeasure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(it.measuredHeight, MeasureSpec.EXACTLY))
        }
    }
}