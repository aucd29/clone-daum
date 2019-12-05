package com.example.clone_daum.di.module

import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.clone_daum.ui.browser.BrowserSubmenuFragment
import com.example.clone_daum.ui.browser.favorite.*
import com.example.clone_daum.ui.browser.urlhistory.UrlHistoryFragment
import com.example.clone_daum.ui.main.MainFragment
import com.example.clone_daum.ui.main.MainWebviewFragment
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
//import com.example.clone_daum.ui.main.navigation.cafe.CafeFragment
//import com.example.clone_daum.ui.main.navigation.mail.MailFragment
//import com.example.clone_daum.ui.main.navigation.shortcut.ShortcutFragment
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueChildFragment
import com.example.clone_daum.ui.main.setting.SettingFragment
import com.example.clone_daum.ui.main.weather.WeatherFragment
import com.example.clone_daum.ui.search.SearchFragment
import dagger.Binds
import dagger.Module

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [
    // REALTIME ISSUE
    RealtimeIssueChildFragment.Module::class,

    // WEATHER
    WeatherFragment.Module::class,

    // MEDIA SEARCH
    MediaSearchFragment.Module::class,
    SpeechFragment.Module::class,
    MusicFragment.Module::class,
    FlowerFragment.Module::class,
    BarcodeFragment.Module::class,
    BarcodeInputFragment.Module::class,

    // NAVIGATION
    NavigationFragment.Module::class,
    CafeFragment.Module::class,
    MailFragment.Module::class,
    BookmarkFragment.Module::class,
//    ShortcutFragment.Module::class,

    // SETTING
    SettingFragment.Module::class,

    // HOME MENU
    HomeMenuFragment.Module::class,

    // HOME TEXT
    HomeTextFragment.Module::class,

    // LOGIN FRAGMENT
    LoginFragment.Module::class,

    // ALARM FRAGMENT
    AlarmFragment.Module::class,

    // SEARCH
    SearchFragment.Module::class,

    // BROWSER,
    BrowserFragment.Module::class,
    BrowserSubmenuFragment.Module::class,

    // FAVORITE
    FavoriteFragment.Module::class,
    FavoriteModifyFragment.Module::class,
    FavoriteFolderFragment.Module::class,
    FavoriteProcessFragment.Module::class,
    FolderFragment.Module::class,

    // URL HISTORY
    UrlHistoryFragment.Module::class,

    // MAIN
    MainFragment.Module::class,
    MainWebviewFragment.Module::class
])
abstract class FragmentModule