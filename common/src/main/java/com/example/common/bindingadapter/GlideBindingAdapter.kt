@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common.bindingadapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.common.GlideApp
import com.example.common.R
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 18. <p/>
 *
 * glide app not found
 * http://www.zoftino.com/android-picasso-image-downloading-and-caching-library-tutorial
 *
 * gpu transformation
 * https://github.com/wasabeef/glide-transformations
 *
 * wrap_content not working
 * https://stackoverflow.com/questions/31561474/imageviews-wrap-content-not-working-with-glide
 *
 * display gif & video thumbnail
 * https://futurestud.io/tutorials/glide-displaying-gifs-and-videos
 */

object GlideBindingAdapter {
    private val mLog = LoggerFactory.getLogger(GlideBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("android:src")
    fun imageSource(view: ImageView, @DrawableRes resid: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND IMAGE : $resid")
        }

        view.glide(resid)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun imagePath(view: ImageView, path: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND IMAGE : $path")
        }

        view.glide(path)
    }
}

inline fun File.isVideo(context: Context) = MediaMetadataRetriever().run {
    setDataSource(context, Uri.fromFile(this@isVideo))
    "yes" == extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)
}

inline fun ImageView.glide(@DrawableRes resid: Int) {
    fitxy()

    GlideApp.with(context).load(resid)
//        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .into(this)
}

inline fun ImageView.fitxy() {
    adjustViewBounds = true
    scaleType = ImageView.ScaleType.FIT_XY
}

inline fun ImageView.glide(path: String) {
    fitxy()

    val glide = GlideApp.with(context)
    if (path.startsWith("http")) {
        glide.load(path)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .placeholder(R.drawable.ic_autorenew_black_24dp)
            .into(this)

        return
    }

    val fp = File(path)
    if (fp.isVideo(context)) {
        glide.asBitmap().load(Uri.fromFile(fp)).into(this)
    } else {
        glide.load(fp).into(this)
    }
}