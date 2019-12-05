package brigitte

import android.widget.SeekBar

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-06-26 <p/>
 */

/**
 * android:onProgressChanged="@{model::onProgressChanged}"
 */
interface ISeekBarProgressChanged {
    fun onProgressChanged(seekbar: SeekBar, value: Int, fromUser: Boolean)
    fun onStopTrackingTouch(seekbar: SeekBar)
}

/**
 * android:onTextChanged="@{model::onTextChanged}"
 */
interface ITextChanged {
    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
}