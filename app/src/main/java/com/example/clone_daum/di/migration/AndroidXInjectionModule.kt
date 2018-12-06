package com.example.clone_daum.di.migration

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.internal.Beta
import dagger.multibindings.Multibinds

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Beta
@Module(includes = arrayOf(AndroidInjectionModule::class))
abstract class AndroidXInjectionModule private constructor() {
    @Multibinds
    internal abstract fun supportFragmentInjectorFactories():
            Map<Class<out Fragment>, AndroidInjector.Factory<out Fragment>>
}
