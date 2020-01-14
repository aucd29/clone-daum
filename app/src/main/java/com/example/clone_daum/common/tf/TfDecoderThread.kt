package com.example.clone_daum.common.tf

import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.example.clone_daum.common.camera.CameraInstance
import com.example.clone_daum.common.camera.SourceData
import brigitte.validateMainThread
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 20. <p/>
 */

class TfDecoderThread(private val mCameraInstance: CameraInstance
    , private val mDecoder: TfDecoder
    , private val mResultHandler: Handler?) {

    companion object {
        private val logger = LoggerFactory.getLogger(TfDecoderThread::class.java)
    }

    lateinit var cropRect: Rect

    private lateinit var mHandler: Handler
    private lateinit var mThread: HandlerThread

    private val LOCK = Any()
    private var mRunning = false
    private val mCallback = Handler.Callback {
        when (it.what) {
            TfConst.DECODE_DECODING -> decode(it.obj as (SourceData))
        }

        true
    }

//    private val mPreviewCallback = object: PreviewCallback {
//        override fun onPreviewError(e: Exception?) {
//        }
//
//        override fun onPreview(sourceData: SourceData?) {
//            if (mRunning) {
//                sourceData?.let {
//                    mHandler.obtainMessage(TfConst.DECODE_DECODING, it).sendToTarget()
//                }
//            }
//        }
//    }


    fun start() {
        validateMainThread()

        mThread = HandlerThread(javaClass.simpleName)
        mThread.start()

        mHandler = Handler(mThread.looper, mCallback)
        mRunning = true

        requestNextPreview()
    }

    fun stop() {
        validateMainThread()

        synchronized(LOCK) {
            mRunning = false
            mHandler.removeCallbacksAndMessages(null)
            mThread.quit()
        }
    }

    private fun requestNextPreview() {
        mCameraInstance.let {
            if (it.isOpen) {
                it.requestPreview { src ->
                    if (mRunning) {
                        mHandler.obtainMessage(TfConst.DECODE_DECODING, src).sendToTarget()
                    }
                }
            }
        }
    }

    private fun decode(srcData: SourceData) {
        if (logger.isTraceEnabled) {
            logger.trace("DECODE START")
        }

        val start = System.currentTimeMillis()
        srcData.cropRect = cropRect

        val result = mDecoder.decode(srcData)

        mResultHandler?.let {
            Message.obtain(it, if (result != null) {
                val end = System.currentTimeMillis()
                if (logger.isTraceEnabled) {
                    logger.trace("FOUND IN ${(end - start)} ms")
                }

                TfConst.DECODE_SUCCEEDED
            } else {
                TfConst.DECODE_FAILED
            }, result).sendToTarget()
        }

        requestNextPreview()
    }
}
