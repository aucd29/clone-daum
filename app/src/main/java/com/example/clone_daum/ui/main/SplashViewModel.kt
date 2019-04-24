package com.example.clone_daum.ui.main

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.example.clone_daum.common.Config
import com.example.common.arch.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 14. <p/>
 */

class SplashViewModel @Inject constructor(val config: Config
) : ViewModel() {
    companion object {
        private val mLog = LoggerFactory.getLogger(SplashViewModel::class.java)

        private const val SPLASH_TIMEOUT = 10L
    }

    private val mDisposable = CompositeDisposable()
    private var mState      = true

    val visibleSplash    = ObservableInt(View.VISIBLE)
    val translationY     = ObservableInt()
    val viewHeight       = ObservableInt()
    val closeSplashEvent = SingleLiveEvent<Void>()

    init {
        // 초기 로딩시 적용되는 v center 와 실제 레이아웃에 들어가는 v center 랑 값이 달라
        // 이를 보정하기 위해 transition 값을 조정 한다.
        translationY.set(splashMargin())
        viewHeight.set(config.SCREEN.y + config.STATUS_BAR_HEIGHT)

        // splash view 를 만들까도 생각했는데 굳이? 라는 생각에 그냥 vm 으로만 하도록 함
        // 여지껏 커스텀 뷰를 만들어서 재활용한 적이 별로 없다.. -_ -;

        // 로딩 완료가 안뜨는 경우가 존재할 수 있으니 이를 보안하기 위한 타이머 추가
        mDisposable.add(Observable.timer(SPLASH_TIMEOUT, TimeUnit.SECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isInfoEnabled && mState) {
                    mLog.info("SPLASH TIMEOUT ($SPLASH_TIMEOUT SECOND)")
                }

                closeSplash()
            })
    }

    // 목적상 이건 config 보단 여기에 위치하는게 나을듯
    private fun splashMargin() = config.run {
        val statusMargin = STATUS_BAR_HEIGHT * -1 / 2
        val bottomButtonMargin = if (SOFT_BUTTON_BAR_HEIGHT == 0) 0 else SOFT_BUTTON_BAR_HEIGHT / 2

        statusMargin + bottomButtonMargin
    }

    fun closeSplash() {
        synchronized(this) {
            if (!mState) {
                return
            }

            if (mLog.isInfoEnabled) {
                mLog.info("CLOSE SPLASH")
            }

            mState = false
            mDisposable.dispose()
            closeSplashEvent.call()
        }
    }
}