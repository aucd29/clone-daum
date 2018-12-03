package com.example.clone_daum.ui.main

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager.widget.ViewPager
import com.example.common.arch.SingleLiveEvent
import com.google.android.material.appbar.AppBarLayout
import org.slf4j.LoggerFactory


class MainViewModel(val app: Application) : AndroidViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1
    }

    val tabAdapter                  = ObservableField<TabAdapter>()
    val viewpager                   = ObservableField<ViewPager>()
    val searchKeyword               = ObservableField<String>()
    val viewpagerLoadedEvent        = ObservableField<()->Unit>()
    val appbarOffsetChangedEvent    = ObservableField<(AppBarLayout, Int)->Unit>()

    var gotoNewsEvent               = ObservableInt(0)

    val visibleBack                 = ObservableInt(View.GONE)

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
