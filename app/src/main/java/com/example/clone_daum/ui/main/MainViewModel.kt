package com.example.clone_daum.ui.main

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.local.TabData
import com.example.common.WebViewSettingParams
import com.example.common.arch.SingleLiveEvent
import com.google.android.material.appbar.AppBarLayout
import org.slf4j.LoggerFactory
import javax.inject.Inject


class MainViewModel @Inject constructor(
    val app: Application,
    val preConfig: PreloadConfig
) : AndroidViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1
    }

    val tabAdapter          = ObservableField<MainTabAdapter>()
    val viewpager           = ObservableField<ViewPager>()
    val brsSetting          = ObservableField<WebViewSettingParams>()
    val viewpagerPageLimit  = ObservableInt(3)

    val visibleBack         = ObservableInt(View.GONE)

    // viewpager 에 adapter 가 set 된 이후 시점을 알려줌 (ViewPagerBindingAdapter)
    val viewpagerLoadedEvent        = ObservableField<()->Unit>()
    // appbar 에 offsetChanged 를 알려줌 (AppBarBindingAdapter)
    val appbarOffsetChangedEvent    = ObservableField<(AppBarLayout, Int)->Unit>()

    var gotoNewsEvent       = ObservableInt(0)
    val gotoSearchEvent     = SingleLiveEvent<Void>()

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
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }

    fun webviewBack() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }
}
