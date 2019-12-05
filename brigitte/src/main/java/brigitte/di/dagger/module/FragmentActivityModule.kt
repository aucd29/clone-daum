package brigitte.di.dagger.module

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistryOwner
import brigitte.di.dagger.scope.ActivityScope
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-27 <p/>
 */

//@Module
//class FragmentActivityModule {
////    @Provides
////    fun provideViewModelStore(activity: FragmentActivity): ViewModelStore =
////        activity.viewModelStore
//
////    @Provides
////    fun provideSavedStateRegistryOwner(activity: FragmentActivity): SavedStateRegistryOwner =
////        activity
//
//    @Provides
//    fun provideOnBackPressedCallback() =
//        DaggerOnBackPressedCallback()
//
//}

//class DaggerOnBackPressedCallback @Inject constructor(
//    enabled: Boolean = true
//) : OnBackPressedCallback(enabled) {
//    var callback: (() -> Unit)? = null
//
//    override fun handleOnBackPressed() {
//        callback?.invoke()
//    }
//}