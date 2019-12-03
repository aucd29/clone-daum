package com.example.clone_daum.common.camera

import android.content.Context
import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.WindowManager
import brigitte.systemService

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 21. <p/>
 */

class RotationListener {
    private var mLastRotation: Int = -1

    private var mWindowManager: WindowManager? = null
    private var mOrientationEventListener: OrientationEventListener? = null
    private var mRotationCallback: ((Int) -> Unit)? = null

    fun listen(context: Context, callback: ((Int) -> Unit)) {
        stop()

        mRotationCallback = callback

        context.applicationContext.apply {
            mWindowManager = systemService<WindowManager>()
            mOrientationEventListener = object: OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                override fun onOrientationChanged(orientation: Int) {
                    if (mWindowManager != null && mRotationCallback != null) {
                        mWindowManager?.defaultDisplay?.rotation?.let {
                            if (it != mLastRotation) {
                                mLastRotation = it
                                mRotationCallback?.invoke(it)
                            }
                        }
                    }
                }
            }

            mOrientationEventListener?.enable()
            mWindowManager?.defaultDisplay?.rotation?.let {
                mLastRotation = it
            }
        }
    }

    fun stop() {
        mOrientationEventListener?.disable()
        mOrientationEventListener = null
        mWindowManager            = null
        mRotationCallback         = null
    }
}