package com.example.clone_daum.common.camera

import android.annotation.TargetApi
import android.graphics.Rect
import android.hardware.Camera
import android.os.Build
import android.util.Log
import org.slf4j.LoggerFactory
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 21. <p/>
 */

object CameraConfigurationUtils {
    private val mLog = LoggerFactory.getLogger(CameraConfigurationUtils::class.java)

    private val SEMICOLON = Pattern.compile(";")

    private const val MAX_EXPOSURE_COMPENSATION = 1.5f
    private const val MIN_EXPOSURE_COMPENSATION = 0.0f
    private const val MIN_FPS = 10
    private const val MAX_FPS = 20
    private const val AREA_PER_1000 = 400

    fun setFocus(
        parameters: Camera.Parameters,
        focusModeSetting: CameraSettings.FocusMode,
        safeMode: Boolean
    ) {
        val supportedFocusModes = parameters.supportedFocusModes
        var focusMode: String? = null

        if (safeMode || focusModeSetting == CameraSettings.FocusMode.AUTO) {
            focusMode = findSettableValue(
                "focus mode",
                supportedFocusModes,
                Camera.Parameters.FOCUS_MODE_AUTO
            )
        } else if (focusModeSetting == CameraSettings.FocusMode.CONTINUOUS) {
            focusMode = findSettableValue(
                "focus mode",
                supportedFocusModes,
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
                Camera.Parameters.FOCUS_MODE_AUTO
            )
        } else if (focusModeSetting == CameraSettings.FocusMode.INFINITY) {
            focusMode = findSettableValue(
                "focus mode",
                supportedFocusModes,
                Camera.Parameters.FOCUS_MODE_INFINITY
            )
        } else if (focusModeSetting == CameraSettings.FocusMode.MACRO) {
            focusMode = findSettableValue(
                "focus mode",
                supportedFocusModes,
                Camera.Parameters.FOCUS_MODE_MACRO
            )
        }

        // Maybe selected auto-focus but not available, so fall through here:
        if (!safeMode && focusMode == null) {
            focusMode = findSettableValue(
                "focus mode",
                supportedFocusModes,
                Camera.Parameters.FOCUS_MODE_MACRO,
                Camera.Parameters.FOCUS_MODE_EDOF
            )
        }
        if (focusMode != null) {
            if (focusMode == parameters.focusMode) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("Focus mode already set to $focusMode")
                }
            } else {
                parameters.focusMode = focusMode
            }
        }
    }

    fun setTorch(parameters: Camera.Parameters, on: Boolean) {
        val supportedFlashModes = parameters.supportedFlashModes
        val flashMode: String?
        flashMode = if (on) {
            findSettableValue(
                "flash mode",
                supportedFlashModes,
                Camera.Parameters.FLASH_MODE_TORCH,
                Camera.Parameters.FLASH_MODE_ON
            )
        } else {
            findSettableValue(
                "flash mode",
                supportedFlashModes,
                Camera.Parameters.FLASH_MODE_OFF
            )
        }
        if (flashMode != null) {
            if (flashMode == parameters.flashMode) {
                if (mLog.isInfoEnabled) {
                    mLog.info("Flash mode already set to $flashMode")
                }
            } else {
                if (mLog.isInfoEnabled) {
                    mLog.info("Setting flash mode to $flashMode")
                }
                parameters.flashMode = flashMode
            }
        }
    }

    fun setBestExposure(parameters: Camera.Parameters, lightOn: Boolean) {
        val minExposure = parameters.minExposureCompensation
        val maxExposure = parameters.maxExposureCompensation
        val step = parameters.exposureCompensationStep
        if ((minExposure != 0 || maxExposure != 0) && step > 0.0f) {
            // Set low when light is on
            val targetCompensation = if (lightOn) MIN_EXPOSURE_COMPENSATION else MAX_EXPOSURE_COMPENSATION
            var compensationSteps = (targetCompensation / step).roundToInt()
            val actualCompensation = step * compensationSteps
            // Clamp value:
            compensationSteps = max(min(compensationSteps, maxExposure), minExposure)
            if (parameters.exposureCompensation == compensationSteps) {
                if (mLog.isInfoEnabled) {
                    mLog.info("Exposure compensation already set to $compensationSteps / $actualCompensation")
                }
            } else {
                if (mLog.isInfoEnabled) {
                    mLog.info("Setting exposure compensation to $compensationSteps / $actualCompensation")
                }
                parameters.exposureCompensation = compensationSteps
            }
        } else {
            if (mLog.isInfoEnabled) {
                mLog.info("Camera does not support exposure compensation")
            }
        }
    }

    @JvmOverloads
    fun setBestPreviewFPS(parameters: Camera.Parameters, minFPS: Int = MIN_FPS, maxFPS: Int = MAX_FPS) {
        val supportedPreviewFpsRanges = parameters.supportedPreviewFpsRange

        if (mLog.isInfoEnabled) {
            mLog.info("Supported FPS ranges: ${toString(supportedPreviewFpsRanges)}")
        }

        if (supportedPreviewFpsRanges != null && supportedPreviewFpsRanges.isNotEmpty()) {
            var suitableFPSRange: IntArray? = null
            for (fpsRange in supportedPreviewFpsRanges) {
                val thisMin = fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                val thisMax = fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                if (thisMin >= minFPS * 1000 && thisMax <= maxFPS * 1000) {
                    suitableFPSRange = fpsRange
                    break
                }
            }
            if (suitableFPSRange == null) {
                if (mLog.isInfoEnabled) {
                    mLog.info("No suitable FPS range?")
                }
            } else {
                val currentFpsRange = IntArray(2)
                parameters.getPreviewFpsRange(currentFpsRange)
                if (Arrays.equals(currentFpsRange, suitableFPSRange)) {
                    if (mLog.isInfoEnabled) {
                        mLog.info("FPS range already set to " + Arrays.toString(suitableFPSRange))
                    }
                } else {
                    if (mLog.isInfoEnabled) {
                        mLog.info("Setting FPS range to " + Arrays.toString(suitableFPSRange))
                    }
                    parameters.setPreviewFpsRange(
                        suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                        suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                    )
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    fun setFocusArea(parameters: Camera.Parameters) {
        if (parameters.maxNumFocusAreas > 0) {
            if (mLog.isInfoEnabled) {
                mLog.info("Old focus areas: " + toString(parameters.focusAreas)!!)
            }
            val middleArea = buildMiddleArea(AREA_PER_1000)

            if (mLog.isInfoEnabled) {
                mLog.info("Setting focus area to : " + toString(middleArea)!!)
            }
            parameters.focusAreas = middleArea
        } else {
            if (mLog.isInfoEnabled) {
                mLog.info("Device does not support focus areas")
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    fun setMetering(parameters: Camera.Parameters) {
        if (parameters.maxNumMeteringAreas > 0) {
            if (mLog.isInfoEnabled) {
                mLog.info("Old metering areas: " + parameters.meteringAreas)
            }
            val middleArea = buildMiddleArea(AREA_PER_1000)
            if (mLog.isInfoEnabled) {
                mLog.info("Setting metering area to : " + toString(middleArea)!!)
            }
            parameters.meteringAreas = middleArea
        } else {
            if (mLog.isInfoEnabled) {
                mLog.info("Device does not support metering areas")
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private fun buildMiddleArea(areaPer1000: Int): List<Camera.Area> {
        return listOf(Camera.Area(Rect(-areaPer1000, -areaPer1000, areaPer1000, areaPer1000), 1))
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    fun setVideoStabilization(parameters: Camera.Parameters) {
        if (parameters.isVideoStabilizationSupported) {
            if (parameters.videoStabilization) {
                if (mLog.isInfoEnabled) {
                    mLog.info("Video stabilization already enabled")
                }
            } else {
                if (mLog.isInfoEnabled) {
                    mLog.info("Enabling video stabilization...")
                }

                parameters.videoStabilization = true
            }
        } else {
            if (mLog.isInfoEnabled) {
                mLog.info("This device does not support video stabilization")
            }
        }
    }

    fun setBarcodeSceneMode(parameters: Camera.Parameters) {
        if (Camera.Parameters.SCENE_MODE_BARCODE == parameters.sceneMode) {
            if (mLog.isInfoEnabled) {
                mLog.info("Barcode scene mode already set")
            }
            return
        }
        val sceneMode = findSettableValue(
            "scene mode",
            parameters.supportedSceneModes,
            Camera.Parameters.SCENE_MODE_BARCODE
        )
        if (sceneMode != null) {
            parameters.sceneMode = sceneMode
        }
    }

    fun setZoom(parameters: Camera.Parameters, targetZoomRatio: Double) {
        if (parameters.isZoomSupported) {
            val zoom = indexOfClosestZoom(parameters, targetZoomRatio) ?: return
            if (parameters.zoom == zoom) {
                if (mLog.isInfoEnabled) {
                    mLog.info("Zoom is already set to $zoom")
                }
            } else {
                if (mLog.isInfoEnabled) {
                    mLog.info("Setting zoom to $zoom")
                }
                parameters.zoom = zoom
            }
        } else {
            if (mLog.isInfoEnabled) {
                mLog.info("Zoom is not supported")
            }
        }
    }

    private fun indexOfClosestZoom(parameters: Camera.Parameters, targetZoomRatio: Double): Int? {
        val ratios = parameters.zoomRatios

        if (mLog.isInfoEnabled) {
            mLog.info("Zoom ratios: " + ratios!!)
        }
        val maxZoom = parameters.maxZoom
        if (ratios == null || ratios.isEmpty() || ratios.size != maxZoom + 1) {
            if (mLog.isInfoEnabled) {
                mLog.info("Invalid zoom ratios!")
            }
            return null
        }
        val target100 = 100.0 * targetZoomRatio
        var smallestDiff = java.lang.Double.POSITIVE_INFINITY
        var closestIndex = 0
        for (i in ratios.indices) {
            val diff = abs(ratios[i] - target100)
            if (diff < smallestDiff) {
                smallestDiff = diff
                closestIndex = i
            }
        }
        if (mLog.isInfoEnabled) {
            mLog.info("Chose zoom ratio of " + ratios[closestIndex] / 100.0)
        }
        return closestIndex
    }

    fun setInvertColor(parameters: Camera.Parameters) {
        if (Camera.Parameters.EFFECT_NEGATIVE == parameters.colorEffect) {
            if (mLog.isInfoEnabled) {
                mLog.info("Negative effect already set")
            }
            return
        }
        val colorMode = findSettableValue(
            "color effect",
            parameters.supportedColorEffects,
            Camera.Parameters.EFFECT_NEGATIVE
        )
        if (colorMode != null) {
            parameters.colorEffect = colorMode
        }
    }

    private fun findSettableValue(
        name: String,
        supportedValues: Collection<String>?,
        vararg desiredValues: String
    ): String? {
        if (mLog.isInfoEnabled) {
            mLog.info("Requesting " + name + " value from among: " + Arrays.toString(desiredValues))
            mLog.info("Supported $name values: $supportedValues")
        }

        if (supportedValues != null) {
            for (desiredValue in desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    if (mLog.isInfoEnabled) {
                        mLog.info("Can set $name to: $desiredValue")
                    }
                    return desiredValue
                }
            }
        }
        if (mLog.isInfoEnabled) {
            mLog.info("No supported values match")
        }
        return null
    }

    private fun toString(arrays: Collection<IntArray>?): String {
        if (arrays == null || arrays.isEmpty()) {
            return "[]"
        }
        val buffer = StringBuilder()
        buffer.append('[')
        val it = arrays.iterator()
        while (it.hasNext()) {
            buffer.append(Arrays.toString(it.next()))
            if (it.hasNext()) {
                buffer.append(", ")
            }
        }
        buffer.append(']')
        return buffer.toString()
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private fun toString(areas: Iterable<Camera.Area>?): String? {
        if (areas == null) {
            return null
        }
        val result = StringBuilder()
        for (area in areas) {
            result.append(area.rect).append(':').append(area.weight).append(' ')
        }
        return result.toString()
    }

    fun collectStats(parameters: Camera.Parameters): String {
        return collectStats(parameters.flatten())
    }

    fun collectStats(flattenedParams: CharSequence?): String {
        val result = StringBuilder(1000)

        result.append("BOARD=").append(Build.BOARD).append('\n')
        result.append("BRAND=").append(Build.BRAND).append('\n')
        result.append("CPU_ABI=").append(Build.CPU_ABI).append('\n')
        result.append("DEVICE=").append(Build.DEVICE).append('\n')
        result.append("DISPLAY=").append(Build.DISPLAY).append('\n')
        result.append("FINGERPRINT=").append(Build.FINGERPRINT).append('\n')
        result.append("HOST=").append(Build.HOST).append('\n')
        result.append("ID=").append(Build.ID).append('\n')
        result.append("MANUFACTURER=").append(Build.MANUFACTURER).append('\n')
        result.append("MODEL=").append(Build.MODEL).append('\n')
        result.append("PRODUCT=").append(Build.PRODUCT).append('\n')
        result.append("TAGS=").append(Build.TAGS).append('\n')
        result.append("TIME=").append(Build.TIME).append('\n')
        result.append("TYPE=").append(Build.TYPE).append('\n')
        result.append("USER=").append(Build.USER).append('\n')
        result.append("VERSION.CODENAME=").append(Build.VERSION.CODENAME).append('\n')
        result.append("VERSION.INCREMENTAL=").append(Build.VERSION.INCREMENTAL).append('\n')
        result.append("VERSION.RELEASE=").append(Build.VERSION.RELEASE).append('\n')
        result.append("VERSION.SDK_INT=").append(Build.VERSION.SDK_INT).append('\n')

        if (flattenedParams != null) {
            val params = SEMICOLON.split(flattenedParams)
            Arrays.sort(params)
            for (param in params) {
                result.append(param).append('\n')
            }
        }

        return result.toString()
    }
}
