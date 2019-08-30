package brigitte.di.dagger.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module
abstract class ContextModule {
    @Binds
    abstract fun provideContext(app: Application): Context
}
