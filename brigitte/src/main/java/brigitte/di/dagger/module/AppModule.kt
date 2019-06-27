package brigitte.di.dagger.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun provideContext(app: Application): Context

    // https://stackoverflow.com/questions/48081881/dagger-2-not-injecting-sharedpreference
//    @Module
//    companion object {
//        @JvmStatic
//        @Provides
//        fun provideSharedPreference(context: Context)
//                = PreferenceManager.getDefaultSharedPreferences(context)
//    }
}