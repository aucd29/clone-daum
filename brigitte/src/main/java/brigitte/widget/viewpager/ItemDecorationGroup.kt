package brigitte.widget.viewpager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-25 <p/>
 */

class SpaceItemDecoration @JvmOverloads constructor(
    private val mMargin: Rect
): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(mMargin)
    }
}