package com.example.clone_daum.ui.main.navigation.mail

import android.os.Bundle
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MailFragmentBinding
import com.example.clone_daum.databinding.NavigationLoginViewBinding
import com.example.clone_daum.ui.main.navigation.NavigationLoginViewModel
import com.example.common.BaseDaggerFragment
import com.example.common.dataBinding
import com.example.common.di.module.inject
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class MailFragment: BaseDaggerFragment<MailFragmentBinding, MailViewModel>() {

    private lateinit var mLoginViewModel: NavigationLoginViewModel
    private lateinit var mLoginDataBinding: NavigationLoginViewBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mLoginViewModel = mViewModelFactory.inject(this, NavigationLoginViewModel::class.java)

        viewBinding()
    }

    private fun viewBinding() = mBinding.run {
        mLoginDataBinding = dataBinding(R.layout.mail_fragment)

//        mailContainer
    }

    override fun settingEvents() {
    }



    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MailFragment
    }
}