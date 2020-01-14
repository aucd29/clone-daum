package brigitte.widget.viewpager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import brigitte.dpToPx

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-25 <p/>
 */

class SpaceItemDecoration constructor(
    private val margin: Rect,
    private val lastMargin: Rect? = null
): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (lastMargin != null && parent.adapter != null &&
            parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount -1) {
            outRect.set(lastMargin)
            return
        }

        outRect.set(margin)
    }
}

//https://stackoverflow.com/questions/29146781/decorating-recyclerview-with-gridlayoutmanager-to-display-divider-between-item
class GridItemDecoration(private val sizeGridSpacingPx: Int, private val gridSize: Int) :
    RecyclerView.ItemDecoration() {

    private var needLeftSpacing = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val frameWidth = ((parent.width - sizeGridSpacingPx.toFloat() * (gridSize - 1)) / gridSize).toInt()
        val padding = parent.width / gridSize - frameWidth
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        if (itemPosition < gridSize) {
            outRect.top = 0
        } else {
            outRect.top = sizeGridSpacingPx
        }
        if (itemPosition % gridSize == 0) {
            outRect.left = 0
            outRect.right = padding
            needLeftSpacing = true
        } else if ((itemPosition + 1) % gridSize == 0) {
            needLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        } else if (needLeftSpacing) {
            needLeftSpacing = false
            outRect.left = sizeGridSpacingPx - padding
            if ((itemPosition + 2) % gridSize == 0) {
                outRect.right = sizeGridSpacingPx - padding
            } else {
                outRect.right = sizeGridSpacingPx / 2
            }
        } else if ((itemPosition + 2) % gridSize == 0) {
            needLeftSpacing = false
            outRect.left = sizeGridSpacingPx / 2
            outRect.right = sizeGridSpacingPx - padding
        } else {
            needLeftSpacing = false
            outRect.left = sizeGridSpacingPx / 2
            outRect.right = sizeGridSpacingPx / 2
        }
        outRect.bottom = 0
    }
}

// https://github.com/DhruvamSharma/Recycler-View-Series/blob/master/app/src/main/java/com/dhruvam/recyclerviewseries/data/DividerItemDecoration.java
class OffsetDividerItemDecoration(
    private val divider: Drawable, val offsetStartDp: Int, val offsetEndDp: Int
): RecyclerView.ItemDecoration() {
    constructor(context: Context, @DrawableRes resid: Int, offsetDp: Int)
        : this(ContextCompat.getDrawable(context, resid)!!, offsetDp, offsetDp)

    constructor(context: Context, @DrawableRes resid: Int, offsetStartDp: Int, offsetEndDp: Int)
            : this(ContextCompat.getDrawable(context, resid)!!, offsetStartDp, offsetEndDp)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) {
            return
        }

        outRect.top = divider.intrinsicHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val dividerStart = offsetStartDp.dpToPx(parent.context)
        val dividerEnd = parent.width - offsetEndDp.dpToPx(parent.context)

        val count = parent.childCount - 1
        val it = parent.children.iterator()
        var i = 0
        it.forEach {
            if (i == count) {
                return@forEach
            }

            val lp = it.layoutParams as (RecyclerView.LayoutParams)
            val dividerTop = it.bottom + lp.bottomMargin
            val dividerBottom = dividerTop + divider.intrinsicHeight

            divider.setBounds(dividerStart, dividerTop, dividerEnd, dividerBottom)
            divider.draw(c)

            ++i
        }
    }
}

class PositionDividerItemDecoration(
    private val divider: Drawable, val positionList: List<Int>
): RecyclerView.ItemDecoration() {
    constructor(context: Context, @DrawableRes resid: Int, positionList: List<Int>)
            : this(ContextCompat.getDrawable(context, resid)!!, positionList)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (positionList.contains(parent.getChildAdapterPosition(view))) {
            outRect.top = divider.intrinsicHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val count = parent.childCount - 1
        val it = parent.children.iterator()
        var i = 0
        it.forEach {
            if (i == count) {
                return@forEach
            }

            if (positionList.contains(i)) {
                val lp = it.layoutParams as (RecyclerView.LayoutParams)
                val dividerTop = it.bottom + lp.bottomMargin
                val dividerBottom = dividerTop + divider.intrinsicHeight

                divider.setBounds(0, dividerTop, parent.width, dividerBottom)
                divider.draw(c)
            }

            ++i
        }
    }
}