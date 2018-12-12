package com.example.clone_daum.di.module

import androidx.lifecycle.ViewModel
import com.example.clone_daum.di.module.common.ViewModelKey
import com.example.clone_daum.ui.browser.BrowserViewModel
import com.example.clone_daum.ui.main.MainViewModel
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
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(vm: MainViewModel): ViewModel

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

}