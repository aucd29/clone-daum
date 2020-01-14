package com.example.clone_daum.common.camera

import android.hardware.Camera
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 21. <p/>
 */

object OpenCameraInterface {
    private val logger = LoggerFactory.getLogger(OpenCameraInterface::class.java)

    /**
     * For [.open], means no preference for which camera to open.
     */
    const val NO_REQUESTED_CAMERA = -1

    fun getCameraId(requestedId: Int): Int {
        val numCameras = Camera.getNumberOfCameras()
        if (numCameras == 0) {
            logger.error("ERROR: No cameras!")
            return -1
        }

        var cameraId = requestedId
        val explicitRequest = cameraId >= 0

        if (!explicitRequest) {
            // Select a camera if no explicit camera requested
            var index = 0
            while (index < numCameras) {
                val cameraInfo = Camera.CameraInfo()
                Camera.getCameraInfo(index, cameraInfo)
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break
                }
                index++
            }

            cameraId = index
        }

        return if (cameraId < numCameras) {
            cameraId
        } else {
            if (explicitRequest) {
                -1
            } else {
                0
            }
        }
    }

    /**
     * Opens the requested camera with [Camera.open], if one exists.
     *
     * @param requestedId camera ID of the camera to use. A negative value
     * or [.NO_REQUESTED_CAMERA] means "no preference"
     * @return handle to [Camera] that was opened
     */
    fun open(requestedId: Int): Camera? {
        val cameraId = getCameraId(requestedId)
        return if (cameraId == -1) {
            null
        } else {
            Camera.open(cameraId)
        }
    }
}
