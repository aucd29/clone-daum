@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.bindingadapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import brigitte.GlideApp
import brigitte.R
import brigitte.dpToPx
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 18. <p/>
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
    fun imageSrc(view: ImageView, @DrawableRes resid: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND IMAGE : $resid")
        }

        view.setImageResource(resid)
    }

    @JvmStatic
    @BindingAdapter("bindImageRes")
    fun glideSource(view: ImageView, @DrawableRes resid: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND IMAGE : $resid")
        }

        view.glide(resid)
    }

    @JvmStatic
    @BindingAdapter("bindImage", "bindImageThumbnail",
        "bindImageWidth", "bindImageHeight",
        "bindRoundedCorners", "bindCircleCrop",
        requireAll = false)
    fun glideImage(view: ImageView, path: String?, thumbnail: String?,
                   x: Int?, y: Int?,
                   roundedCorners: Int?, circleCrop: Boolean?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND IMAGE : $path THUMBNAIL : $thumbnail")
        }

        if (path == null) {
            return
        }

        view.glide(path, thumbnail, x, y, roundedCorners, circleCrop)
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

@SuppressLint("CheckResult")
inline fun ImageView.glide(path: String, thumbnail: String?,
                           x: Int?, y: Int?,
                           roundedCorners: Int?, circleCrop: Boolean?) {
    fitxy()

    val progress = CircularProgressDrawable(context)
    progress.apply {
        strokeWidth     = 3f.dpToPx(context)
        centerRadius    = 20f.dpToPx(context)
        alpha = 50
        setColorSchemeColors(0xffffffff.toInt())

        start()
    }

    val glide = GlideApp.with(context)
    if (path.startsWith("http")) {
        val request = glide.load(path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(progress)
            .error(R.drawable.ic_error_outline_black_24dp)
            .transition(DrawableTransitionOptions.withCrossFade())

        thumbnail?.let { request.thumbnail(Glide.with(context).load(it)) }

        // https://github.com/wasabeef/glide-transformations
        roundedCorners?.let { request.transform(CenterCrop(), RoundedCorners(it)) }
        circleCrop?.let {
            if (it) {
                request.apply(RequestOptions.circleCropTransform())
            }
        }

        // https://stackoverflow.com/questions/36652134/android-glide-load-picture-file-apply-overlay-and-set-to-imageview

        if (x != null && y != null && x > 0 && y > 0) {
            request.override(x, y)
        }

        request.into(this)

        return
    } else if (path.startsWith("drawable://")) {
        val strId = path.replace("drawable://", "")
        val id = context.resources.getIdentifier(strId, "drawable", context.packageName)
        if (id == -1) {
            return
        }

        val request = glide.load(id)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_error_outline_black_24dp)
            .transition(DrawableTransitionOptions.withCrossFade())

        roundedCorners?.let { request.transform(CenterCrop(), RoundedCorners(it)) }
        circleCrop?.let {
            if (it) { request.apply(RequestOptions.circleCropTransform()) }
        }

        if (x != null && y != null && x > 0 && y > 0) {
            request.override(x, y)
        }

        request.into(this)

        return
    }

    val fp = File(path)
    if (fp.isVideo(context)) {
        glide.asBitmap().load(Uri.fromFile(fp))
            .into(this)
    } else {
        val request = glide.load(fp)
            .transition(DrawableTransitionOptions.withCrossFade())

        roundedCorners?.let { request.transform(CenterCrop(), RoundedCorners(it))}
        circleCrop?.let { if (it) { request.apply(RequestOptions.circleCropTransform()) }}

        // 로컬 파일을 이럴 필요는 없나?
//        thumbnail?.let { request.thumbnail(Glide.with(context).load(it)) }

        request.into(this)
    }
}