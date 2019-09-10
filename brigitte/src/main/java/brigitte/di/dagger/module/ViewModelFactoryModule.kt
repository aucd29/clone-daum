@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.di.dagger.module

import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import dagger.Binds
import dagger.MapKey
import dagger.Module
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass


////////////////////////////////////////////////////////////////////////////////////
//
// https://medium.com/@marco_cattaneo/android-mViewModel-and-factoryprovider-good-way-to-manage-it-with-dagger-2-d9e20a07084c
//
////////////////////////////////////////////////////////////////////////////////////

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Singleton
class DaggerViewModelFactory @Inject constructor(
    private val creator: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator[modelClass]?.get() as? T
            ?: throw IllegalArgumentException("unknown model class $modelClass") as Throwable
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Singleton
class DaggerViewModelProviders @Inject constructor(
    private val mFactory: DaggerViewModelFactory
) {
    fun <T: ViewModel> get(owner: ViewModelStoreOwner, klass: KClass<T>): T {
        // TODO 추후 이걸로 바꾸자
        return get(owner, klass.java)
    }

    fun <T: ViewModel> get(owner: ViewModelStoreOwner, factory: ViewModelProvider.Factory, klass: KClass<T>): T {
        // TODO 추후 이걸로 바꾸자
        return get(owner, factory, klass.java)
    }

    fun <T: ViewModel> get(owner: ViewModelStoreOwner, clazz: Class<T>): T {
        return ViewModelProvider(owner, mFactory).get(clazz)
    }

    fun <T: ViewModel> get(owner: ViewModelStoreOwner, factory: ViewModelProvider.Factory, clazz: Class<T>): T {
        return ViewModelProvider(owner, factory).get(clazz)
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Module
abstract class ViewModelFactoryModule {
//    @Binds
//    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

//    @Binds
//    abstract fun bindViewModelProvider(provider: DaggerViewModelProviders): Any
}
