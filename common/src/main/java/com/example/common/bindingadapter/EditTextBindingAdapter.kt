package com.example.common.bindingadapter

import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */


// 이와 관련된 코드가 없기 때문에 커스텀형태로 만든다.
object EditTextBindingAdapter {
    @JvmStatic
    @BindingAdapter("bindEditorAction")
    fun bindEditorAction(view: TextView, callback: (String) -> Boolean) {
        view.setOnEditorActionListener { _, id, _ ->
            when (id) {
                EditorInfo.IME_ACTION_DONE,
                EditorInfo.IME_ACTION_SEARCH -> {
                    callback(view.text.toString())
                }
                else -> false
            }
        }
    }
}