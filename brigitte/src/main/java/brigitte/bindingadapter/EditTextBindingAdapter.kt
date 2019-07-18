package brigitte.bindingadapter

import android.view.KeyEvent
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
        view.setOnEditorActionListener { _, id, event ->
            when (id) {
                EditorInfo.IME_ACTION_DONE,
                EditorInfo.IME_ACTION_SEARCH -> {
                    callback(view.text.toString())
                }
                EditorInfo.IME_ACTION_UNSPECIFIED -> {
                    //https://pk09.tistory.com/entry/custom-IME%EC%97%90%EC%84%9C-enterdone-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EB%B0%98%EC%9D%91%EC%9D%B4-%EC%97%86%EC%9D%84%EB%95%8C
                    when(event.keyCode) {
                        KeyEvent.KEYCODE_ENTER -> {
                            callback(view.text.toString())
                        }
                        else -> false
                    }
                }
                else -> false
            }
        }
    }
}