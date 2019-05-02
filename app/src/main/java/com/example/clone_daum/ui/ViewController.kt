package com.example.clone_daum.ui

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.clone_daum.ui.browser.BrowserSubmenuFragment
import com.example.clone_daum.ui.browser.favorite.*
import com.example.clone_daum.ui.browser.urlhistory.UrlHistoryFragment
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
import com.example.clone_daum.ui.search.SearchFragment
import brigitte.*
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 13. <p/>
 */
class ViewController @Inject constructor(val manager: FragmentManager) {
    // 나만의 룰을 만들었더니만 navigation editor 나와버림 =_ = ㅋ

    companion object {
        private val mLog = LoggerFactory.getLogger(ViewController::class.java)

        const val CONTAINER          = R.id.container
        const val NAV_TAB_CONTAINER  = R.id.navi_tab_container
        const val FAVORITE_CONTAINER = R.id.favorite_container
    }

    fun mainFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("MAIN FRAGMENT")
        }

        manager.show<MainFragment>(FragmentParams(CONTAINER
            , commit = FragmentCommit.NOW
            , backStack = false))
    }

    fun mediaSearchFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("MEDIA SEARCH FRAGMENT")
        }

        manager.show<MediaSearchFragment>(FragmentParams(CONTAINER))
    }

    fun speechFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("SPEECH FRAGMENT")
        }

        manager.show<SpeechFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT))
    }

    fun musicFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("MUSIC FRAGMENT")
        }

        manager.show<MusicFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT))
    }

    fun flowerFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("FLOWER FRAGMENT")
        }

        manager.show<FlowerFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT))
    }

    fun barcodeFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("BARCORD FRAGMENT")
        }

        manager.show<BarcodeFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT))
    }

    fun barcodeInputFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("BARCODE INPUT FRAGMENT")
        }

        manager.show<BarcodeInputFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT))
    }

    fun navigationFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("NAVIGATION FRAGMENT")
        }

        manager.show<NavigationFragment>(FragmentParams(CONTAINER))
    }

    fun cafeFragment(child: FragmentManager) {
        if (mLog.isInfoEnabled) {
            mLog.info("CAFE FRAGMENT")
        }

        child.show<CafeFragment>(FragmentParams(NAV_TAB_CONTAINER
            , add = false))
    }

    fun mailFragment(child: FragmentManager) {
        if (mLog.isInfoEnabled) {
            mLog.info("MAIL FRAGMENT")
        }

        child.show<MailFragment>(FragmentParams(NAV_TAB_CONTAINER
            , add = false))
    }

    fun shortcutFragment(child: FragmentManager, add: Boolean = false) {
        if (mLog.isInfoEnabled) {
            mLog.info("SHORTCUT FRAGMENT")
        }

        child.show<ShortcutFragment>(FragmentParams(NAV_TAB_CONTAINER
            , add = add))
    }

    fun searchFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("SEARCH FRAGMENT")
        }

        manager.show<SearchFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.ALPHA))
    }

    fun browserFragment(url: String?) {
        if (mLog.isInfoEnabled) {
            mLog.info("BROWSER FRAGMENT $url")
        }

        if (url == null) {
            mLog.error("ERROR: URL == NULL")

            return
        }

        manager.show<BrowserFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.ALPHA
            , bundle = Bundle().apply {
                putString(BrowserFragment.K_URL, url)
            }))
    }

    fun browserSubFragment(callback: (String) -> Unit) {
        if (mLog.isInfoEnabled) {
            mLog.info("BROWSER SUBMENU FRAGMENT")
        }

        manager.showDialog(BrowserSubmenuFragment(callback), "brs-submenu")
    }

    fun favoriteFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE FRAGMENT")
        }

        manager.show<FavoriteFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT))
    }

    fun favoriteModifyFragment(folderId: Int = 0) {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE MODIFY FRAGMENT ($folderId)")
        }

        manager.show<FavoriteModifyFragment>(FragmentParams(CONTAINER
            , bundle = Bundle().apply {
                putInt(FavoriteModifyFragment.K_FOLDER, folderId)
            }))
    }

    fun favoriteFolderFragment(folderId: Int = 0) {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE FOLDER FRAGMENT ($folderId)")
        }

        manager.show<FavoriteFolderFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT
            , bundle = Bundle().apply {
                putInt(FavoriteFolderFragment.K_FOLDER, folderId)
            }))
    }

    fun favoriteProcessFragment(title: String, url: String) {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE PROCESS(ADD) FRAGMENT")
        }

        manager.show<FavoriteProcessFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT
            , bundle = Bundle().apply {
                putString(FavoriteProcessFragment.K_TITLE, title)
                putString(FavoriteProcessFragment.K_URL, url)
            }))
    }

    fun favoriteProcessFragment(favorite: MyFavorite) {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE PROCESS(MODIFY) FRAGMENT")
        }

        // add fragment 로 ui 를 대신해 봄
        manager.show<FavoriteProcessFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT
            , bundle = Bundle().apply {
                putSerializable(FavoriteProcessFragment.K_MODIFY, favorite)
            }))
    }

    fun folderFragment(child: FragmentManager, currentFolderId: Int = 0, container: Int = FAVORITE_CONTAINER) {
        if (mLog.isInfoEnabled) {
            mLog.info("FOLDER FRAGMENT (${currentFolderId})")
        }

        child.show<FolderFragment>(FragmentParams(container
            , anim = FragmentAnim.RIGHT
            , bundle = Bundle().apply {
                putInt(FolderFragment.K_CURRENT_FOLDER, currentFolderId)
            }))
    }

    fun urlHistoryFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("URL HISTORY FRAGMENT")
        }

        manager.show<UrlHistoryFragment>(FragmentParams(CONTAINER
            , anim = FragmentAnim.RIGHT))
    }

}