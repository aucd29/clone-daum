package brigitte.di.dagger.module

import javax.inject.Qualifier

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-27 <p/>
 */

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ChildFragmentManager(val value: String = "")
