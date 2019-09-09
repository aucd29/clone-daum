package com.example.clone_daum.di.module

import brigitte.di.dagger.module.RxModule
import brigitte.di.dagger.module.ViewModelAssistedFactoriesModule
import brigitte.di.dagger.module.ViewModelFactoryModule
import com.example.clone_daum.di.module.libs.CalligraphyModule
import com.example.clone_daum.di.module.libs.ChipModule
import com.example.clone_daum.di.module.libs.RxLocationModule
import dagger.Module

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [
    CalligraphyModule::class,
    ChipModule::class,
    RxLocationModule::class,
    RxModule::class,
    DbModule::class,
    ViewModelModule::class,
    AssistedViewModelModule::class,
    ViewModelFactoryModule::class,
    ViewModelAssistedFactoriesModule::class
])
class UtilModule