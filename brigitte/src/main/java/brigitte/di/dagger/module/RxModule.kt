package brigitte.di.dagger.module

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 7. <p/>
 */

@Module
class RxModule {
    @Singleton
    @Provides
    fun provideCompositeDisposable()
            = CompositeDisposable()
}