package com.example.clone_daum.common.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.AttributeSet
import android.view.TextureView
import android.view.ViewGroup
import android.view.WindowManager
import brigitte.systemService
import brigitte.validateMainThread
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.math.min

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 18. <p/>
 */

open class CameraPreview: ViewGroup {
    companion object {
        private val mLog = LoggerFactory.getLogger(CameraPreview::class.java)

        const val ROTATION_LISTENER_DELAY_MS = 250L
    }

    var mCameraInstance: CameraInstance? = null
    lateinit var mWindowManager: WindowManager
    var mStateHandler: Handler? = null
    lateinit var mTextureView: TextureView

    private var previewActive = false
    val isPreviewActive
        get() = previewActive

    var mRotationListener: RotationListener? = null
    var openedOrientation = -1

    val mStateListeners = arrayListOf<PreviewStateListener>()

    lateinit var mDisplayConfig: DisplayConfiguration
    var mCameraSettings: CameraSettings = CameraSettings()

    var containerSize: Size? = null
    var previewSize: Size? = null
    var surfaceRect: Rect? = null

    var currentSurfaceSize: Size? = null

    var framingRect: Rect? = null
    var previewFramingRect: Rect? = null
    var framingRectSize: Size? = null

    var marginFraction: Double = 0.1

    private var previewScalingStrategy: PreviewScalingStrategy? = null

    var isTorchOn = false
        set(on) {
            field = on
            mCameraInstance?.setTorch(on)
        }

    fun surfaceTextureListener(): TextureView.SurfaceTextureListener {
        return object: TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                onSurfaceTextureSizeChanged(surface, width, height)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                currentSurfaceSize = Size(width, height)
                startPreviewIfReady()
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?) = false
        }
    }

    val mStateCallback = Handler.Callback { msg ->
        when (msg.what) {
            CameraConst.K_PREVIEW_SIZE_READY ->
                previewSized(msg.obj as Size)
        }

        false
    }


    constructor(context: Context) : super(context) {
        this.initLayout(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initLayout(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.initLayout(attrs)
    }

    fun initLayout(attrs: AttributeSet?) {
        if (background == null) {
            setBackgroundColor(Color.BLACK)
        }

        attrs?.let { initAttr(it) }

        if (mLog.isDebugEnabled) {
            mLog.debug("INIT LAYOUT")
        }

        mWindowManager    = context.systemService<WindowManager>()!!
        mStateHandler     = Handler(mStateCallback)
        mRotationListener = RotationListener()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setupTextureView()
    }

    fun initAttr(attrs: AttributeSet) {
        // TODO 일단 attr 이름을 정한 뒤에 생성해야 함

        //framingRectSize = Size(-1, -1)
        previewScalingStrategy = CenterCropStrategy()

        postDelayed(::resume, 500)
    }

    private fun rotationChanged() {
        if (isActive() && displayRotation() != openedOrientation) {
            pause()
            resume()
        }
    }

    private fun setupTextureView() {
        mTextureView = TextureView(context)
        mTextureView.surfaceTextureListener = surfaceTextureListener()

        addView(mTextureView)
    }

    val mFireState = object: PreviewStateListener {
        override fun sized() {
            mStateListeners.forEach { it.sized() }
        }

        override fun started() {
            mStateListeners.forEach { it.started() }
        }

        override fun stopped() {
            mStateListeners.forEach { it.stopped() }
        }

        override fun error(e: Exception) {
            mStateListeners.forEach { it.error(e) }
        }
    }

    private fun calculateFrames() {
        if (containerSize == null || previewSize == null || mDisplayConfig == null) {
            previewFramingRect = null
            framingRect = null
            surfaceRect = null

            throw IllegalStateException("containerSize or previewSize is not set yet")
        }

        val pw = previewSize!!.width
        val ph = previewSize!!.height

        val w = containerSize!!.width
        val h = containerSize!!.height

        surfaceRect = mDisplayConfig.scalePreview(previewSize!!)
        surfaceRect?.let {
            val container = Rect(0, 0, w, h)
            framingRect = calculateFramingRect(container, it)
            val frameInPreview = Rect(framingRect)

            frameInPreview.offset(it.left * -1, it.top * -1)

            val l = frameInPreview.left * pw / it.width()
            val t = frameInPreview.top * ph / it.height()
            val r = frameInPreview.right * pw / it.width()
            val b = frameInPreview.bottom * ph / it.height()

            previewFramingRect = Rect(l, t, r, b)
            if (previewFramingRect!!.width() <= 0 || previewFramingRect!!.height() <= 0) {
                previewFramingRect = null
                framingRect        = null

                if (mLog.isInfoEnabled) {
                    mLog.info("PREVIEW FRAME IS TOO SMALL")
                }
            } else {
                mFireState.sized()
            }
        }
    }

    private fun containerSized(containerSize: Size) {
        this.containerSize = containerSize
        mCameraInstance?.let {
            if (it.displayConfig == null) {
                mDisplayConfig = DisplayConfiguration(displayRotation(), containerSize)
                mDisplayConfig.previewScalingStrategy = previewScalingStrategy()

                it.displayConfig(mDisplayConfig)
                it.configureCamera()

                if (isTorchOn) {
                    it.setTorch(true)
                }
            }
        }
    }

    fun previewScalingStrategy(): PreviewScalingStrategy {
        if (previewScalingStrategy != null) {
            return previewScalingStrategy!!
        }

        if (mTextureView != null) {
            return CenterCropStrategy()
        } else {
            return FitCenterStrategy()
        }
    }

    private fun previewSized(size: Size) {
        previewSize = size

        if (containerSize != null) {
            calculateFrames()
            requestLayout()
            startPreviewIfReady()
        }
    }

    protected fun calculateTextureTransform(textureSize: Size, previewSize: Size): Matrix {
        val ratioTexture = textureSize.width.toFloat() / textureSize.height.toFloat()
        val ratioPreview = previewSize.width.toFloat() / previewSize.height.toFloat()

        val scaleX: Float
        val scaleY: Float

        // We scale so that either width or height fits exactly in the TextureView, and the other
        // is bigger (cropped).
        if (ratioTexture < ratioPreview) {
            scaleX = ratioPreview / ratioTexture
            scaleY = 1f
        } else {
            scaleX = 1f
            scaleY = ratioTexture / ratioPreview
        }

        val matrix = Matrix()

        matrix.setScale(scaleX, scaleY)

        // Center the preview
        val scaledWidth = textureSize.width * scaleX
        val scaledHeight = textureSize.height * scaleY
        val dx = (textureSize.width - scaledWidth) / 2
        val dy = (textureSize.height - scaledHeight) / 2

        // Perform the translation on the scaled preview
        matrix.postTranslate(dx, dy)

        return matrix
    }

    private fun startPreviewIfReady() {
        if (currentSurfaceSize != null && previewSize != null && surfaceRect != null) {
            if (mTextureView != null && mTextureView.surfaceTexture != null) {
                previewSize?.let {
                    val transform = calculateTextureTransform(
                        Size(mTextureView.width, mTextureView.height), it)
                    mTextureView.setTransform(transform)
                }
            }

            startCameraPreview(CameraSurface(mTextureView.surfaceTexture))
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        containerSized(Size(r - l, b - t))

        mTextureView?.layout(0, 0, width, height)
    }

    open fun resume() {
        validateMainThread()
        if (mLog.isDebugEnabled) {
            mLog.debug("resume()")
        }

        initCamera()

        if (currentSurfaceSize != null) {
            startPreviewIfReady()
        } else if (mTextureView != null) {
            mTextureView.surfaceTextureListener = surfaceTextureListener()
        }

        requestLayout()
        mRotationListener?.listen(context) {
            mStateHandler?.postDelayed(::rotationChanged, ROTATION_LISTENER_DELAY_MS)
        }
    }

    open fun pause() {
        validateMainThread()
        if (mLog.isDebugEnabled) {
            mLog.debug("pause()")
        }

        openedOrientation = -1
        mCameraInstance?.let {
            it.close()

            mCameraInstance = null
            previewActive   = false
        }

        if (currentSurfaceSize == null && mTextureView != null) {
            mTextureView.surfaceTextureListener = null
        }

        containerSize      = null
        previewSize        = null
        previewFramingRect = null
        mRotationListener?.stop()

        mFireState.stopped()
    }

    fun marginFraction(marginfraction: Double) {
        if (marginfraction >= 0.5) {
            throw IllegalArgumentException("The margin fraction must be less than 0.5")
        }

        marginFraction = marginfraction
    }

    protected fun isActive() = mCameraInstance != null

    private fun displayRotation() = mWindowManager.defaultDisplay.rotation

    private fun initCamera() {
        if (mCameraInstance != null) {
            if (mLog.isDebugEnabled) {
                mLog.debug("INIT CAMERA CALLED TWICE")
            }

            return
        }

        if (mLog.isDebugEnabled) {
            mLog.debug("INIT CAMERA")
        }

        mCameraInstance = CameraInstance(context)
        mCameraInstance?.apply {
            cameraSetting = mCameraSettings
            readyHandler  = mStateHandler
            open()

            openedOrientation = displayRotation()
        }
    }

    private fun startCameraPreview(surface: CameraSurface) {
        if (!previewActive) {
            mCameraInstance?.let {
                it.surface = surface
                it.startPreview()
                previewActive = true

                previewStarted()
                mFireState.started()
            }
        }
    }

    open protected fun previewStarted() {

    }

    protected fun calculateFramingRect(container: Rect, surface: Rect): Rect {
        val intersection = Rect(container)

        framingRectSize?.let {
            val hMargin = max(0, (intersection.width() - it.width) / 2)
            val vMargin = max(0, (intersection.height() - it.height) / 2)

            intersection.inset(hMargin, vMargin)

            return intersection
        }

        val margin = min(intersection.width() * marginFraction, intersection.height() * marginFraction).toInt()
        intersection.inset(margin, margin)

        if (intersection.height() > intersection.width()) {
            intersection.inset(0, (intersection.height() - intersection.width()) / 2)
        }

        return intersection
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = super.onSaveInstanceState()

        return Bundle().apply {
            putParcelable("super", state)
            putBoolean("torch", isTorchOn)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            state.apply {
                super.onRestoreInstanceState(getParcelable("super"))
                isTorchOn = getBoolean("torch")
            }

            return
        }

        super.onRestoreInstanceState(state)
    }
}

interface PreviewStateListener {
    fun sized()
    fun started()
    fun stopped()
    fun error(e: Exception)
}

