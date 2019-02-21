package com.example.clone_daum.ui

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.clone_daum.R
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.clone_daum.ui.browser.BrowserSubmenuFragment
import com.example.clone_daum.ui.main.MainFragment
import com.example.clone_daum.ui.main.mediasearch.MediaSearchFragment
import com.example.clone_daum.ui.main.mediasearch.barcode.BarcodeFragment
import com.example.clone_daum.ui.main.mediasearch.barcode.BarcodeInputFragment
import com.example.clone_daum.ui.main.mediasearch.flower.FlowerFragment
import com.example.clone_daum.ui.main.mediasearch.music.MusicFragment
import com.example.clone_daum.ui.main.mediasearch.speech.SpeechFragment
import com.example.clone_daum.ui.main.navigation.NavigationFragment
import com.example.clone_daum.ui.main.navigation.cafe.CafeFragment
import com.example.clone_daum.ui.main.navigation.mail.MailFragment
import com.example.clone_daum.ui.main.navigation.shortcut.ShortcutFragment
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueFragment
import com.example.clone_daum.ui.main.weather.WeatherFragment
import com.example.clone_daum.ui.search.SearchFragment
import com.example.common.*
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 13. <p/>
 */
class ViewController @Inject constructor(val manager: FragmentManager) {
    // 나만의 룰을 만들었더니만 navigation editor 나와버림 =_ = ㅋ

    companion object {
        private val mLog = LoggerFactory.getLogger(ViewController::class.java)

        const val CONTAINER         = R.id.container
        const val NAV_TAB_CONTAINER = R.id.navi_tab_container
    }

    fun mainFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("MAIN FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER,
            MainFragment::class.java, commit = FragmentCommit.NOW, backStack = false))
    }

    fun realtimeIssueFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("REALTIME ISSUE FRAGMENT")
        }

        manager.showDialog(RealtimeIssueFragment(), "realtime-issue")
    }

    fun weatherFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("WEATHER FRAGMENT")
        }

        manager.showDialog(WeatherFragment(), "weather")
    }

    fun mediaSearchFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("MEDIA SEARCH FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER, MediaSearchFragment::class.java))
    }

    fun speechFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("SPEECH FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER, SpeechFragment::class.java
            , anim = FragmentAnim.RIGHT))
    }

    fun musicFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("MUSIC FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER, MusicFragment::class.java
            , anim = FragmentAnim.RIGHT))
    }

    fun flowerFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("FLOWER FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER, FlowerFragment::class.java
            , anim = FragmentAnim.RIGHT))
    }

    fun barcodeFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("BARCORD FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER, BarcodeFragment::class.java
            , anim = FragmentAnim.RIGHT))
    }

    fun barcodeInputFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("BARCODE INPUT FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER, BarcodeInputFragment::class.java
            , anim = FragmentAnim.RIGHT))
    }

    fun navigationFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("NAVIGATION FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER, NavigationFragment::class.java))
    }

    fun cafeFragment(child: FragmentManager) {
        if (mLog.isInfoEnabled) {
            mLog.info("CAFE FRAGMENT")
        }

        child.show(FragmentParams(NAV_TAB_CONTAINER, CafeFragment::class.java, add = false))
    }

    fun mailFragment(child: FragmentManager) {
        if (mLog.isInfoEnabled) {
            mLog.info("MAIL FRAGMENT")
        }

        child.show(FragmentParams(NAV_TAB_CONTAINER, MailFragment::class.java, add = false))
    }

    fun shortcutFragment(child: FragmentManager, add: Boolean = false) {
        if (mLog.isInfoEnabled) {
            mLog.info("SHORTCUT FRAGMENT")
        }

        child.show(FragmentParams(NAV_TAB_CONTAINER, ShortcutFragment::class.java, add = add))
    }

    fun searchFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("SEARCH FRAGMENT")
        }

        manager.show(FragmentParams(CONTAINER,
            SearchFragment::class.java, anim = FragmentAnim.ALPHA))
    }

    fun browserFragment(url: String?) {
        if (mLog.isInfoEnabled) {
            mLog.info("BROWSER FRAGMENT $url")
        }

        url?.let {
            val bundle = Bundle()
            bundle.putString("url", it)

            manager.show(FragmentParams(CONTAINER,
                BrowserFragment::class.java, anim = FragmentAnim.ALPHA, bundle = bundle))
        }
    }

    fun browserSubFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("BROWSER SUBMENU FRAGMENT")
        }

        manager.showDialog(BrowserSubmenuFragment(), "brs-submenu")
    }
}