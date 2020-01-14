package com.example.clone_daum.ui

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import brigitte.*
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.clone_daum.ui.browser.BrowserSubmenuFragment
import com.example.clone_daum.ui.browser.favorite.*
import com.example.clone_daum.ui.browser.urlhistory.UrlHistoryFragment
import com.example.clone_daum.ui.main.MainFragment
import com.example.clone_daum.ui.main.alarm.AlarmFragment
import com.example.clone_daum.ui.main.bookmark.BookmarkFragment
import com.example.clone_daum.ui.main.homemenu.HomeMenuFragment
import com.example.clone_daum.ui.main.hometext.HomeTextFragment
import com.example.clone_daum.ui.main.login.LoginFragment
import com.example.clone_daum.ui.main.mediasearch.MediaSearchFragment
import com.example.clone_daum.ui.main.mediasearch.barcode.BarcodeFragment
import com.example.clone_daum.ui.main.mediasearch.barcode.BarcodeInputFragment
import com.example.clone_daum.ui.main.mediasearch.flower.FlowerFragment
import com.example.clone_daum.ui.main.mediasearch.music.MusicFragment
import com.example.clone_daum.ui.main.mediasearch.speech.SpeechFragment
import com.example.clone_daum.ui.main.navigation.NavigationFragment
import com.example.clone_daum.ui.main.navigation.cafe.CafeFragment
import com.example.clone_daum.ui.main.navigation.mail.MailFragment
import com.example.clone_daum.ui.main.setting.SettingFragment
import com.example.clone_daum.ui.main.setting.alarmpreference.AlarmPreferenceFragment
import com.example.clone_daum.ui.main.setting.alarmsetting.AlarmSettingFragment
import com.example.clone_daum.ui.main.setting.daumappinfo.DaumAppInfoFragment
import com.example.clone_daum.ui.main.setting.filemanager.DownloadPathFragment
import com.example.clone_daum.ui.main.setting.privacypolicy.PrivacyPolicyFragment
import com.example.clone_daum.ui.main.setting.research.ResearchFragment
import com.example.clone_daum.ui.main.setting.userhistory.UserHistoryFragment
import com.example.clone_daum.ui.map.DaummapFragment
import com.example.clone_daum.ui.search.SearchFragment
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 13. <p/>
 *
 * 구글에서 제공하고 있는 navigation 를 써보려고 했는데 add 없이 replace 만 존재하고 이와 관련되어
 * 딱히 문제점을 못 느끼고 있기에 navigation 는 버리고 원래대로 사용하는 식으로 재 변경
 */
class Navigator @Inject constructor(
    @param:Named("activityFragmentManager") val manager: FragmentManager
) {
    companion object {
        private val logger = LoggerFactory.getLogger(Navigator::class.java)

        const val CONTAINER          = R.id.rootContainer
        const val FAVORITE_CONTAINER = R.id.favorite_container
    }

    fun mainFragment() {
        if (logger.isInfoEnabled) {
            logger.info("MAIN FRAGMENT")
        }

        manager.show<MainFragment>(FragmentParams(CONTAINER, commit = FragmentCommit.NOW,
            backStack = false))
    }

    fun searchFragment() {
        if (logger.isInfoEnabled) {
            logger.info("SEARCH FRAGMENT")
        }

        manager.show<SearchFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.ALPHA))
    }

    fun navigationFragment() {
        if (logger.isInfoEnabled) {
            logger.info("NAVIGATION FRAGMENT")
        }

        manager.show<NavigationFragment>(FragmentParams(CONTAINER))
    }

    fun settingFragment() {
        if (logger.isInfoEnabled) {
            logger.info("SETTING FRAGMENT")
        }

        manager.show<SettingFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun privacyPolicyFragment() {
        if (logger.isInfoEnabled) {
            logger.info("PrivacyPolicyFragment")
        }

        manager.show<PrivacyPolicyFragment>(FragmentParams(CONTAINER))
    }

    fun alarmSettingFragment() {
        if (logger.isInfoEnabled) {
            logger.info("AlarmSettingFragment")
        }

        manager.show<AlarmSettingFragment>(FragmentParams(CONTAINER))
    }

    fun alarmPreferenceFragment() {
        if (logger.isInfoEnabled) {
            logger.info("AlarmPreferenceFragment")
        }

        manager.show<AlarmPreferenceFragment>(FragmentParams(CONTAINER))
    }

    fun userHistoryFragment() {
        if (logger.isInfoEnabled) {
            logger.info("UserHistoryFragment")
        }

        manager.show<UserHistoryFragment>(FragmentParams(CONTAINER))
    }

    fun downloadPathFragment(): DownloadPathFragment? {
        if (logger.isInfoEnabled) {
            logger.info("DownloadPathFragment")
        }

        return manager.show<DownloadPathFragment>(FragmentParams(CONTAINER)) as DownloadPathFragment
    }

    fun daumAppInfoFragment() {
        if (logger.isInfoEnabled) {
            logger.info("DaumAppInfoFragment")
        }

        manager.show<DaumAppInfoFragment>(FragmentParams(CONTAINER))
    }

    fun researchFragment() {
        if (logger.isInfoEnabled) {
            logger.info("ResearchFragment")
        }

        manager.show<ResearchFragment>(FragmentParams(CONTAINER))
    }

    fun homeMenuFragment() {
        if (logger.isInfoEnabled) {
            logger.info("EDIT HOME MENU FRAGMENT")
        }

        manager.show<HomeMenuFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun homeTextFragment() {
        if (logger.isInfoEnabled) {
            logger.info("RESIZE HOME TEXT FRAGMENT")
        }

        manager.showDialog(HomeTextFragment(), HomeTextFragment::class.java.simpleName)
    }

    fun mailFragment() {
        if (logger.isInfoEnabled) {
            logger.info("MAIL FRAGMENT")
        }

        manager.show<MailFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun cafeFragment() {
        if (logger.isInfoEnabled) {
            logger.info("CAFE FRAGMENT")
        }

        manager.show<CafeFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun loginFragment() {
        if (logger.isInfoEnabled) {
            logger.info("LOGIN FRAGMENT")
        }

        manager.show<LoginFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun alarmFragment() {
        if (logger.isInfoEnabled) {
            logger.info("ALARM FRAGMENT")
        }

        manager.show<AlarmFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun bookmarkFragment() {
        if (logger.isInfoEnabled) {
            logger.info("BOOKMARK FRAGMENT")
        }

        manager.show<BookmarkFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun mediaSearchFragment() {
        if (logger.isInfoEnabled) {
            logger.info("MEDIA SEARCH FRAGMENT")
        }

        manager.show<MediaSearchFragment>(FragmentParams(CONTAINER))
    }

    fun browserFragment(url: String?, finishInclusive: Boolean = false) {
        if (logger.isInfoEnabled) {
            logger.info("BROWSER FRAGMENT $url")
        }

        if (url == null) {
            logger.error("ERROR: URL == NULL")

            return
        }

        manager.show<BrowserFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.ALPHA,
            bundle = Bundle().apply {
                putString(BrowserFragment.K_URL, url)
                putBoolean(BrowserFragment.K_FINISH_INCLUSIVE, finishInclusive)
            }))
    }

    fun speechFragment() {
        if (logger.isInfoEnabled) {
            logger.info("SPEECH FRAGMENT")
        }

        manager.show<SpeechFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun musicFragment() {
        if (logger.isInfoEnabled) {
            logger.info("MUSIC FRAGMENT")
        }

        manager.show<MusicFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun flowerFragment() {
        if (logger.isInfoEnabled) {
            logger.info("FLOWER FRAGMENT")
        }

        manager.show<FlowerFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun barcodeFragment() {
        if (logger.isInfoEnabled) {
            logger.info("BARCODE FRAGMENT")
        }

        manager.show<BarcodeFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun barcodeInputFragment() {
        if (logger.isInfoEnabled) {
            logger.info("BARCODE INPUT FRAGMENT")
        }

        manager.show<BarcodeInputFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun browserSubFragment() {
        if (logger.isInfoEnabled) {
            logger.info("BROWSER SUBMENU FRAGMENT")
        }

        manager.showDialog(BrowserSubmenuFragment(), "brs-submenu")
    }

    fun favoriteFragment() {
        if (logger.isInfoEnabled) {
            logger.info("FAVORITE FRAGMENT")
        }

        manager.show<FavoriteFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun favoriteModifyFragment(folderId: Int = 0) {
        if (logger.isInfoEnabled) {
            logger.info("FAVORITE MODIFY FRAGMENT ($folderId)")
        }

        manager.show<FavoriteModifyFragment>(FragmentParams(CONTAINER,
            bundle = Bundle().apply {
                putInt(FavoriteModifyFragment.K_FOLDER, folderId)
            }))
    }

    fun favoriteFolderFragment(folderId: Int = 0) {
        if (logger.isInfoEnabled) {
            logger.info("FAVORITE FOLDER FRAGMENT ($folderId)")
        }

        manager.show<FavoriteFolderFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT,
            bundle = Bundle().apply {
                putInt(FavoriteFolderFragment.K_FOLDER, folderId)
            }))
    }

    fun favoriteProcessFragment(title: String, url: String) {
        if (logger.isInfoEnabled) {
            logger.info("FAVORITE PROCESS(ADD) FRAGMENT")
        }

        manager.show<FavoriteProcessFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT,
            bundle = Bundle().apply {
                putString(FavoriteProcessFragment.K_TITLE, title)
                putString(FavoriteProcessFragment.K_URL, url)
            }))
    }

    fun favoriteProcessFragment(favorite: MyFavorite) {
        if (logger.isInfoEnabled) {
            logger.info("FAVORITE PROCESS(MODIFY) FRAGMENT")
        }

        // add fragment 로 ui 를 대신해 봄
        manager.show<FavoriteProcessFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT,
            bundle = Bundle().apply {
                putSerializable(FavoriteProcessFragment.K_MODIFY, favorite)
            }))
    }

    fun folderFragment(child: FragmentManager, currentFolderId: Int = 0, container: Int = FAVORITE_CONTAINER) {
        if (logger.isInfoEnabled) {
            logger.info("FOLDER FRAGMENT ($currentFolderId)")
        }

        child.show<FolderFragment>(FragmentParams(container,
            anim = FragmentAnim.RIGHT,
            bundle = Bundle().apply {
                putInt(FolderFragment.K_CURRENT_FOLDER, currentFolderId)
            }))
    }

    fun urlHistoryFragment() {
        if (logger.isInfoEnabled) {
            logger.info("URL HISTORY FRAGMENT")
        }

        manager.show<UrlHistoryFragment>(FragmentParams(CONTAINER,
            anim = FragmentAnim.RIGHT))
    }

    fun mapFragment() {
        if (logger.isInfoEnabled) {
            logger.info("MapFragment")
        }

        manager.show<DaummapFragment>(FragmentParams(CONTAINER))
    }
}
