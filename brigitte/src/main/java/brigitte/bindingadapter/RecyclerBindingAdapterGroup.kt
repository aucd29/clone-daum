@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.bindingadapter

import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.*
import brigitte.IRecyclerDiff
import brigitte.InfiniteScrollListener
import brigitte.RecyclerAdapter
import brigitte.removeItemDecorationAll
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 6. <p/>
 */

object RecyclerBindingAdapter {
    private val mLog = LoggerFactory.getLogger(RecyclerBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindAdapter", "bindItems")
    fun <T: IRecyclerDiff> bindAdapter(recycler: RecyclerView, adapter: RecyclerAdapter<T>?,
                                       items: List<T>?) {
        if (adapter == null || items == null) {
            return
        }

        var myadapter: RecyclerAdapter<T>? = null

        adapter.let {
            if (recycler.adapter == null) {
                myadapter = it
                recycler.adapter = myadapter
            } else {
                myadapter = recycler.adapter as RecyclerAdapter<T>

                if (myadapter !== it) {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("CHANGED ADAPTER")
                    }

                    myadapter = it
                    recycler.adapter = it
                }
            }
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("BIND ADAPTER ($myadapter, ${items.run { hashCode() }}), ITEM COUNT (${items.count()})")
        }

        items.let {
            if (it is ArrayList<T>) {
                myadapter?.setItems(recycler, it)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindItemTouchHelper")
    fun bindDragCallback(recycler: RecyclerView, helper: ItemTouchHelper?) {
        helper?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("BIND ITEM TOUCH HELPER (${recycler.id})")
            }

            it.attachToRecyclerView(recycler)
        }
    }

    /**
     *  <?xml version="1.0" encoding="utf-8"?>
        <shape xmlns:android="http://schemas.android.com/apk/res/android"
        android:shape="rectangle">
        <size android:height="1dp" />
        <solid android:color="#ff992900" />
        </shape>

    <?xml version="1.0" encoding="utf-8"?>
    <shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
        <size
        android:width="1dp"
        android:height="1dp" />

        <solid android:color="@color/dark_gray" />
    </shape>
     */
    @JvmStatic
    @BindingAdapter("bindHorDecoration", "bindVerDecoration", requireAll = false)
    fun bindDecoration(recycler: RecyclerView, horDrawable: Int? = null, verDrawable: Int? = null) {
        //https://stackoverflow.com/questions/31242812/how-can-a-divider-line-be-added-in-an-android-recyclerview
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND DECORATION: hor($horDrawable), ver($verDrawable)")
        }

        recycler.removeItemDecorationAll()
        recycler.apply {
            horDrawable?.let { decorator(it) }
            verDrawable?.let { decorator(it, DividerItemDecoration.VERTICAL) }
        }
    }

    @JvmStatic
    @BindingAdapter("bindItemDecoration")
    fun bindItemDecoration(recycler: RecyclerView, decoration: RecyclerView.ItemDecoration? = null) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND ITEM DECORATION")
        }

        recycler.removeItemDecorationAll()
        decoration?.let { recycler.addItemDecoration(it) }
    }

    @JvmStatic
    @BindingAdapter("bindItemDecorations")
    fun bindItemDecorations(recycler: RecyclerView, decorations: Array<RecyclerView.ItemDecoration>? = null) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND ITEM DECORATION")
        }

        recycler.removeItemDecorationAll()
        decorations?.let { it.forEach { recycler.addItemDecoration(it) } }
    }

    @JvmStatic
    @BindingAdapter("bindGridLayoutManager")
    fun bindGridLayoutManager(recycler: RecyclerView, spancount: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND GRID LAYOUT: SPAN COUNT($spancount)")
        }

        recycler.layoutManager = GridLayoutManager(recycler.context, spancount)
    }

    @JvmStatic
    @BindingAdapter("bindStaggeredVerticalGridLayoutManager")
    fun bindStaggeredGridLayoutManager(recycler: RecyclerView, spancount: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND STAGGERED GRID LAYOUT: SPAN COUNT($spancount)")
        }

        recycler.layoutManager = StaggeredGridLayoutManager(spancount, StaggeredGridLayoutManager.VERTICAL)
    }

    @JvmStatic
    @BindingAdapter("bindLockedGridLayoutManager")
    fun bindLockedGridLayoutManager(recycler: RecyclerView, spancount: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND LOCKED GRID LAYOUT: SPAN COUNT($spancount)")
        }

        recycler.layoutManager = object: GridLayoutManager(recycler.context, spancount) {
            override fun canScrollVertically() = false
        }

        ViewCompat.setNestedScrollingEnabled(recycler, false)
    }

    @JvmStatic
    @BindingAdapter("bindLayoutManager")
    fun bindLayoutManager(recycler: RecyclerView, manager: RecyclerView.LayoutManager?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND LAYOUT MANAGER")
        }

        manager?.let { recycler.layoutManager = it }
    }

    @JvmStatic
    @BindingAdapter("bindItemAnimator")
    fun bindLayoutManager(recycler: RecyclerView, animator: RecyclerView.ItemAnimator?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND ITEM ANIMATOR")
        }

        recycler.itemAnimator = animator
    }

    @JvmStatic
    @BindingAdapter("bindSmoothToPosition")
    fun bindSmoothToPosition(recycler: RecyclerView, position: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND SMOOTH TO POSITION $position")
        }

        recycler.smoothScrollToPosition(position)
    }

    @JvmStatic
    @BindingAdapter("bindInfiniteScrollListener")
    fun bindInfiniteScrollListener(recycler: RecyclerView, listener: InfiniteScrollListener?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND INFINITE SCROLL LISTENER")
        }

        listener?.let {
            it.recycler = recycler
            recycler.addOnScrollListener(it)
        }
    }
}

inline fun RecyclerView.decorator(@DrawableRes drawable: Int, type: Int = DividerItemDecoration.HORIZONTAL) {
    ContextCompat.getDrawable(context, drawable)?.let {
        DividerItemDecoration(context, type).let { divider ->
            divider.setDrawable(it)
            addItemDecoration(divider)
        }
    }
}

//inline fun ImageView.picasso(@DrawableRes resid: Int, @DrawableRes holder: Int = R.drawable.ic_autorenew_black_24dp) =
//    Picasso.get().load(resid).into(this)
//
//inline fun ImageView.picasso(fp: File, @DrawableRes holder: Int = R.drawable.ic_autorenew_black_24dp) =
//    Picasso.get().load(fp).into(this)
//
//inline fun ImageView.picassoVideo(path: String, @DrawableRes holder: Int = R.drawable.ic_autorenew_black_24dp) {
//    Picasso.Builder(context).addRequestHandler(VideoRequestHandler()).build()
//        .load("${VideoRequestHandler.VIDEO_SCHEME}:$path")
//        .placeholder(holder).into(this)
//}

//
//class VideoRequestHandler: RequestHandler() {
//    companion object {
//        const val VIDEO_SCHEME = "video"
//    }
//
//    override fun canHandleRequest(data: Request?) =
//        VIDEO_SCHEME == data?.uri?.scheme ?: false
//
//    override fun load(request: Request?, networkPolicy: Int) = request?.let {
//        val bm = ThumbnailUtils.createVideoThumbnail(it.uri.path,
//            MediaStore.Images.Thumbnails.FULL_SCREEN_KIND)
//
//        Result(bm, Picasso.LoadedFrom.DISK)
//    }
//}