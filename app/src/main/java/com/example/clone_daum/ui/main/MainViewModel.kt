package com.example.clone_daum.ui.main

import android.app.Application
import android.view.MotionEvent
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.*
import androidx.lifecycle.LifecycleEventObserver
import com.example.clone_daum.common.Config
import brigitte.*
import brigitte.viewmodel.CommandEventViewModel
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.common.widget.magneticEffect
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs


class MainViewModel @Inject constructor(
    app: Application,
    val config: Config
) : CommandEventViewModel(app), LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        mLog.error("ERROR: ######## LIFE CYCLE $event")
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1

        const val CMD_SEARCH_FRAGMENT       = "search-fragment"
        const val CMD_NAVIGATION_FRAGMENT   = "navigation-fragment"
        const val CMD_MEDIA_SEARCH_FRAGMENT = "media-search"

        const val CMD_REALTIME_ISSUE = "realtime-issue"
        const val CMD_BRS_OPEN       = "brs-open"

        const val GOTO_NEWS = "goto-news"
    }

    // MAIN WEB TAB CONTROL
    val tabChangedCallback      = ObservableField<TabSelectedCallback>()
    var tabChangedLive          = MutableLiveData<TabLayout.Tab?>()
    var tabSelector             = ObservableInt(0)

    val appbarChangedListener   = ObservableField<(Int, Int) -> Unit>()
    val appbarAlpha             = ObservableField<Float>()

    val appbarDragCallback      = ObservableBoolean(false)
    val appbarOffsetLive        = MutableLiveData<Int>()
    var progressViewOffsetLive  = MutableLiveData<Int>()

    var mainContainerTouchEvent  = ObservableField<(MotionEvent) -> Boolean>()
    var appbarMagneticEffectLive = MutableLiveData<Boolean>()

    val idSearchIcon            = ObservableInt(config.SEARCH_ICON)
    val viewBack                = ObservableInt(View.GONE)

    var appbarHeight = 0

    init {
        tabChangedCallback.set(TabSelectedCallback {
            if (mLog.isDebugEnabled) {
                mLog.debug("CHANGED MAIN TAB ${it?.position}")
            }

            tabChangedLive.value = it
        })
    }

    fun appbarHeight(appbarHeight: Int, containerHeight: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("APPBAR HEIGHT: $appbarHeight, CONTAINER HEIGHT: $containerHeight")
        }

        this.progressViewOffsetLive.value = appbarHeight
        this.appbarHeight                 = appbarHeight - containerHeight
    }

    fun appbarAlpha() {
        // n 사도 그렇지만 k 사도 search 쪽을 view 로 가려서 하는 데
        // -_ - 이러한 구조를 가져가는게
        // 딱히 득이 될건 없어 보이는데 흠; 전국적으로 헤더 만큼에 패킷 낭비가...

        appbarChangedListener.set { maxScroll, offset ->
            val percentage = 1f - abs(offset).toFloat() / maxScroll.toFloat()

            if (mLog.isTraceEnabled) {
                mLog.trace("APPBAR (ALPHA) : $percentage")
            }

            appbarAlpha.set(percentage)

            // scroll 되어 offset 된 값을 webview 쪽으로 전달
            appbarOffsetLive.value = offset
        }
    }

    fun mainContainerDispatchTouchEvent() {
        mainContainerTouchEvent.set {
            val y   = appbarOffsetLive.value!!
            val max = appbarHeight * -1

            magneticEffect(it, y, max) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("MAGNETIC EFFECT : ${if (it) "UP" else "DOWN"}")
                }

                appbarMagneticEffectLive.value = it
            }
        }
    }

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            GOTO_NEWS -> tabSelector.run {
                if (mLog.isDebugEnabled) {
                    mLog.debug("GOTO NEWS TAB $INDEX_NEWS")
                }

                when (get()) {
                    INDEX_NEWS -> notifyChange()
                    else       -> set(INDEX_NEWS)
                }
            }
            else -> super.command(cmd, data)
        }
    }
}
