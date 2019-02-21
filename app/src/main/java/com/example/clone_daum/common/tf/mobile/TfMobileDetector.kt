package com.example.clone_daum.common.tf.mobile

import android.content.Context
import android.graphics.Bitmap
import com.example.smartlenskotlin.tf.detector.TfDetector
import com.example.smartlenskotlin.tf.detector.TfImageHelper
import com.example.smartlenskotlin.tf.detector.TfRecognition
import org.tensorflow.Operation
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Float
import java.util.*
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 12. <p/>
 */

class TfMobileDetector constructor(val mContext: Context) : TfDetector {
    companion object {
        val INPUT_SIZE           = 224L

        private val THRESHOLD    = 0.1f
        private val MAX_RESULTS  = 3
        private val MODEL_FILE   = "tensorflow/mobilenet_v1.pb"
        private val LABEL_FILE   = "tensorflow/labels.txt"
        private val CLASS_SIZE   = 1001
        private val INPUT_NAME   = "input"
        private val OUTPUT_NAME  = "MobilenetV1/Predictions/Reshape_1"
        private val OUTPUT_NAMES = arrayOf(OUTPUT_NAME)
    }

    private val mOutputOp: Operation? = null
    private var mTfInterface: TensorFlowInferenceInterface? = null
    private var mLabels: ArrayList<String> = ArrayList()

    init {
        loadModel()
    }

    // Implements TFDetector interfaces
    override fun loadModel(): Boolean {
        this.mTfInterface = TensorFlowInferenceInterface(mContext.assets, MODEL_FILE)

        return if (this.mTfInterface == null) false else initLabels()
    }

    override fun close(): Boolean = true

    override fun recognizeImage(bitmap: Bitmap): List<TfRecognition> {
        return recognizeImage(TfImageHelper.bitmapToFloat(bitmap))
    }

    private fun initLabels(): Boolean {
        val result = true

        try {
            val ism = mContext.assets.open(LABEL_FILE)
            BufferedReader(InputStreamReader(ism)).use {
                mLabels.addAll(it.readLines())
            }
        } catch (e: IOException) {
            throw RuntimeException("Problem reading label file!", e)
        }

        return result
    }

    private fun recognizeImage(imageFloats: FloatArray): List<TfRecognition> {
        val recognitions = ArrayList<TfRecognition>()

        mTfInterface?.let {
            it.feed(INPUT_NAME, imageFloats, 1L, INPUT_SIZE, INPUT_SIZE, 3L)
            it.run(OUTPUT_NAMES, false)

            val outputs = FloatArray(CLASS_SIZE)
            it.fetch(OUTPUT_NAME, outputs)

            val pq = PriorityQueue<TfRecognition>(3, Comparator<TfRecognition> { lhs, rhs ->
                    Float.compare(rhs.confidence!!, lhs.confidence!!) })

            for (i in outputs.indices) {
                if (outputs[i] > THRESHOLD) {
                    pq.add(TfRecognition("$i", mLabels[i], outputs[i], null))
                }
            }

            val recognitionsSize = Math.min(pq.size, MAX_RESULTS)
            for (i in 0 until recognitionsSize) {
                recognitions.add(pq.poll())
            }
        }

        return recognitions
    }
}
