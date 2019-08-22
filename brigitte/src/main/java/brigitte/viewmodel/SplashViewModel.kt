package brigitte.viewmodel

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import brigitte.arch.SingleLiveEvent
import brigitte.singleTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 14. <p/>
 */

class SplashViewModel @Inject constructor(
) : ViewModel() {
    companion object {
        private val mLog = LoggerFactory.getLogger(SplashViewModel::class.java)

        private const val SPLASH_TIMEOUT = 7000L
    }

    private val mDisposable = CompositeDisposable()
    private var mState      = true

    val closeEvent = SingleLiveEvent<Void>()

    init {
        // splash view 를 만들까도 생각했는데 굳이? 라는 생각에 그냥 vm 으로만 하도록 함
        // 여지껏 커스텀 뷰를 만들어서 재활용한 적이 별로 없다.. -_ -;

        // 로딩 완료가 안뜨는 경우가 존재할 수 있으니 이를 보안하기 위한 타이머 추가
        mDisposable.add(singleTimer(SPLASH_TIMEOUT)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                if (mLog.isInfoEnabled && mState) {
                    mLog.info("SPLASH TIMEOUT ($SPLASH_TIMEOUT MILLISECONDS)")
                }

                closeSplash()
            })
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
            closeEvent.call()
        }
    }
}