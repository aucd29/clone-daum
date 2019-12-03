package com.example.clone_daum.common.camera

import android.graphics.SurfaceTexture
import android.hardware.Camera
import java.io.IOException

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 21. <p/>
 */

class CameraSurface(var surfaceTexture: SurfaceTexture) {
    @Throws(IOException::class)
    fun setPreview(camera: Camera) {
        camera.setPreviewTexture(surfaceTexture)
    }
}