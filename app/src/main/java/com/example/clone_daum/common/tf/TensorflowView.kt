package com.example.clone_daum.common.tf

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import com.example.clone_daum.common.camera.CameraPreview
import com.example.smartlenskotlin.tf.detector.TfRecognition

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 20. <p/>
 */

class TensorflowView: CameraPreview {
    var mResultCallback: ((List<TfRecognition>?) -> Unit)? = null

    private lateinit var mDecodeHandler: Handler
    private var mDecoderThread: TfDecoderThread? = null
    private val mDecodeCallback = Handler.Callback {
        var value = false

        when (it.what) {
            TfConst.DECODE_SUCCEEDED -> {
                mResultCallback?.invoke(it.obj as List<TfRecognition>)
                value = true
            }
            TfConst.DECODE_FAILED -> {
                value = true
            }
        }

        value
    }

    constructor(context: Context) : super(context) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initLayout()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.initLayout()
    }

    fun initLayout() {
        mDecodeHandler = Handler(mDecodeCallback)
    }

    private fun createDecoder(): TfDecoder {
        return TfDecoder(context)
    }

    fun stopDecoding() {
        mResultCallback = null
        stopDecoderThread()
    }

    private fun startDecoderThread() {
        stopDecoderThread()

        if (isPreviewActive) {
            mCameraInstance?.let {
                mDecoderThread = TfDecoderThread(it, createDecoder(), mDecodeHandler).apply {
                    cropRect = previewFramingRect!!
                    start()
                }
            }
        }
    }

    override fun previewStarted() {
        super.previewStarted()

        startDecoderThread()
    }

    private fun stopDecoderThread() {
        mDecoderThread?.stop()
        mDecoderThread = null
    }

    override fun pause() {
        stopDecoderThread()

        super.pause()
    }
}