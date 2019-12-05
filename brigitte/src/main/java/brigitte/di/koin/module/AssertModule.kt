package brigitte.di.koin.module

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-05-22 <p/>
 */

val assertModule = module {
    single { androidContext().assets }
}