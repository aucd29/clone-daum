package com.example.clone_daum.ui

import androidx.fragment.app.FragmentManager
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import dagger.Binds
import dagger.Module
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 13. <p/>
 */
@Singleton
class FragmentFactory @Inject constructor(

) {
    companion object {
        private val mLog = LoggerFactory.getLogger(FragmentFactory::class.java)

//        const val CONTAINER          = 0 //R.id.rootContainer
//        const val NAV_TAB_CONTAINER  = R.id.navi_tab_container
        const val FAVORITE_CONTAINER = R.id.favorite_container
    }

    fun mainFragment(manager: FragmentManager?) {
        if (mLog.isInfoEnabled) {
            mLog.info("MAIN FRAGMENT")
        }

//        manager?.showBy(FragmentParams(CONTAINER, commit = FragmentCommit.NOW,
//            fragment = creator[MainFragment::class.java]?.get(), backStack = false))
    }

    fun searchFragment(manager: FragmentManager?) {
        if (mLog.isInfoEnabled) {
            mLog.info("SEARCH FRAGMENT")
        }

//        manager?.showBy(FragmentParams(CONTAINER,
//            fragment = creator[SearchFragment::class.java]?.get(), anim = FragmentAnim.ALPHA))
    }

    fun navigationFragment(manager: FragmentManager?) {
        if (mLog.isInfoEnabled) {
            mLog.info("NAVIGATION FRAGMENT")
        }

//        manager?.showBy(FragmentParams(CONTAINER,
//            fragment = creator[NavigationFragment::class.java]?.get()))
    }

    fun mediaSearchFragment(manager: FragmentManager?) {
        if (mLog.isInfoEnabled) {
            mLog.info("MEDIA SEARCH FRAGMENT")
        }

//        manager?.showBy(FragmentParams(CONTAINER,
//            fragment = creator[NavigationFragment::class.java]?.get()))
    }

    fun browserFragment(manager: FragmentManager?, url: String?) {
        if (mLog.isInfoEnabled) {
            mLog.info("BROWSER FRAGMENT $url")
        }

        if (url == null) {
            mLog.error("ERROR: URL == NULL")

            return
        }

//        manager?.showBy(FragmentParams(CONTAINER,
//            fragment = creator[BrowserFragment::class.java]?.get(), anim = FragmentAnim.ALPHA,
//            bundle = Bundle().apply {
//                putString(BrowserFragment.K_URL, url)
//            }))
    }

    fun speechFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("SPEECH FRAGMENT")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mSpeechFragment.get(), anim = FragmentAnim.RIGHT))
    }

    fun musicFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("MUSIC FRAGMENT")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mMusicFragment.get(), anim = FragmentAnim.RIGHT))
    }

    fun flowerFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("FLOWER FRAGMENT")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFlowerFragment.get(), anim = FragmentAnim.RIGHT))
    }

    fun barcodeFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("BARCODE FRAGMENT")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mBarcodeFragment.get(), anim = FragmentAnim.RIGHT))
    }

    fun barcodeInputFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("BARCODE INPUT FRAGMENT")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mBarcodeInputFragment.get(), anim = FragmentAnim.RIGHT))
    }


    fun cafeFragment(child: FragmentManager) {
        if (mLog.isInfoEnabled) {
            mLog.info("CAFE FRAGMENT")
        }

//        child.showBy(FragmentParams(NAV_TAB_CONTAINER,
//            fragment = mCafeFragment.get(), add = false))
    }

    fun mailFragment(child: FragmentManager) {
        if (mLog.isInfoEnabled) {
            mLog.info("MAIL FRAGMENT")
        }

//        child.showBy(FragmentParams(NAV_TAB_CONTAINER,
//            fragment = mMailFragment.get(), add = false))
    }

    fun shortcutFragment(child: FragmentManager, add: Boolean = false) {
        if (mLog.isInfoEnabled) {
            mLog.info("SHORTCUT FRAGMENT")
        }

//        child.showBy(FragmentParams(NAV_TAB_CONTAINER,
//            fragment = mShortcutFragment.get(), add = add))
    }

    fun browserSubFragment(callback: (String) -> Unit) {
        if (mLog.isInfoEnabled) {
            mLog.info("BROWSER SUBMENU FRAGMENT")
        }

//        manager.showDialog(BrowserSubmenuFragment(callback), "brs-submenu")
    }

    fun favoriteFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE FRAGMENT")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteFragment.get(), anim = FragmentAnim.RIGHT))
    }

    fun favoriteModifyFragment(folderId: Int = 0) {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE MODIFY FRAGMENT ($folderId)")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteModifyFragment.get(), bundle = Bundle().apply {
//                putInt(FavoriteModifyFragment.K_FOLDER, folderId)
//            }))
    }

    fun favoriteFolderFragment(folderId: Int = 0) {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE FOLDER FRAGMENT ($folderId)")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteFolderFragment.get(), anim = FragmentAnim.RIGHT,
//            bundle = Bundle().apply {
//                putInt(FavoriteFolderFragment.K_FOLDER, folderId)
//            }))
    }

    fun favoriteProcessFragment(title: String, url: String) {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE PROCESS(ADD) FRAGMENT")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteProcessFragment.get(), anim = FragmentAnim.RIGHT,
//            bundle = Bundle().apply {
//                putString(FavoriteProcessFragment.K_TITLE, title)
//                putString(FavoriteProcessFragment.K_URL, url)
//            }))
    }

    fun favoriteProcessFragment(favorite: MyFavorite) {
        if (mLog.isInfoEnabled) {
            mLog.info("FAVORITE PROCESS(MODIFY) FRAGMENT")
        }

        // add fragment 로 ui 를 대신해 봄
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteProcessFragment.get(), anim = FragmentAnim.RIGHT,
//            bundle = Bundle().apply {
//                putSerializable(FavoriteProcessFragment.K_MODIFY, favorite)
//            }))
    }

    fun folderFragment(child: FragmentManager, currentFolderId: Int = 0, container: Int = FAVORITE_CONTAINER) {
        if (mLog.isInfoEnabled) {
            mLog.info("FOLDER FRAGMENT ($currentFolderId)")
        }

//        child.showBy(FragmentParams(container,
//            fragment = mFolderFragment.get(), anim = FragmentAnim.RIGHT,
//            bundle = Bundle().apply {
//                putInt(FolderFragment.K_CURRENT_FOLDER, currentFolderId)
//            }))
    }

    fun urlHistoryFragment() {
        if (mLog.isInfoEnabled) {
            mLog.info("URL HISTORY FRAGMENT")
        }

//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mUrlHistoryFragment.get(), anim = FragmentAnim.RIGHT))
    }
}

////@Singleton
//class FragmentFactory @Inject constructor(
//    private val manager: FragmentManager
//) {
//    companion object {
//        private val mLog = LoggerFactory.getLogger(FragmentFactory::class.java)
//
//        const val CONTAINER          = R.id.rootContainer
//        const val NAV_TAB_CONTAINER  = R.id.navi_tab_container
//        const val FAVORITE_CONTAINER = R.id.favorite_container
//    }
//
//    // 나만의 룰을 만들었더니만 navigation editor 나와버림 =_ = ㅋ
//
//    @Inject lateinit var mMainFragment: Provider<MainFragment>
//    @Inject lateinit var mMediaSearchFragment: Provider<MediaSearchFragment>
//    @Inject lateinit var mSpeechFragment: Provider<SpeechFragment>
//    @Inject lateinit var mMusicFragment: Provider<MusicFragment>
//    @Inject lateinit var mFlowerFragment: Provider<FlowerFragment>
//    @Inject lateinit var mBarcodeFragment: Provider<BarcodeFragment>
//    @Inject lateinit var mBarcodeInputFragment: Provider<BarcodeInputFragment>
//    @Inject lateinit var mNavigationFragment: Provider<NavigationFragment>
//    @Inject lateinit var mCafeFragment: Provider<CafeFragment>
//    @Inject lateinit var mMailFragment: Provider<MailFragment>
//    @Inject lateinit var mShortcutFragment: Provider<ShortcutFragment>
//    @Inject lateinit var mSearchFragment: Provider<SearchFragment>
//    @Inject lateinit var mBrowserFragment: Provider<BrowserFragment>
//    //    @Inject lateinit var mBrowserSubmenuFragment: Provider<BrowserSubmenuFragment>
//    @Inject lateinit var mFavoriteFragment: Provider<FavoriteFragment>
//    @Inject lateinit var mFavoriteModifyFragment: Provider<FavoriteModifyFragment>
//    @Inject lateinit var mFavoriteFolderFragment: Provider<FavoriteFolderFragment>
//    @Inject lateinit var mFavoriteProcessFragment: Provider<FavoriteProcessFragment>
//    @Inject lateinit var mFolderFragment: Provider<FolderFragment>
//    @Inject lateinit var mUrlHistoryFragment: Provider<UrlHistoryFragment>
//
//    fun mainFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("MAIN FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER, commit = FragmentCommit.NOW,
//            fragment  = mMainFragment.get(), backStack = false))
//    }
//
//    fun mediaSearchFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("MEDIA SEARCH FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mMediaSearchFragment.get()))
//    }
//
//    fun speechFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("SPEECH FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mSpeechFragment.get(), anim = FragmentAnim.RIGHT))
//    }
//
//    fun musicFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("MUSIC FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mMusicFragment.get(), anim = FragmentAnim.RIGHT))
//    }
//
//    fun flowerFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("FLOWER FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFlowerFragment.get(), anim = FragmentAnim.RIGHT))
//    }
//
//    fun barcodeFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("BARCODE FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mBarcodeFragment.get(), anim = FragmentAnim.RIGHT))
//    }
//
//    fun barcodeInputFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("BARCODE INPUT FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mBarcodeInputFragment.get(), anim = FragmentAnim.RIGHT))
//    }
//
//    fun navigationFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("NAVIGATION FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mNavigationFragment.get()))
//    }
//
//    fun cafeFragment(child: FragmentManager) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("CAFE FRAGMENT")
//        }
//
//        child.showBy(FragmentParams(NAV_TAB_CONTAINER,
//            fragment = mCafeFragment.get(), add = false))
//    }
//
//    fun mailFragment(child: FragmentManager) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("MAIL FRAGMENT")
//        }
//
//        child.showBy(FragmentParams(NAV_TAB_CONTAINER,
//            fragment = mMailFragment.get(), add = false))
//    }
//
//    fun shortcutFragment(child: FragmentManager, add: Boolean = false) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("SHORTCUT FRAGMENT")
//        }
//
//        child.showBy(FragmentParams(NAV_TAB_CONTAINER,
//            fragment = mShortcutFragment.get(), add = add))
//    }
//
//    fun searchFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("SEARCH FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mSearchFragment.get(), anim = FragmentAnim.ALPHA))
//    }
//
//    fun browserFragment(url: String?) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("BROWSER FRAGMENT $url")
//        }
//
//        if (url == null) {
//            mLog.error("ERROR: URL == NULL")
//
//            return
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mBrowserFragment.get(), anim = FragmentAnim.ALPHA,
//            bundle = Bundle().apply {
//                putString(BrowserFragment.K_URL, url)
//            }))
//    }
//
//    fun browserSubFragment(callback: (String) -> Unit) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("BROWSER SUBMENU FRAGMENT")
//        }
//
//        manager.showDialog(BrowserSubmenuFragment(callback), "brs-submenu")
//    }
//
//    fun favoriteFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("FAVORITE FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteFragment.get(), anim = FragmentAnim.RIGHT))
//    }
//
//    fun favoriteModifyFragment(folderId: Int = 0) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("FAVORITE MODIFY FRAGMENT ($folderId)")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteModifyFragment.get(), bundle = Bundle().apply {
//                putInt(FavoriteModifyFragment.K_FOLDER, folderId)
//            }))
//    }
//
//    fun favoriteFolderFragment(folderId: Int = 0) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("FAVORITE FOLDER FRAGMENT ($folderId)")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteFolderFragment.get(), anim = FragmentAnim.RIGHT,
//            bundle = Bundle().apply {
//                putInt(FavoriteFolderFragment.K_FOLDER, folderId)
//            }))
//    }
//
//    fun favoriteProcessFragment(title: String, url: String) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("FAVORITE PROCESS(ADD) FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteProcessFragment.get(), anim = FragmentAnim.RIGHT,
//            bundle = Bundle().apply {
//                putString(FavoriteProcessFragment.K_TITLE, title)
//                putString(FavoriteProcessFragment.K_URL, url)
//            }))
//    }
//
//    fun favoriteProcessFragment(favorite: MyFavorite) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("FAVORITE PROCESS(MODIFY) FRAGMENT")
//        }
//
//        // add fragment 로 ui 를 대신해 봄
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mFavoriteProcessFragment.get(), anim = FragmentAnim.RIGHT,
//            bundle = Bundle().apply {
//                putSerializable(FavoriteProcessFragment.K_MODIFY, favorite)
//            }))
//    }
//
//    fun folderFragment(child: FragmentManager, currentFolderId: Int = 0, container: Int = FAVORITE_CONTAINER) {
//        if (mLog.isInfoEnabled) {
//            mLog.info("FOLDER FRAGMENT ($currentFolderId)")
//        }
//
//        child.showBy(FragmentParams(container,
//            fragment = mFolderFragment.get(), anim = FragmentAnim.RIGHT,
//            bundle = Bundle().apply {
//                putInt(FolderFragment.K_CURRENT_FOLDER, currentFolderId)
//            }))
//    }
//
//    fun urlHistoryFragment() {
//        if (mLog.isInfoEnabled) {
//            mLog.info("URL HISTORY FRAGMENT")
//        }
//
//        manager.showBy(FragmentParams(CONTAINER,
//            fragment = mUrlHistoryFragment.get(), anim = FragmentAnim.RIGHT))
//    }
//}