@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.di.dagger.module

import androidx.lifecycle.*
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

// https://satoshun.github.io/2019/05/viewmodel-savedstate-dagger/

//@Singleton
//class DaggerSavedStateViewModelFactory @Inject constructor(
//    private val creator: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<SavedStateViewModelFactory<out: ViewModel>>>,
//    private val owner: SavedStateRegistryOwner
//) : AbstractSavedStateViewModelFactory(owner, null) {
//    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
//
//        return creator[modelClass]?.let {
////            // it 은 lazy<ViewModel> 이고
////            val viewmodel = it.get().create(handle)
////
//////            // ISavedStateHandle 인터페이스를 상속하고 있으면 이를 설정
//////            // 사용자는 saveStateHandle 을
//////            if (viewmodel is ISavedStateHandle) {
//////                viewmodel.savedStateHandle = handle
//////            }
////
////            viewmodel as? T
//        } ?: throw IllegalArgumentException("unknown model class $modelClass")
//    }
//}

typealias MyViewModelFactory = DaggerViewModelFactory

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Singleton
class DaggerViewModelProviders @Inject constructor(
    val factory: MyViewModelFactory
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
// https://proandroiddev.com/saving-ui-state-with-viewmodel-savedstate-and-dagger-f77bcaeb8b08
//
////////////////////////////////////////////////////////////////////////////////////

interface SavedStateViewModelFactory<T: ViewModel> {
    fun create(handle: SavedStateHandle): T
}

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: MyViewModelFactory): ViewModelProvider.Factory

    @Binds
    abstract fun bindViewModelProvider(provider: DaggerViewModelProviders): Any
}
