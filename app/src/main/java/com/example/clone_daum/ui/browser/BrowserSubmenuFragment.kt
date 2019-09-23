package com.example.clone_daum.ui.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BrowserSubmenuFragmentBinding
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 18. <p/>
 *
 * https://gist.github.com/orhanobut/8665372
 *
 * 만들려다가 왠지 있을거 같아서 검색보니 있더라.. (BottomSheetFragment) 구글아 고마워
 *
 * BottomSheetFragment
 *  - http://liveonthekeyboard.tistory.com/145
 *  - https://github.com/material-components/material-components-android/tree/master/lib/java/com/google/android/material
 */

@SuppressLint("ValidFragment")
class BrowserSubmenuFragment (
    private val mCallback: (String) -> Unit
) : BaseDaggerBottomSheetDialogFragment<BrowserSubmenuFragmentBinding, BrowserSubmenuViewModel>() {
    override val layoutId = R.layout.browser_submenu_fragment

    override fun initViewBinding() { }

    override fun initViewModelEvents() { }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            BrowserSubmenuViewModel.CMD_SUBMENU -> mCallback.invoke(data.toString())
        }

        dismiss()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [BrowserSubmenuFragmentModule::class])
        abstract fun contributeBrowserSubmenuFragmentInjector(): BrowserSubmenuFragment
    }
    
    @dagger.Module
    abstract class BrowserSubmenuFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: BrowserSubmenuFragment): SavedStateRegistryOwner
    }
}
