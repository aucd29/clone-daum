package com.example.clone_daum.common.camera

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 21. <p/>
 */

class CameraSettings {
    /**
     * Allows third party apps to specify the camera ID, rather than determine
     * it automatically based on available cameras and their orientation.
     *
     * @param requestedCameraId camera ID of the camera to use. A negative value means "no preference".
     */
    var requestedCameraId = OpenCameraInterface.NO_REQUESTED_CAMERA
    /**
     * Default to false.
     *
     * Inverted means dark & light colors are inverted.
     *
     * @return true if scan is inverted
     */
    var isScanInverted = false
//    /**
//     * Default to false.
//     *
//     * @return true if barcode scene mode is enabled
//     */
//    var isBarcodeSceneModeEnabled = false
    /**
     * Default to false.
     *
     * If enabled, metering is performed to determine focus area.
     *
     * @return true if metering is enabled
     */
    var isMeteringEnabled = false
    /**
     * Default to true.
     *
     * @return true if auto-focus is enabled
     */
    var isAutoFocusEnabled = true
        set(autoFocusEnabled) {
            field = autoFocusEnabled

            focusMode = if (autoFocusEnabled && isContinuousFocusEnabled) {
                FocusMode.CONTINUOUS
            } else if (autoFocusEnabled) {
                FocusMode.AUTO
            } else {
                FocusMode.AUTO  // 변경
            }
        }
    /**
     * Default to false.
     *
     * @return true if continuous focus is enabled
     */
    var isContinuousFocusEnabled = false
        set(continuousFocusEnabled) {
            field = continuousFocusEnabled

            focusMode = when {
                continuousFocusEnabled -> FocusMode.CONTINUOUS
                isAutoFocusEnabled -> FocusMode.AUTO
                else -> FocusMode.AUTO // 변경
            }
        }
    /**
     * Default to false.
     *
     * @return true if exposure is enabled.
     */
    var isExposureEnabled = false
    /**
     * Default to false.
     *
     * @return true if the torch is automatically controlled based on ambient light.
     */
    var isAutoTorchEnabled = false
    /**
     * Default to FocusMode.AUTO.
     *
     * @return value of selected focus mode
     */
    var focusMode: FocusMode = FocusMode.AUTO

    enum class FocusMode {
        AUTO,
        CONTINUOUS,
        INFINITY,
        MACRO
    }
}
