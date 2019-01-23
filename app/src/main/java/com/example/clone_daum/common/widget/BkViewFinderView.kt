package com.example.clone_daum.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.journeyapps.barcodescanner.CameraPreview
import java.lang.Exception

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 22. <p/>
 */

class BkViewFinderView: View {
    var cameraPreview: CameraPreview? = null

    protected var framingRect: Rect? = null
    protected var previewFramingRect: Rect? = null
    protected lateinit var paint: Paint

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initLayout()
    }

    fun initLayout() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    fun cameraPreview(view: CameraPreview) {
        cameraPreview = view
        view.addStateListener(object: CameraPreview.StateListener {
            override fun previewSized() {
                invalidate()
            }

            override fun cameraError(error: Exception?) { }
            override fun previewStopped() { }
            override fun previewStarted() { }
            override fun cameraClosed() { }
        })
    }

    protected fun refreshSizes() {
        cameraPreview?.let {
            val framingRect = it.framingRect
            val previewFramingRect = it.previewFramingRect

            if (framingRect != null && previewFramingRect != null) {
                this.framingRect        = framingRect
                this.previewFramingRect = previewFramingRect
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        refreshSizes()

        if (framingRect == null && previewFramingRect == null) {
            return
        }

        val frame = framingRect
        val previewFrame = previewFramingRect

        val width = canvas.getWidth()
        val height = canvas.getHeight()
    }
}