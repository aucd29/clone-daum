package com.example.clone_daum.ui.main

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.example.clone_daum.di.module.Config
import com.example.common.arch.SingleLiveEvent
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 14. <p/>
 */

class SplashViewModel @Inject constructor(
    val config: Config
) : ViewModel() {
    val visibleSplash      = ObservableInt(View.VISIBLE)
    val splashTranslationY = ObservableInt()
    val splashHeight       = ObservableInt()

    val splashCloseEvent   = SingleLiveEvent<Void>()

    init {
        // 초기 로딩시 적용되는 v center 와 실제 레이아웃에 들어가는 v center 랑 값이 달라
        // 이를 보정하기 위해 transition 값을 조정 한다.
        splashTranslationY.set(config.STATUS_BAR_HEIGHT * -1 / 2)
        splashHeight.set(config.SCREEN.y + config.STATUS_BAR_HEIGHT)

        // splash view 를 만들까도 생각했는데 굳이? 라는 생각에 그냥 vm 으로만 하도록 함
        // 여지껏 커스텀 뷰를 만들어서 재활용한 적이 별로 없다.. -_ -;
    }
}