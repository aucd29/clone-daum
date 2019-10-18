package com.example.clone_daum.ui.main.navigation.cafe

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.databinding.CafeFragmentBinding
import com.example.clone_daum.databinding.NavigationLoginViewBinding
import com.example.clone_daum.ui.main.navigation.NavigationLoginViewModel
import brigitte.BaseDaggerFragment
import brigitte.dataBinding
import brigitte.di.dagger.scope.FragmentScope
import brigitte.lpmm
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 *
 * 디자인 변경으로 삭제 [aucd29][2019-10-17]
 */

//class CafeFragment @Inject constructor(
//) : BaseDaggerFragment<CafeFragmentBinding, CafeViewModel>() {
//    override val layoutId = R.layout.cafe_fragment
//
//    private val mLoginViewModel: NavigationLoginViewModel by inject()
//    private lateinit var mLoginDataBinding: NavigationLoginViewBinding
//
//
//    override fun initViewBinding() = mBinding.run {
//        mLoginDataBinding = dataBinding(R.layout.navigation_login_view)
//        mLoginDataBinding.model = mLoginViewModel
//
//        cafeContainer.addView(mLoginDataBinding.naviLoginContainer)
//        mLoginDataBinding.naviLoginContainer.lpmm(cafeContainer)
//
//        loginViewModelEvents()
//    }
//
//    override fun initViewModelEvents() = mViewModel.run {
//
//    }
//
//    private fun loginViewModelEvents() = mLoginViewModel.run {
//        message.set(R.string.navi_require_login_check_cafe)
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////
//    //
//    // MODULE
//    //
//    ////////////////////////////////////////////////////////////////////////////////////
//
//    @dagger.Module
//    abstract class Module {
//        @FragmentScope
//        @ContributesAndroidInjector(modules = [CafeFragmentModule::class])
//        abstract fun contributeCafeFragmentInjector(): CafeFragment
//    }
//
//    @dagger.Module
//    abstract class CafeFragmentModule {
//        @Binds
//        abstract fun bindSavedStateRegistryOwner(activity: CafeFragment): SavedStateRegistryOwner
//    }
//}