package com.example.clone_daum.ui.main

import android.app.Application
import android.view.MotionEvent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.*
import com.example.clone_daum.common.Config
import brigitte.*
import brigitte.viewmodel.CommandEventViewModel
import brigitte.widget.magneticEffect
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.google.android.material.tabs.TabLayout
import dagger.Module
import kotlin.math.abs

class MainViewModel @Inject constructor(
    val config: Config,
    app: Application
) : CommandEventViewModel(app) {
    companion object {
        private val logger = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1

        const val CMD_SEARCH_FRAGMENT       = "search-fragment"
        const val CMD_NAVIGATION_FRAGMENT   = "navigation-fragment"
        const val CMD_MEDIA_SEARCH_FRAGMENT = "media-search"

        const val CMD_REALTIME_ISSUE = "realtime-issue"
        const val CMD_BRS_OPEN       = "brs-open"
        const val CMD_MAP            = "map-open"

        const val ITN_GOTO_NEWS = "goto-news"
    }

    // MAIN WEB TAB CONTROL
    val tabChangedCallback      = ObservableField<TabSelectedCallback>()
    var tabChangedLive          = MutableLiveData<TabLayout.Tab?>()
    var tabSelector             = ObservableInt(0)

    val appbarChangedListener   = ObservableField<(Int, Int) -> Unit>()
    val appbarAlpha             = ObservableField<Float>()

    val appbarDragCallback      = ObservableBoolean(false)
    val appbarOffsetLive        = MutableLiveData<Int>()

    var mainContainerTouchEvent  = ObservableField<(MotionEvent) -> Boolean>()
    var appbarMagneticEffectLive = MutableLiveData<Boolean>()

    val idSearchIcon            = ObservableInt(config.SEARCH_ICON)
    val webViewPagerOffLimit    = ObservableInt(3)

    var appbarHeight     = 0
    val spinnerOffsetEnd = ObservableInt()

    init {
        tabChangedCallback.set(TabSelectedCallback {
            if (logger.isDebugEnabled) {
                logger.debug("CHANGED MAIN TAB ${it?.position}")
            }

            tabChangedLive.value = it
        })

        command(ITN_GOTO_NEWS)
    }

    fun appbarHeight(appbarHeight: Int, containerHeight: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("APPBAR HEIGHT: $appbarHeight, CONTAINER HEIGHT: $containerHeight")
        }

        spinnerOffsetEnd.set(appbarHeight)
        this.appbarHeight = appbarHeight - containerHeight
    }

    fun appbarAlpha() {
        // n 사도 그렇지만 k 사도 search 쪽을 view 로 가려서 하는 데
        // -_ - 이러한 구조를 가져가는게
        // 딱히 득이 될건 없어 보이는데 흠; 전국적으로 헤더 만큼에 패킷 낭비가...

        appbarChangedListener.set { maxScroll, offset ->
            val percentage = 1f - abs(offset).toFloat() / maxScroll.toFloat()

            if (logger.isTraceEnabled) {
                logger.trace("APPBAR (ALPHA) : $percentage")
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
                if (logger.isDebugEnabled) {
                    logger.debug("MAGNETIC EFFECT : ${if (it) "UP" else "DOWN"}")
                }

                appbarMagneticEffectLive.value = it
            }
        }
    }

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            ITN_GOTO_NEWS -> tabSelector.notify(INDEX_NEWS)
            else -> super.command(cmd, data)
        }
    }
}
