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
    private val mMargin: Rect,
    private val mLastMargin: Rect? = null
): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (mLastMargin != null && parent.adapter != null &&
            parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount -1) {
            outRect.set(mLastMargin)
            return
        }

        outRect.set(mMargin)
    }
}

//https://stackoverflow.com/questions/29146781/decorating-recyclerview-with-gridlayoutmanager-to-display-divider-between-item
class GridItemDecoration(private val mSizeGridSpacingPx: Int, private val mGridSize: Int) :
    RecyclerView.ItemDecoration() {

    private var mNeedLeftSpacing = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val frameWidth = ((parent.width - mSizeGridSpacingPx.toFloat() * (mGridSize - 1)) / mGridSize).toInt()
        val padding = parent.width / mGridSize - frameWidth
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        if (itemPosition < mGridSize) {
            outRect.top = 0
        } else {
            outRect.top = mSizeGridSpacingPx
        }
        if (itemPosition % mGridSize == 0) {
            outRect.left = 0
            outRect.right = padding
            mNeedLeftSpacing = true
        } else if ((itemPosition + 1) % mGridSize == 0) {
            mNeedLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        } else if (mNeedLeftSpacing) {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx - padding
            if ((itemPosition + 2) % mGridSize == 0) {
                outRect.right = mSizeGridSpacingPx - padding
            } else {
                outRect.right = mSizeGridSpacingPx / 2
            }
        } else if ((itemPosition + 2) % mGridSize == 0) {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx / 2
            outRect.right = mSizeGridSpacingPx - padding
        } else {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx / 2
            outRect.right = mSizeGridSpacingPx / 2
        }
        outRect.bottom = 0
    }
}

// https://github.com/DhruvamSharma/Recycler-View-Series/blob/master/app/src/main/java/com/dhruvam/recyclerviewseries/data/DividerItemDecoration.java
class OffsetDividerItemDecoration(
    private val mDivider: Drawable, val mOffsetStartDp: Int, val mOffsetEndDp: Int
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

        outRect.top = mDivider.intrinsicHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val dividerStart = mOffsetStartDp.dpToPx(parent.context)
        val dividerEnd = parent.width - mOffsetEndDp.dpToPx(parent.context)

        val count = parent.childCount - 1
        val it = parent.children.iterator()
        var i = 0
        it.forEach {
            if (i == count) {
                return@forEach
            }

            val lp = it.layoutParams as (RecyclerView.LayoutParams)
            val dividerTop = it.bottom + lp.bottomMargin
            val dividerBottom = dividerTop + mDivider.intrinsicHeight

            mDivider.setBounds(dividerStart, dividerTop, dividerEnd, dividerBottom)
            mDivider.draw(c)

            ++i
        }
    }
}

class PositionDividerItemDecoration(
    private val mDivider: Drawable, val positionList: List<Int>
): RecyclerView.ItemDecoration() {
    constructor(context: Context, @DrawableRes resid: Int, positionList: List<Int>)
            : this(ContextCompat.getDrawable(context, resid)!!, positionList)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (positionList.contains(parent.getChildAdapterPosition(view))) {
            outRect.top = mDivider.intrinsicHeight
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
                val dividerBottom = dividerTop + mDivider.intrinsicHeight

                mDivider.setBounds(0, dividerTop, parent.width, dividerBottom)
                mDivider.draw(c)
            }

            ++i
        }
    }
}