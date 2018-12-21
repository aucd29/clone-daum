package com.example.clone_daum.ui.main

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.ui.ViewController
import com.example.common.WebViewEvent
import com.example.common.WebViewSettingParams
import com.example.common.arch.SingleLiveEvent
import com.google.android.material.appbar.AppBarLayout
import org.slf4j.LoggerFactory
import javax.inject.Inject


class MainViewModel @Inject constructor(val app: Application
    , val preConfig: PreloadConfig
) : AndroidViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1
    }

    val tabAdapter         = ObservableField<MainTabAdapter>()
    val viewpager          = ObservableField<ViewPager>()
    val brsSetting         = ObservableField<WebViewSettingParams>()
    val brsEvent           = ObservableField<WebViewEvent>()
    val viewpagerPageLimit = ObservableInt(3)
    val visibleBack        = ObservableInt(View.GONE)
    var gotoNewsEvent      = ObservableInt(0)
    val gotoSearchEvent    = SingleLiveEvent<Void>()

    val navEvent = SingleLiveEvent<Void>()

    // viewpager 에 adapter 가 set 된 이후 시점을 알려줌 (ViewPagerBindingAdapter)
    val viewpagerLoadedEvent     = ObservableField<() -> Unit>()
    val appbarOffsetChangedEvent = ObservableField<(AppBarLayout, Int) -> Unit>()
    val appbarOffsetLiveEvent    = MutableLiveData<Int>()


    fun gotoNews() {
        if (mLog.isDebugEnabled) {
            mLog.debug("goto tab $INDEX_NEWS")
        }

        if (gotoNewsEvent.get() == INDEX_NEWS) {
            gotoNewsEvent.notifyChange()
        } else {
            gotoNewsEvent.set(INDEX_NEWS)
        }
    }

    fun searchFragment() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }

        gotoSearchEvent.call()
    }

    fun searchExtendMenu() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun searchExtendRank() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun weatherDetail() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun confirmGps() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun tabMenu() {
        navEvent.call()
    }

    fun webviewBack() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }
}
