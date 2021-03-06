package com.example.clone_daum.di.module

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import brigitte.di.dagger.scope.ActivityScope
import com.example.clone_daum.MainActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Named

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module(includes = [])
abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [
        MainActivityModule::class
    ])
    abstract fun contributeMainActivity(): MainActivity
}

// https://stackoverflow.com/questions/48533899/how-to-inject-members-in-baseactivity-using-dagger-android
@Module(includes = [
    FragmentModule::class
])
abstract class MainActivityModule {
    // 라이브러리 쪽에서 Activity 를 이용하기 위해서 반드시 설정해야 함
    @Binds
    abstract fun bindFragmentActivity(activity: MainActivity): FragmentActivity

    @Module
    companion object {
        @Provides
        @JvmStatic
        @Named("activityFragmentManager")
        fun provideFragmentManager(activity: MainActivity) =
            activity.supportFragmentManager
    }
}