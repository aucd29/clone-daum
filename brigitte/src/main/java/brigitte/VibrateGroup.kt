@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import brigitte.viewmodel.requireContext

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-04-22 <p/>
 */

inline fun Context.vibrate(milliseconds: Long = 300) {
    systemService<Vibrator>()?.let {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            it.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            it.vibrate(milliseconds)
        }
    }
}

/**
    0 : Start without a delay
    400 : Vibrate for 400 milliseconds
    200 : Pause for 200 milliseconds
    400 : Vibrate for 400 milliseconds
 */
inline fun Context.vibrate(pattern: LongArray, repeat: Int) {
    systemService<Vibrator>()?.let {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            it.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            it.vibrate(pattern, repeat)
        }
    }
}

inline fun Fragment.vibrate(pattern: LongArray, repeat: Int) =
    requireContext().vibrate(pattern, repeat)

inline fun Fragment.vibrate(milliseconds: Long) =
    requireContext().vibrate(milliseconds)

inline fun AndroidViewModel.vibrate(pattern: LongArray, repeat: Int) =
    requireContext().vibrate(pattern, repeat)

inline fun AndroidViewModel.vibrate(milliseconds: Long) =
    requireContext().vibrate(milliseconds)