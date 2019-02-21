package com.example.smartlenskotlin.tf.detector

import android.graphics.Bitmap
import java.io.IOException

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 12. <p/>
 *
 * https://github.com/wadehuang36/tensorflow_mobilenet_android_example
 */

interface TfDetector {
    @Throws(IOException::class)
    fun loadModel(): Boolean
    fun close(): Boolean
    fun recognizeImage(bitmap: Bitmap): List<TfRecognition>
}