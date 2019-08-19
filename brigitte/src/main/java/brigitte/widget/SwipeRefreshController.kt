package brigitte.widget

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import brigitte.arch.SingleLiveEvent
import brigitte.notify
import brigitte.singleTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-14 <p/>
 */

class SwipeRefreshController {
    private val mLog = LoggerFactory.getLogger(SwipeRefreshController::class.java)

    val listener    = ObservableField<() -> Unit>()
    val isRefresh   = ObservableBoolean(false)
    var refreshLive = SingleLiveEvent<Void>()

    fun init(listener: () -> Unit) {
        if (mLog.isDebugEnabled) {
            mLog.debug("INIT")
        }

        this.listener.set(listener)
    }

    fun initTest(dp: CompositeDisposable, delay: Long = 500) {
        if (mLog.isDebugEnabled) {
            mLog.debug("INIT TEST $delay")
        }

        this.listener.set {
            dp.add(singleTimer(delay)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ -> stopSwipeRefresh() })
        }
    }

    fun initLive() {
        if (mLog.isDebugEnabled) {
            mLog.debug("INIT LIVE")
        }

        this.listener.set { refreshLive.call() }
    }

    fun stopSwipeRefresh() {
        if (mLog.isDebugEnabled) {
            mLog.debug("STOP SWIPE REFRESH")
        }

        isRefresh.notify(false)
    }
}