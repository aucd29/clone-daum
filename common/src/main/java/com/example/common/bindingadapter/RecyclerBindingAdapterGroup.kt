@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common.bindingadapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.IRecyclerDiff
import com.example.common.R
import com.example.common.RecyclerAdapter
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 6. <p/>
 */

object ImageBindingAdapter {
    private val mLog = LoggerFactory.getLogger(ImageBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("android:src")
    fun imageSource(view: ImageView, resid: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND IMAGE : ${view} = ${resid}")
        }

        view.picasso(resid)
//        view.setImageResource(resid)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun imagePath(view: ImageView, path: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND IAMGE : $path")
        }

        val fp = File(path)
        if (!fp.exists()) {
            mLog.error("ERROR: FILE NOT FOUND($path)")
            return
        }

        if (fp.isVideo(view.context)) {
            view.picassoVideo(path)
        } else {
            view.picasso(fp)
        }
    }
}

object RecyclerBindingAdapter {
    private val mLog = LoggerFactory.getLogger(RecyclerBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter(*arrayOf("bindAdapter", "bindItems"))
    fun <T: IRecyclerDiff> bindAdapter(recycler: RecyclerView, adapter: RecyclerAdapter<T>,
                                       items: java.util.ArrayList<T>?) {
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
    @BindingAdapter(*arrayOf("bindHorDecoration", "bindVerDecoration"))
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
}

inline fun RecyclerView.decorator(@DrawableRes drawable: Int, type: Int = DividerItemDecoration.HORIZONTAL) {
    ContextCompat.getDrawable(context, drawable)?.let {
        DividerItemDecoration(context, type).let { divider ->
            divider.setDrawable(it)
            addItemDecoration(divider)
        }
    }
}

inline fun ImageView.picasso(@DrawableRes resid: Int, @DrawableRes holder: Int = R.drawable.ic_autorenew_black_24dp) =
    Picasso.get().load(resid).placeholder(holder).into(this)

inline fun ImageView.picasso(fp: File, @DrawableRes holder: Int = R.drawable.ic_autorenew_black_24dp) =
    Picasso.get().load(fp).placeholder(holder).into(this)

inline fun ImageView.picassoVideo(path: String, @DrawableRes holder: Int = R.drawable.ic_autorenew_black_24dp) {
    Picasso.Builder(context).addRequestHandler(VideoRequestHandler()).build()
        .load("${VideoRequestHandler.VIDEO_SCHEME}:$path")
        .placeholder(holder).into(this)
}

inline fun File.isVideo(context: Context) = MediaMetadataRetriever().run {
    setDataSource(context, Uri.fromFile(this@isVideo))
    "yes" == extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)
}

class VideoRequestHandler: RequestHandler() {
    companion object {
        const val VIDEO_SCHEME = "video"
    }

    override fun canHandleRequest(data: Request?) =
        VIDEO_SCHEME == data?.uri?.scheme ?: false

    override fun load(request: Request?, networkPolicy: Int) = request?.let {
        val bm = ThumbnailUtils.createVideoThumbnail(it.uri.path,
            MediaStore.Images.Thumbnails.FULL_SCREEN_KIND)

        RequestHandler.Result(bm, Picasso.LoadedFrom.DISK)
    }
}