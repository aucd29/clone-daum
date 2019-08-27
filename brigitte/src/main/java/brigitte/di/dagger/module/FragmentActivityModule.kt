package brigitte.di.dagger.module

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistryOwner
import dagger.Module
import dagger.Provides

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-27 <p/>
 */

@Module
class FragmentActivityModule {
    @Provides
    fun provideViewModelStore(activity: FragmentActivity): ViewModelStore =
        activity.viewModelStore

    @Provides
    fun provideSavedStateRegistryOwner(activity: FragmentActivity): SavedStateRegistryOwner =
        activity
}