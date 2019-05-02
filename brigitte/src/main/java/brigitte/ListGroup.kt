@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import androidx.databinding.ObservableBoolean

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-04-26 <p/>
 */

inline fun <T> List<T>.toggleItems(targetCallback: (T) -> ObservableBoolean) {
    var allTrue = false

    // 현대 리스트에서 check 된 항목이 존재하는지 확인 뒤
    for (item in this) {
        if (!targetCallback(item).get()) {
            allTrue = true
            break
        }
    }

    if (allTrue) {
        // 체크된게 하나라도 있으면 그냥 다 true 로 설정
        forEach { targetCallback(it).set(true) }
    } else {
        // 아니면 기존 값에 반대되는 값을 넣기
        forEach { targetCallback(it).toggle() }
    }
}
