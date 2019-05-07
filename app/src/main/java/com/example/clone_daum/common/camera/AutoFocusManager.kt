package com.example.clone_daum.common.camera

import android.hardware.Camera
import android.os.Handler
import org.slf4j.LoggerFactory
import java.util.ArrayList

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 21. <p/>
 */

class AutoFocusManager(private val camera: Camera, settings: CameraSettings) {
    companion object {
        private val mLog = LoggerFactory.getLogger(AutoFocusManager::class.java)

        private val AUTO_FOCUS_INTERVAL_MS = 2000L
        private val FOCUS_MODES_CALLING_AF: MutableCollection<String>

        init {
            FOCUS_MODES_CALLING_AF = ArrayList(2)
            FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO)
            FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO)
        }
    }

    private var stopped: Boolean = false
    private var focusing: Boolean = false
    private val useAutoFocus: Boolean
    private val handler: Handler

    private val MESSAGE_FOCUS = 1

    private val focusHandlerCallback = Handler.Callback { msg ->
        if (msg.what == MESSAGE_FOCUS) {
            focus()
            return@Callback true
        }
        false
    }

    private val autoFocusCallback: Camera.AutoFocusCallback

    init {
        this.handler = Handler(focusHandlerCallback)
        this.autoFocusCallback = Camera.AutoFocusCallback { success, theCamera ->
            handler.post {
                focusing = false
                autoFocusAgainLater()
            }
        }

        val currentFocusMode = camera.parameters.focusMode
        useAutoFocus = settings.isAutoFocusEnabled && FOCUS_MODES_CALLING_AF.contains(currentFocusMode)

        if (mLog.isInfoEnabled) {
            mLog.info("Current focus mode '$currentFocusMode'; use auto focus? $useAutoFocus")
        }

        start()
    }

    @Synchronized
    private fun autoFocusAgainLater() {
        if (!stopped && !handler.hasMessages(MESSAGE_FOCUS)) {
            handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_FOCUS), AUTO_FOCUS_INTERVAL_MS)
        }
    }

    /**
     * Start auto-focus. The first focus will happen now, then repeated every two seconds.
     */
    fun start() {
        stopped = false
        focus()
    }

    private fun focus() {
        if (useAutoFocus) {
            if (!stopped && !focusing) {
                try {
                    camera.autoFocus(autoFocusCallback)
                    focusing = true
                } catch (re: RuntimeException) {
                    // Have heard RuntimeException reported in Android 4.0.x+; continue?
                    if (mLog.isDebugEnabled) {
                        re.printStackTrace()
                    }

                    mLog.error("ERROR: Unexpected exception while focusing ${re.message}")

                    // Try again later to keep cycle going
                    autoFocusAgainLater()
                }
            }
        }
    }

    private fun cancelOutstandingTask() {
        handler.removeMessages(MESSAGE_FOCUS)
    }

    /**
     * Stop auto-focus.
     */
    fun stop() {
        stopped = true
        focusing = false
        cancelOutstandingTask()
        if (useAutoFocus) {
            // Doesn't hurt to call this even if not focusing
            try {
                camera.cancelAutoFocus()
            } catch (re: RuntimeException) {
                // Have heard RuntimeException reported in Android 4.0.x+; continue?
                if (mLog.isDebugEnabled) {
                    re.printStackTrace()
                }

                mLog.error("ERROR: Unexpected exception while cancelling focusing ${re.message}")
            }
        }
    }
}
