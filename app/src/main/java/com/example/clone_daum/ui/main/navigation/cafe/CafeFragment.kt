package com.example.clone_daum.ui.main.navigation.cafe

import android.os.Bundle
import com.example.clone_daum.R
import com.example.clone_daum.databinding.CafeFragmentBinding
import com.example.clone_daum.databinding.NavigationLoginViewBinding
import com.example.clone_daum.ui.main.navigation.NavigationLoginViewModel
import com.example.common.BaseDaggerFragment
import com.example.common.dataBinding
import com.example.common.di.module.injectOf
import com.example.common.lpmm
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class CafeFragment: BaseDaggerFragment<CafeFragmentBinding, CafeViewModel>() {
    private lateinit var mLoginViewModel: NavigationLoginViewModel
    private lateinit var mLoginDataBinding: NavigationLoginViewBinding

    override fun bindViewModel() {
        super.bindViewModel()

        mLoginViewModel = mViewModelFactory.injectOf(this)
    }

    override fun initViewBinding() = mBinding.run {
        mLoginDataBinding = dataBinding(R.layout.navigation_login_view)
        mLoginDataBinding.model = mLoginViewModel

        cafeContainer.addView(mLoginDataBinding.naviLoginContainer)
        mLoginDataBinding.naviLoginContainer.lpmm(cafeContainer)

        loginViewModelEvents()
    }

    override fun initViewModelEvents() = mViewModel.run {

    }

    private fun loginViewModelEvents() = mLoginViewModel.run {
        message.set(R.string.navi_require_login_check_cafe)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): CafeFragment
    }
}