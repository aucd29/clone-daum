package brigitte.di.dagger.module

import androidx.fragment.app.Fragment
import dagger.MapKey
import javax.inject.Qualifier
import kotlin.reflect.KClass

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-27 <p/>
 */

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ChildFragmentManager(val value: String = "")


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MapKey
annotation class FragmentKey(val value: KClass<out Fragment>)
