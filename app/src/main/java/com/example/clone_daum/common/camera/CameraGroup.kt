package com.example.clone_daum.common.camera

import android.content.Context
import android.hardware.Camera
import android.view.Surface
import org.slf4j.LoggerFactory
import java.io.IOException
import java.lang.RuntimeException

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 15. <p/>
 */

class CameraManager constructor(val mContext: Context) {
    companion object {
        private val mLog = LoggerFactory.getLogger(CameraManager::class.java)

        fun getPreviewSizes(params: Camera.Parameters): List<Size> {
            val rawSupportedSizes = params.supportedPreviewSizes
            val previewSizes = arrayListOf<Size>()

            if (rawSupportedSizes == null) {
                val defaultSize = params.previewSize
                if (defaultSize != null) {
                    previewSizes.add(Size(defaultSize.width, defaultSize.height))
                }

                return previewSizes
            }

            rawSupportedSizes.forEach { previewSizes.add(Size(it.width, it.height)) }

            return previewSizes
        }
    }

    private var mCamera: Camera? = null
    private lateinit var mCameraInfo: Camera.CameraInfo

    private var mAutoFocusManager: AutoFocusManager? = null
//    private var mAmbientLightManager: AmbientLightManager? = null

    private var mPreviewing: Boolean = false
    private var mDefaultParams: String = ""

    var settings = CameraSettings()
    lateinit var displayConfig: DisplayConfiguration

    private var mRequestedPreviewSize: Size? = null
    private lateinit var mPreviewSize: Size

    var rotationDegrees = -1

    inner class CameraPreviewCallback : Camera.PreviewCallback {
        var callback: ((SourceData) -> Unit)? = null
        var resolution: Size? = null

        override fun onPreviewFrame(data: ByteArray, camera: Camera) {
            if (resolution == null || callback == null) {
                mLog.error("ERROR: Got preview callback, but no handler or resolution available")
                return
            }

            resolution?.let {
                val format = camera.parameters.previewFormat
                val src = SourceData(data, it.width, it.height, format, rotationDegrees)

                callback?.invoke(src)
            }
        }
    }

    private val mCameraPreviewCallback = CameraPreviewCallback()


    fun open() {
        mCamera = OpenCameraInterface.open(settings.requestedCameraId)
        if (mCamera == null) {
            throw RuntimeException("Failed to open camera")
        }

        val id = OpenCameraInterface.getCameraId(settings.requestedCameraId)
        mCameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(id, mCameraInfo)
    }

    fun configure() {
        if (mCamera == null) {
            throw RuntimeException("Camera not open")
        }

        setParameters()
    }

    @Throws(IOException::class)
    fun setPreviewDisplay(surface: CameraSurface) {
        mCamera?.let {
            surface.setPreview(it)
        }
    }

    fun startPreview() {
        mCamera?.let {
            if (!mPreviewing) {
                it.startPreview()

                mPreviewing          = true
                mAutoFocusManager    = AutoFocusManager(it, settings)
//                mAmbientLightManager = AmbientLightManager(mContext, this, settings)
//                mAmbientLightManager?.start()
            }
        }
    }

    fun stopPreview() {
        mAutoFocusManager?.stop()
        mAutoFocusManager = null

//        mAmbientLightManager?.stop()
//        mAmbientLightManager = null

        mCamera?.let {
            if (mPreviewing) {
                it.stopPreview()
                mCameraPreviewCallback.callback = null
                mPreviewing = false
            }
        }
    }

    fun close() {
        mCamera?.release()
        mCamera = null
    }

    fun isCameraRotated(): Boolean {
        if (rotationDegrees == -1) {
            throw IllegalStateException("Rotation not calculated yet. Call configure() first.")
        }

        return rotationDegrees % 180 != 0
    }

    private fun getDefaultCameraParameters(): Camera.Parameters {
        if (mCamera == null) {
            throw RuntimeException("Camera not open")
        }

        val params = mCamera!!.parameters
        if (mDefaultParams == null) {
            mDefaultParams = params.flatten()
        } else {
            params.unflatten(mDefaultParams)
        }

        return params
    }

    private fun setDesiredParameters(safeMode: Boolean) {
        val params = getDefaultCameraParameters()

        if (mLog.isInfoEnabled) {
            mLog.info("INIT CAMERA PARAMS : ${params.flatten()}")
        }

        if (safeMode) {
            if (mLog.isInfoEnabled) {
                mLog.info("IN CAMERA CONFIG SAFE MODE -- MOST SETTINGS WILL NOT BE HONORED")
            }
        }

        CameraConfigurationUtils.setFocus(params, settings.focusMode, safeMode)

        if (!safeMode) {
            CameraConfigurationUtils.setTorch(params, false)

            if (settings.isScanInverted) {
                CameraConfigurationUtils.setInvertColor(params)
            }

            if (settings.isMeteringEnabled) {
                CameraConfigurationUtils.setVideoStabilization(params)
                CameraConfigurationUtils.setFocusArea(params)
                CameraConfigurationUtils.setMetering(params)
            }
        }

        val previewSize = getPreviewSizes(params)
        if (previewSize.size == 0) {
            mRequestedPreviewSize = null
        } else {
            mRequestedPreviewSize = displayConfig.getBestPreviewSize(previewSize, isCameraRotated())
            mRequestedPreviewSize?.let {
                params.setPreviewSize(it.width, it.height)
            }
        }

        mCamera!!.parameters = params
    }

    private fun desiredParameters(safeMode: Boolean) {
        val params = getDefaultCameraParameters()
        if (params == null) {
            if (mLog.isInfoEnabled) {
                mLog.info("Device error: no camera parameters are available. Proceeding without configuration.")
            }

            return
        }

        if (mLog.isInfoEnabled) {
            mLog.info("Initial camera parameters: ${params.flatten()}")
        }

        if (safeMode) {
            if (mLog.isInfoEnabled) {
                mLog.info("In camera config safe mode -- most settings will not be honored")
            }
        }

        CameraConfigurationUtils.setFocus(params, settings.focusMode, safeMode)

        if (!safeMode) {
            settings.let {
                if (it.isScanInverted) {
                    CameraConfigurationUtils.setInvertColor(params)
                }

                if (it.isMeteringEnabled) {
                    CameraConfigurationUtils.setVideoStabilization(params)
                    CameraConfigurationUtils.setFocusArea(params)
                    CameraConfigurationUtils.setMetering(params)
                }
            }
        }

        val previewSizes = getPreviewSizes(params)
        if (previewSizes.size == 0) {
            mRequestedPreviewSize = null
        } else {
            mRequestedPreviewSize = displayConfig.getBestPreviewSize(previewSizes, isCameraRotated())
            mRequestedPreviewSize?.let {
                params.setPreviewSize(it.width, it.height)
            }
        }

        if (mLog.isInfoEnabled) {
            mLog.info("Final camera parameters: ${params.flatten()}")
        }

        mCamera?.parameters = params
    }

    private fun calculateDisplayRotation(): Int {
        if (mCamera == null) {
            throw RuntimeException("Camera not open")
        }

        val rotation = displayConfig.rotation
        var degrees = 0

        when (rotation) {
            Surface.ROTATION_0   -> degrees = 0
            Surface.ROTATION_90  -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + degrees) % 360
            result = (360 - result) % 360   // compensate the mirror
        } else {    // back facing
            result = (mCameraInfo.orientation - degrees + 360) % 360
        }

        if (mLog.isInfoEnabled) {
            mLog.info("Camera Display Orientation: $result")
        }

        return result
    }

    private fun setCameraDisplayOrientation(rotation: Int) {
        mCamera?.setDisplayOrientation(rotation)
    }

    private fun setParameters() {
        try {
            rotationDegrees = calculateDisplayRotation()
            setCameraDisplayOrientation(rotationDegrees)
        } catch (e: Exception) {
            if (mLog.isDebugEnabled) {
                e.printStackTrace()
            }

            mLog.error("ERROR: Failed to set rotation.")
        }

        try {
            setDesiredParameters(false)
        } catch (e: Exception) {
            if (mLog.isDebugEnabled) {
                e.printStackTrace()
            }

            try {
                setDesiredParameters(true);
            } catch (e2: java.lang.Exception) {
                if (mLog.isDebugEnabled) {
                    e2.printStackTrace()
                }
                mLog.error("ERROR: Camera rejected even safe-mode parameters! No configuration")
            }
        }

        if (mCamera == null) {
            throw RuntimeException("Camera not open")
        }

        val realPreviewSize = mCamera?.parameters?.previewSize
        if (realPreviewSize == null) {
            mPreviewSize = mRequestedPreviewSize!!
        } else {
            mPreviewSize = Size(realPreviewSize.width, realPreviewSize.height)
        }

        mCameraPreviewCallback.resolution = mPreviewSize
    }

    fun getPreviewSize(): Size? {
        if (mPreviewSize == null) {
            return null
        } else if (isCameraRotated()) {
            return mPreviewSize.rotate()
        } else {
            return mPreviewSize
        }
    }

    fun requestPreviewFrame(callback: (SourceData) -> Unit) {
        mCamera?.let {
            if (mPreviewing) {
                mCameraPreviewCallback.callback = callback
                it.setOneShotPreviewCallback(mCameraPreviewCallback)
            }
        }
    }

    fun setTorch(on: Boolean) {
        mCamera?.let {
            val isOn = isTorchOn()

            if (on != isOn) {
                mAutoFocusManager?.stop()
                mCamera?.parameters?.let { params ->
                    CameraConfigurationUtils.setTorch(params, on)

                    if (settings.isExposureEnabled) {
                        CameraConfigurationUtils.setBestExposure(params, on)
                    }

                    it.parameters = params
                }

                mAutoFocusManager?.start()
            }
        }
    }

    fun isTorchOn(): Boolean {
        val params = mCamera?.parameters
        if (params == null) {
            return false
        }

        return params.flashMode.run {
             (Camera.Parameters.FLASH_MODE_ON.equals(this@run) ||
                    Camera.Parameters.FLASH_MODE_TORCH.equals(this@run))
        }
    }
}
