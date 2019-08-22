@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.di.dagger.module

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.MapKey
import dagger.Module
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

/**
 * https://medium.com/@marco_cattaneo/android-mViewModel-and-factoryprovider-good-way-to-manage-it-with-dagger-2-d9e20a07084c
 */
@Singleton
class DaggerViewModelFactory @Inject constructor(
    private val creator: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creator[modelClass] ?:
        creator.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")

        return try {
            creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

//inline fun <reified T : ViewModel> DaggerViewModelFactory.injectOfActivity(activity: FragmentActivity) =
//    ViewModelProviders.of(activity, this).get(T::class.java)
//
//inline fun <reified T : ViewModel> DaggerViewModelFactory.injectOfActivity(fragment: Fragment) =
//    ViewModelProviders.of(fragment.activity!!, this).get(T::class.java)
//
//inline fun <reified T : ViewModel> DaggerViewModelFactory.injectOf(fragment: Fragment) =
//    ViewModelProviders.of(fragment, this).get(T::class.java)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory
}
