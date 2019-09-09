package com.example.clone_daum.di.module

import androidx.lifecycle.ViewModel
import brigitte.di.dagger.module.AssistedViewModelKey
import brigitte.di.dagger.module.ViewModelAssistedFactory
import com.example.clone_daum.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-09 <p/>
 */

@Module
abstract class AssistedViewModelModule {
//    @Binds
//    @IntoMap
//    @AssistedViewModelKey(MainViewModel::class)
//    abstract fun bindMainViewModel(vm: MainViewModel): ViewModelAssistedFactory<out ViewModel>
}