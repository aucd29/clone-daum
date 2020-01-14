package com.example.clone_daum.common.camera

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import brigitte.validateMainThread
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 18. <p/>
 */

class CameraInstance constructor(val context: Context) {
    companion object {
        private val logger = LoggerFactory.getLogger(CameraInstance::class.java)
    }

    private var mThread: CameraThread? = null
    private var mManager: CameraManager? = null

    private var mOpen = false
    var cameraSetting = CameraSettings()
        set (cameraSetting) {
            if (!mOpen) {
                field = cameraSetting
                mManager?.settings = cameraSetting
            }
        }

    lateinit var surface: CameraSurface

    var displayConfig: DisplayConfiguration? = null
    var readyHandler: Handler? = null

    val isOpen: Boolean
        get() = mOpen

    val mOpener = Runnable {
        try {
            if (logger.isDebugEnabled) {
                logger.debug("Opening camera")
            }

            mManager?.open()
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                e.printStackTrace()
            }

            notifyError(e)
            logger.error("ERROR: Failed to open camera ${e.message}")
        }
    }

    val mConfigure = Runnable {
        try {
            if (logger.isDebugEnabled) {
                logger.debug("Configuring camera")
            }

            mManager?.let {
                it.configure()

                readyHandler?.obtainMessage(CameraConst.K_PREVIEW_SIZE_READY,
                    it.getPreviewSize())?.sendToTarget()
            }
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                e.printStackTrace()
            }

            notifyError(e)
            logger.error("ERROR: Failed to configure camera ${e.message}")
        }
    }

    val mPreviewStarter = Runnable {
        if (logger.isDebugEnabled) {
            logger.debug("STATING PREVIEW")
        }

        try {
            mManager?.apply {
                setPreviewDisplay(surface)
                startPreview()
            }
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                e.printStackTrace()
            }

            notifyError(e)

            logger.error("ERROR: Failed to start preview ${e.message}")
        }
    }

    val mCloser = Runnable {
        try {
            mManager?.apply {
                stopPreview()
                close()
            }
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                e.printStackTrace()
            }

            logger.error("ERROR: Failed to close camera ${e.message}")
        }

        mThread?.decrementInstance()
    }

    init {
        validateMainThread()

        mThread  = CameraThread.get
        mManager = CameraManager(context)
        mManager?.settings = cameraSetting
    }

    fun displayConfig(config: DisplayConfiguration) {
        displayConfig = config
        mManager?.displayConfig = config
    }

    // FIXME
//    fun surfaceHolder(holder: SurfaceHolder) {
//        surface = CameraSurface(holder)
//    }

//    fun cameraSettings(settings: CameraSettings) {
//        if (!mOpen) {
//            mCameraSetting = settings
//            mManager?.settings = settings
//        }
//    }

    fun previewSize() = mManager?.getPreviewSize()

    fun cameraRotation() = mManager?.rotationDegrees

    fun open() {
        validateMainThread()

        if (logger.isDebugEnabled) {
            logger.debug("CAMERA INSTANCE OPEN")
        }

        mOpen = true
        mThread?.incrementAndEnqueue(mOpener)
    }

    fun configureCamera() {
        validateMainThread()
        validateOpen()

        mThread?.enqueue(mConfigure)
    }

    fun startPreview() {
        validateMainThread()
        validateOpen()

        if (logger.isDebugEnabled) {
            logger.debug("START PREVIEW")
        }

        mThread?.enqueue(mPreviewStarter)
    }

    fun setTorch(on: Boolean) {
        validateMainThread()

        if (mOpen) {
            mThread?.enqueue(Runnable { mManager?.setTorch(on) })
        }
    }

    fun close() {
        validateMainThread()

        if (logger.isDebugEnabled) {
            logger.debug("CAMERA INSTANCE CLOSE")
        }

        if (mOpen) {
            mThread?.enqueue(mCloser)
        }

        mOpen = false
    }

    fun requestPreview(callback: (SourceData) -> Unit) {
        validateOpen()

        if (logger.isTraceEnabled) {
            logger.trace("REQUEST PREVIEW")
        }

        mThread?.enqueue(Runnable { mManager?.requestPreviewFrame(callback) })
    }

    private fun validateOpen() {
        if (!mOpen) {
            throw IllegalStateException("CameraInstance is not open")
        }
    }

    fun notifyError(e: Exception) {
        readyHandler?.obtainMessage(CameraConst.K_CAMERA_ERROR, e)?.sendToTarget()
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

internal class CameraThread {
    private object Holder { val INSTANCE = CameraThread() }

    companion object {
        val get: CameraThread by lazy { Holder.INSTANCE }
    }

    private var mHandler: Handler? = null
    private var mThread: HandlerThread? = null
    private var mOpenCount = 0

    private val LOCK = Any()

    fun enqueue(runnable: Runnable) {
        synchronized(LOCK) {
            checkRunning()
            mHandler?.post(runnable)
        }
    }

    fun enqueueDelayed(runnable: Runnable, delayMillis: Long) {
        synchronized(LOCK) {
            checkRunning()
            mHandler?.postDelayed(runnable, delayMillis)
        }
    }

    private fun checkRunning() {
        synchronized(LOCK) {
            if (mHandler == null) {
                if (mOpenCount <= 0) {
                    throw IllegalStateException("CameraThread is not open")
                }

                mThread = HandlerThread("camera-thread")
                mThread?.start()
                mHandler = Handler(mThread?.looper)
            }
        }
    }

    private fun quit() {
        synchronized(LOCK) {
            mThread?.quit()
            mThread = null
            mHandler = null
        }
    }

    fun decrementInstance() {
        synchronized(LOCK) {
            if (--mOpenCount == 0) {
                quit()
            }
        }
    }

    fun incrementAndEnqueue(runnable: Runnable) {
        synchronized(LOCK) {
            ++mOpenCount
            enqueue(runnable)
        }
    }
}
