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
import org.slf4j.LoggerFactory

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

class BrowserSubmenuFragment
    : BaseDaggerBottomSheetDialogFragment<BrowserSubmenuFragmentBinding, BrowserSubmenuViewModel>() {
    override val layoutId = R.layout.browser_submenu_fragment

    init {
        mViewModelScope = SCOPE_ACTIVITY
    }

    override fun initViewBinding() { }

    override fun initViewModelEvents() {
        observe(mViewModel.dismiss) {
            if (mLog.isDebugEnabled) {
                mLog.debug("SUBMENU DISMISS")
            }

            dismiss()
        }
    }

    // 옵저빙을 BrowserFragment 하기 위해 아래의 메소드를 설정하지 않는다.
    override fun commandEventAware() { }

//    ////////////////////////////////////////////////////////////////////////////////////
//    //
//    // ICommandEventAware
//    //
//    ////////////////////////////////////////////////////////////////////////////////////
//
//    override fun onCommandEvent(cmd: String, data: Any) {
//        when (cmd) {
//            BrowserSubmenuViewModel.CMD_SUBMENU -> mCallback.invoke(data.toString())
//        }
//
//        dismiss()
//    }

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

    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserSubmenuFragment::class.java)
    }
}
