package com.example.clone_daum.ui.main

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.common.Config
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import com.google.android.material.appbar.AppBarLayout
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R
import java.util.*


class MainViewModel @Inject constructor(val app: Application
    , val config: Config
) : CommandEventViewModel(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModel::class.java)

        const val INDEX_NEWS = 1

        const val CMD_SEARCH_FRAMGNET         = "search"
        const val CMD_NAVIGATION_FRAGMENT     = "navigation"
        const val CMD_REALTIME_ISSUE_FRAGMENT = "realtime-issue"
        const val CMD_MEDIA_SEARCH_FRAGMENT   = "media-search"

        const val CMD_BRS_OPEN                = "brs-open"
    }

    val tabAdapter              = ObservableField<MainTabAdapter>()
    val viewpager               = ObservableField<ViewPager>()
    var gotoNewsEvent           = ObservableInt(0)
    val searchIconResId         = ObservableInt(config.SEARCH_ICON)

    val visibleBack             = ObservableInt(View.GONE)

    val appbarOffsetChangedLive = ObservableField<(AppBarLayout, Int) -> Unit>()
    val appbarOffsetLive        = MutableLiveData<Int>()

    var progressViewOffsetLive  = MutableLiveData<Int>()
    var currentTabPositionLive  = MutableLiveData<Int>()

    var searchAreaHeight:Int    = 0

    fun eventGotoNews() = gotoNewsEvent.run {
        if (mLog.isDebugEnabled) {
            mLog.debug("GOTO TAB $INDEX_NEWS")
        }

        when (get()) {
            INDEX_NEWS -> notifyChange()
            else       -> set(INDEX_NEWS)
        }
    }

    fun webviewBack() {
        if (mLog.isDebugEnabled) {
            mLog.debug("")
        }
    }
}
