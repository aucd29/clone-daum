@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common.bindingadapter

import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.IRecyclerDiff
import com.example.common.RecyclerAdapter
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 6. <p/>
 */

//object ImageBindingAdapter {
//    private val mLog = LoggerFactory.getLogger(ImageBindingAdapter::class.java)
//
//    @JvmStatic
//    @BindingAdapter("android:src")
//    fun imagePath(view: ImageView, path: String) {
//        if (mLog.isDebugEnabled) {
//            mLog.debug("BIND IAMGE : $path")
//        }
//
//        val fp = File(path)
//        if (!fp.exists()) {
//            mLog.error("ERROR: FILE NOT FOUND($path)")
//            return
//        }
//
//        if (fp.isVideo(view.context)) {
//            view.picassoVideo(path)
//        } else {
//            view.picasso(fp)
//        }
//    }
//}

object RecyclerBindingAdapter {
    private val mLog = LoggerFactory.getLogger(RecyclerBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter(*["bindAdapter", "bindItems"])
    fun <T: IRecyclerDiff> bindAdapter(recycler: RecyclerView, adapter: RecyclerAdapter<T>,
                                       items: List<T>?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND ADAPTER (${recycler.id}), ITEM COUNT (${items?.count()})")
        }

        val myadapter: RecyclerAdapter<T>
        if (recycler.adapter == null) {
            myadapter = adapter
            recycler.adapter = myadapter
        } else {
            myadapter = recycler.adapter as RecyclerAdapter<T>
        }

        items?.let {
            myadapter.setItems(recycler, it)
        }
    }

    @JvmStatic
    @BindingAdapter(*["bindHorDecoration", "bindVerDecoration"])
    fun bindDecorator(recycler: RecyclerView, horDrawable: Int, verDrawable: Int) {
        //https://stackoverflow.com/questions/31242812/how-can-a-divider-line-be-added-in-an-android-recyclerview
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND DECORATION: hor($horDrawable), ver($verDrawable)")
        }

        recycler.run {
            decorator(horDrawable)
            decorator(verDrawable, DividerItemDecoration.VERTICAL)
        }
    }

    @JvmStatic
    @BindingAdapter("bindLockedGridLayoutManager")
    fun bindLockedGridLayoutManager(recycler: RecyclerView, spancount: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND GRID LAYOUT: SPAN COUNT($spancount)")
        }

        recycler.layoutManager = object: GridLayoutManager(recycler.context, spancount) {
            override fun canScrollVertically() = false
        }

        ViewCompat.setNestedScrollingEnabled(recycler, false)
    }

    @JvmStatic
    @BindingAdapter("bindLayoutManager")
    fun bindLayoutManager(recycler: RecyclerView, manager: RecyclerView.LayoutManager) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND LAYOUT MANAGER")
        }

        recycler.layoutManager = manager
    }

    @JvmStatic
    @BindingAdapter("bindItemAnimator")
    fun bindLayoutManager(recycler: RecyclerView, animator: RecyclerView.ItemAnimator?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND ITEM ANIMATOR")
        }

        recycler.itemAnimator = animator
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
//
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