package com.example.clone_daum.common.camera

import android.graphics.Rect
import com.journeyapps.barcodescanner.Size
import java.util.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 19. <p/>
 */

//abstract class PreviewScalingStrategy {
//    fun getBestPreviewSize(sizes: List<Size>, desired: Size): Size {
//        val ordered = getBestPreviewOrder(sizes, desired)
//
//        return ordered.get(0)
//    }
//
//    fun getBestPreviewOrder(sizes: List<Size>, desired: Size?): List<Size> {
//        if (desired == null) {
//            return sizes
//        }
//
//        Collections.sort(sizes) { a, b ->
//            score(a, desired).compareTo(score(b, desired))
//        }
//
//        return sizes
//    }
//
//    open protected fun score(size: Size, desired: Size): Float = 0.5f
//
//    abstract fun scalePreview(previewSize: Size, viewfinderSize: Size): Rect
//}
//
//////////////////////////////////////////////////////////////////////////////////////
////
//// CenterCropStrategy
////
//////////////////////////////////////////////////////////////////////////////////////
//
//class CenterCropStrategy: PreviewScalingStrategy() {
//    override fun score(size: Size, desired: Size): Float {
//        if (size.width <= 0 || size.height <= 0) {
//            return 0f
//        }
//
//        val scaled = size.scaleCrop(desired)
//        val scaleRatio = scaled.width * 1f / size.width
//
//        val scaleScore = if (scaleRatio > 1f) {
//            Math.pow((1f / scaleRatio).toDouble(), 1.1).toFloat()
//        } else {
//            scaleRatio
//        }
//
//        val cropRatio = scaled.width * 1f / desired.width + scaled.height * 1f / desired.height
//        val cropScore = 1f / cropRatio / cropRatio
//
//        return scaleScore * cropScore
//    }
//
//    override fun scalePreview(previewSize: Size, viewfinderSize: Size): Rect {
//        val scaledPreview = previewSize.scaleCrop(viewfinderSize)
//
//        val dx = (scaledPreview.width - viewfinderSize.width) / 2
//        val dy = (scaledPreview.height - viewfinderSize.height) / 2
//
//        return Rect(-dx, -dy, scaledPreview.width - dx, scaledPreview.height - dy)
//    }
//}
//
//////////////////////////////////////////////////////////////////////////////////////
////
//// FitCenterStrategy
////
//////////////////////////////////////////////////////////////////////////////////////
//
//class FitCenterStrategy: PreviewScalingStrategy() {
//    override fun score(size: Size, desired: Size): Float {
//        if (size.width <= 0 || size.height <= 0) {
//            return 0f
//        }
//
//        val scaled = size.scaleFit(desired)
//        val scaleRatio = scaled.width * 1f / size.width
//
//        val scaleScore = if (scaleRatio > 1f) {
//            Math.pow(1f / scaleRatio.toDouble(), 1.1).toFloat()
//        } else {
//            scaleRatio
//        }
//
//        val cropRatio = (desired.width * 1f / scaled.width) * (desired.height * 1f / scaled.height)
//        val cropScore = 1f / cropRatio / cropRatio / cropRatio
//
//        return scaleScore * cropRatio
//    }
//
//    override fun scalePreview(previewSize: Size, viewfinderSize: Size): Rect {
//        val scaledPreview = previewSize.scaleFit(viewfinderSize)
//
//        val dx = (scaledPreview.width - viewfinderSize.width) / 2
//        val dy = (scaledPreview.height - viewfinderSize.height) / 2
//
//        return Rect(-dx, -dy, scaledPreview.width - dx, scaledPreview.height - dy)
//    }
//}
//
//////////////////////////////////////////////////////////////////////////////////////
////
////
////
//////////////////////////////////////////////////////////////////////////////////////
//
//
//class FitXYStrategy: PreviewScalingStrategy() {
//    companion object {
//        fun absRatio(ratio: Float) = if (ratio < 1f) 1f / ratio else ratio
//    }
//
//    override fun score(size: Size, desired: Size): Float {
//        if (size.width <= 0 || size.height <= 0) {
//            return 0f
//        }
//
//        val x = absRatio(size.width * 1f / desired.width)
//        val y = absRatio(size.height * 1f / desired.height)
//
//        val scaleScore = 1f / x / y
//        val distortion = absRatio((1f * size.width / size.height) /
//                (1f * desired.width / desired.height))
//
//        // distortion is bad
//        val distortionScore = 1f / distortion / distortion / distortion
//
//        return scaleScore * distortionScore
//    }
//
//    override fun scalePreview(previewSize: Size, viewfinderSize: Size): Rect {
//        return Rect(0, 0, viewfinderSize.width, viewfinderSize.height)
//    }
//}