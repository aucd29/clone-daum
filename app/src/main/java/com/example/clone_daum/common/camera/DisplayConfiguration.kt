package com.example.clone_daum.common.camera

import android.graphics.Rect

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 21. <p/>
 */

class DisplayConfiguration(var rotation: Int, private var viewfinderSize: Size) {
    var previewScalingStrategy: PreviewScalingStrategy = FitCenterStrategy()

    /**
     * @param rotate true to rotate the preview size
     * @return desired preview size in natural camera orientation.
     */
    fun getDesiredPreviewSize(rotate: Boolean): Size {
        return if (rotate) {
            viewfinderSize.rotate()
        } else {
            viewfinderSize
        }
    }

    /**
     * Choose the best preview size, based on our display size.
     *
     * We prefer:
     * 1. no scaling
     * 2. least downscaling
     * 3. least upscaling
     *
     * We do not care much about aspect ratio, since we just crop away extra pixels. We only choose
     * the size to minimize scaling.
     *
     * In the future we may consider choosing the biggest possible preview size, to maximize the
     * resolution we have for decoding. We need more testing to see whether or not that is feasible.
     *
     * @param sizes supported preview sizes, containing at least one size. Sizes are in natural camera orientation.
     * @param isRotated true if the camera is rotated perpendicular to the current display orientation
     * @return the best preview size, never null
     */
    fun getBestPreviewSize(sizes: List<Size>, isRotated: Boolean): Size {
        // Sample of supported preview sizes:
        // http://www.kirill.org/ar/ar.php

        val desired = getDesiredPreviewSize(isRotated)

        return previewScalingStrategy.getBestPreviewSize(sizes, desired)
    }

    /**
     * Scale the preview to cover the viewfinder, then center it.
     *
     * Aspect ratio is preserved.
     *
     * @param previewSize the size of the preview (camera), in current display orientation
     * @return a rect placing the preview
     */
    fun scalePreview(previewSize: Size): Rect {
        return previewScalingStrategy.scalePreview(previewSize, viewfinderSize)
    }
}