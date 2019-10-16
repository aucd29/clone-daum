package brigitte.widget.swiperefresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import brigitte.arch.SingleLiveEvent
import brigitte.notify
import brigitte.singleTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject
import kotlin.math.abs

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 2. 26. <p/>
 *
 * https://stackoverflow.com/questions/34136178/swiperefreshlayout-blocking-horizontally-scrolled-recyclerview
 */

open class VerticalSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet
) : SwipeRefreshLayout(context, attrs) {
    private var touchSlop: Int
    private var prevX: Float = 0f
    private var decliend: Boolean = false

    init {
        this.initLayout()
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    open fun initLayout() {

    }

    override fun requestLayout() {
        super.requestLayout()
    }

    @SuppressLint("Recycle")
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                prevX = MotionEvent.obtain(ev).x
                decliend = false
            }
            MotionEvent.ACTION_MOVE -> {
                val evX = ev.x
                val xDiff = abs(evX - prevX)

                if (decliend || xDiff > touchSlop) {
                    decliend = true

                    return false
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }
}

// 이걸 dagger 종속적 이게 @inject 가 나으려나 아니면 koin 과 같이 쓰게
// provider 로 하는게 나으려나?? =_ = ?
// 후자가 나아보이긴 한데 =_ =ㅋ
class SwipeRefreshController @Inject constructor() {
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
            dp.add(
                singleTimer(delay)
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