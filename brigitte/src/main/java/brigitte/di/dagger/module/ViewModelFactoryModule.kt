@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.di.dagger.module

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.MapKey
import dagger.Module
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

/**
 * https://medium.com/@marco_cattaneo/android-mViewModel-and-factoryprovider-good-way-to-manage-it-with-dagger-2-d9e20a07084c
 */
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
// https://proandroiddev.com/saving-ui-state-with-viewmodel-savedstate-and-dagger-f77bcaeb8b08
//
////////////////////////////////////////////////////////////////////////////////////

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MapKey
annotation class AssistedViewModelKey(val value: KClass<out ViewModel>)

@AssistedModule
@Module(includes = [AssistedInject_ViewModelAssistedFactoriesModule::class])
abstract class ViewModelAssistedFactoriesModule

interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}

// https://satoshun.github.io/2019/05/viewmodel-savedstate-dagger/

class DaggerSavedStateViewModelFactory @Inject constructor(
    private val viewModelMap: MutableMap<Class<out ViewModel>, ViewModelAssistedFactory<out ViewModel>>,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return viewModelMap[modelClass]?.create(handle) as? T ?: throw IllegalStateException("Unknown ViewModel class")
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Singleton
class DaggerViewModelProviders @Inject constructor(
//    val savedStateFactory: DaggerSavedStateViewModelFactory,
    val factory: DaggerViewModelFactory
) {
    fun <T: ViewModel> get(owner: ViewModelStoreOwner, klass: KClass<T>): T {
        // TODO 추후 이걸로 바꾸자
        return get(owner, klass.java)
    }

    fun <T: ViewModel> get(owner: ViewModelStoreOwner, clazz: Class<T>): T {
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
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

//    @Binds
//    abstract fun bindSavedStateViewModelFactory(viewModelFactory: DaggerSavedStateViewModelFactory): AbstractSavedStateViewModelFactory

    @Binds
    abstract fun bindViewModelProvider(provider: DaggerViewModelProviders): Any
}
