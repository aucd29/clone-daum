package com.example.clone_daum.common.tf

import android.content.Context
import android.graphics.Bitmap
import com.example.clone_daum.common.camera.SourceData
import com.example.clone_daum.common.tf.detector.TfRecognition
import com.example.clone_daum.common.tf.mobile.TfMobileDetector

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 20. <p/>
 */

class TfDecoder constructor(val context: Context) {
    private val mTfDetector = TfMobileDetector(context)

    companion object {
        const val INPUT_SIZE = 224
    }

    fun decode(src: SourceData): List<TfRecognition> {
        val cropedBmp = Bitmap.createScaledBitmap(src.bitmap, INPUT_SIZE, INPUT_SIZE, true)

        return mTfDetector.recognizeImage(cropedBmp)
    }
}