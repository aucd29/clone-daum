package com.example.clone_daum.common.tf.detector

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.Image
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 12. <p/>
 *
 * https://github.com/wadehuang36/tensorflow_mobilenet_android_example
 */

object TfImageHelper {
    const val IMAGE_MEAN = 0
    const val IMAGE_STD  = 255f

    const val kMaxChannelValue = 262143


    fun bitmapToFloat(bitmap: Bitmap): FloatArray {
        val intValues = IntArray(bitmap.width * bitmap.height)
        val result = FloatArray(bitmap.width * bitmap.height * 3)

        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
//        bitmap.getPixel(*values, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var i = 0
        intValues.forEach {
            result[i * 3]     = ((it shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD
            result[i * 3 + 1] = ((it shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD
            result[i * 3 + 2] = ((it and 0xFF) - IMAGE_MEAN) / IMAGE_STD

            ++i
        }

        return result
    }

    /**
     * Convert yuv 420 image to Bitmap, use the same Bitmap to save memory
     *
     * @param image
     * @return
     */
    fun imageToBitmap(image: Image, bitmap: Bitmap) {
        val imageWidth = image.width
        val imageHeight = image.height


        val argb = IntArray(imageWidth * imageHeight)
        val planes = image.planes
        val yuvBytes = arrayOfNulls<ByteArray>(3)

        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer.get(yuvBytes[i])
        }

        val yRowStride    = planes[0].rowStride
        val uvRowStride   = planes[1].rowStride
        val uvPixelStride = planes[1].pixelStride

        convertYUV420ToARGB8888(
            yuvBytes[0],
            yuvBytes[1],
            yuvBytes[2],
            imageWidth,
            imageHeight,
            yRowStride,
            uvRowStride,
            uvPixelStride,
            argb
        )

        bitmap.setPixels(argb, 0, imageWidth, 0, 0, imageWidth, imageHeight)
    }

    /**
     * Returns a transformation matrix from one reference frame into another.
     * Handles cropping (if maintaining aspect ratio is desired) and rotation.
     *
     * @param srcWidth Width of source frame.
     * @param srcHeight Height of source frame.
     * @param dstWidth Width of destination frame.
     * @param dstHeight Height of destination frame.
     * @param applyRotation Amount of rotation to apply from one frame to another.
     * Must be a multiple of 90.
     * @param maintainAspectRatio If true, will ensure that scaling in x and y remains constant,
     * cropping the image if necessary.
     * @return The transformation fulfilling the desired requirements.
     */
    fun getTransformationMatrix(
        srcWidth: Int,
        srcHeight: Int,
        dstWidth: Int,
        dstHeight: Int,
        applyRotation: Int,
        maintainAspectRatio: Boolean
    ): Matrix {
        val matrix = Matrix()

        if (applyRotation != 0) {
            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }

        // Account for the already applied rotation, if any, and then determine how
        // much scaling is needed for each axis.
        val transpose = (abs(applyRotation) + 90) % 180 == 0

        val inWidth = if (transpose) srcHeight else srcWidth
        val inHeight = if (transpose) srcWidth else srcHeight

        // Apply scaling if necessary.
        if (inWidth != dstWidth || inHeight != dstHeight) {
            val scaleFactorX = dstWidth / inWidth.toFloat()
            val scaleFactorY = dstHeight / inHeight.toFloat()

            if (maintainAspectRatio) {
                // Scale by minimum factor so that dst is filled completely while
                // maintaining the aspect ratio. Some image may fall off the edge.
                val scaleFactor = max(scaleFactorX, scaleFactorY)
                matrix.postScale(scaleFactor, scaleFactor)
            } else {
                // Scale exactly to fill dst from src.
                matrix.postScale(scaleFactorX, scaleFactorY)
            }
        }

        if (applyRotation != 0) {
            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }

        return matrix
    }

    private fun convertYUV420ToARGB8888(
        yData: ByteArray?,
        uData: ByteArray?,
        vData: ByteArray?,
        width: Int,
        height: Int,
        yRowStride: Int,
        uvRowStride: Int,
        uvPixelStride: Int,
        out: IntArray
    ) {
        var i = 0
        for (y in 0 until height) {
            val pY = yRowStride * y
            val pUV = uvRowStride * (y shr 1)

            for (x in 0 until width) {
                val uv_offset = pUV + (x shr 1) * uvPixelStride
                out[i++] = YUV2RGB(
                    convertByteToInt(yData, pY + x),
                    convertByteToInt(uData, uv_offset),
                    convertByteToInt(vData, uv_offset)
                )
            }
        }
    }

    private fun convertByteToInt(arr: ByteArray?, pos: Int): Int {
        return arr?.let {
            it[pos].toInt() and 0xFF
        } ?: 0
    }

    private fun YUV2RGB(y: Int, u: Int, v: Int): Int {
        var nY = y
        var nU = u
        var nV = v

        nY -= 16
        nU -= 128
        nV -= 128

        if (nY < 0) nY = 0

        var nR = 1192 * nY + 1634 * nV
        var nG = 1192 * nY - 833 * nV - 400 * nU
        var nB = 1192 * nY + 2066 * nU

        nR = min(kMaxChannelValue, max(0, nR))
        nG = min(kMaxChannelValue, max(0, nG))
        nB = min(kMaxChannelValue, max(0, nB))

        nR = nR shr 10 and 0xff
        nG = nG shr 10 and 0xff
        nB = nB shr 10 and 0xff

        return -0x1000000 or (nR shl 16) or (nG shl 8) or nB
    }
}