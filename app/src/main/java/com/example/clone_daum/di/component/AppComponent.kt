package com.example.clone_daum.di.component

import android.app.Application
import com.example.clone_daum.MainApp
import com.example.clone_daum.di.migration.AndroidXInjectionModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Singleton
@Component(modules = arrayOf(AndroidXInjectionModule::class))
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }

    fun inject(app: MainApp)
}