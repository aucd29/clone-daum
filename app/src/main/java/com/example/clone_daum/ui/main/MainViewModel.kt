package com.example.clone_daum.ui.main

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager.widget.ViewPager
import org.slf4j.LoggerFactory


class MainViewModel(val app: Application) : AndroidViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)
    }

    val tabAdapter              = ObservableField<TabAdapter>()
    val viewpager               = ObservableField<ViewPager>()
    val searchKeyword           = ObservableField<String>()
    val viewpagerLoadedEvent    = ObservableField<()->Unit>()

    fun gotoNews() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
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
}
