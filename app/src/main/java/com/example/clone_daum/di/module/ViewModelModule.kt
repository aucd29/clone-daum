package com.example.clone_daum.di.module

import androidx.lifecycle.ViewModel
import com.example.clone_daum.ui.browser.BrowserSubmenuViewModel
import com.example.common.di.module.ViewModelKey
import com.example.clone_daum.ui.browser.BrowserViewModel
import com.example.clone_daum.ui.main.MainViewModel
import com.example.clone_daum.ui.main.SplashViewModel
import com.example.clone_daum.ui.main.mediasearch.MediaSearchViewModel
import com.example.clone_daum.ui.main.navigation.NavigationLoginViewModel
import com.example.clone_daum.ui.main.navigation.NavigationViewModel
import com.example.clone_daum.ui.main.navigation.cafe.CafeViewModel
import com.example.clone_daum.ui.main.navigation.mail.MailViewModel
import com.example.clone_daum.ui.main.navigation.shortcut.FrequentlySiteViewModel
import com.example.clone_daum.ui.main.navigation.shortcut.ShortcutViewModel
import com.example.clone_daum.ui.main.navigation.shortcut.SitemapViewModel
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueViewModel
import com.example.clone_daum.ui.main.weather.WeatherViewModel
import com.example.clone_daum.ui.search.PopularViewModel
import com.example.clone_daum.ui.search.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module
abstract class ViewModelModule {
    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MAIN
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(vm: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(vm: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RealtimeIssueViewModel::class)
    abstract fun bindRealtimeIssueViewModel(vm: RealtimeIssueViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    abstract fun bindWeatherViewModel(vm: WeatherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MediaSearchViewModel::class)
    abstract fun bindMediaSearchViewModel(vm: MediaSearchViewModel): ViewModel

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // NAVIATION
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @Binds
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    abstract fun bindNavigationViewModel(vm: NavigationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CafeViewModel::class)
    abstract fun bindCafeViewModel(vm: CafeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MailViewModel::class)
    abstract fun bindMailViewModel(vm: MailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShortcutViewModel::class)
    abstract fun bindShortcutViewModel(vm: ShortcutViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SitemapViewModel::class)
    abstract fun bindSitemapViewModel(vm: SitemapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FrequentlySiteViewModel::class)
    abstract fun bindFrequentlySiteViewModel(vm: FrequentlySiteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NavigationLoginViewModel::class)
    abstract fun bindNavigationLoginViewModel(vm: NavigationLoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(vm: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PopularViewModel::class)
    abstract fun bindPopularViewModel(vm: PopularViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BrowserViewModel::class)
    abstract fun bindBrowserViewModel(vm: BrowserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BrowserSubmenuViewModel::class)
    abstract fun bindBrowserSubmenuViewModel(vm: BrowserSubmenuViewModel): ViewModel
}
