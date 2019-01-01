@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common.di.module

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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

inline fun <reified T : ViewModel> DaggerViewModelFactory.injectOfActivity(activity: FragmentActivity, clazz: Class<T>) =
    ViewModelProviders.of(activity, this).get(clazz)

inline fun <reified T : ViewModel> DaggerViewModelFactory.injectOfActivity(frgmt: Fragment, clazz: Class<T>) =
    ViewModelProviders.of(frgmt.activity!!, this).get(clazz)

inline fun <reified T : ViewModel> DaggerViewModelFactory.injectOf(frgmt: Fragment, clazz: Class<T>) =
    ViewModelProviders.of(frgmt, this).get(clazz)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)


@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory
}
