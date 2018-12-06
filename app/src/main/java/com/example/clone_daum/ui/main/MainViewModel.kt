package com.example.clone_daum.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.design.widget.AppBarLayout
import android.support.v4.view.ViewPager
import android.view.View
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory


class MainViewModel(val app: Application) : AndroidViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1
    }

    val tabAdapter                  = ObservableField<TabAdapter>()
    val viewpager                   = ObservableField<ViewPager>()

    // view events
    val viewpagerLoadedEvent        = ObservableField<()->Unit>()
    val appbarOffsetChangedEvent    = ObservableField<(AppBarLayout, Int)->Unit>()

    var gotoNewsEvent               = ObservableInt(0)
    val visibleBack                 = ObservableInt(View.GONE)

    val gotoSearchEvent             = SingleLiveEvent<Void>()

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
