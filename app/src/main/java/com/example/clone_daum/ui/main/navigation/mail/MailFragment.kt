package com.example.clone_daum.ui.main.navigation.mail

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MailFragmentBinding
import com.example.clone_daum.databinding.NavigationLoginViewBinding
import com.example.clone_daum.ui.main.navigation.NavigationLoginViewModel
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 */

class MailFragment @Inject constructor(
) : BaseDaggerFragment<MailFragmentBinding, MailViewModel>() {
    override val layoutId = R.layout.mail_fragment

    private val mLoginViewModel: NavigationLoginViewModel by inject()
    private lateinit var mLoginDataBinding: NavigationLoginViewBinding

    override fun initViewBinding() = mBinding.run {
        // LOGOUT STATUS
        mLoginDataBinding = dataBinding(R.layout.navigation_login_view)
        mLoginDataBinding.model = mLoginViewModel

        mailContainer.addView(mLoginDataBinding.naviLoginContainer)
        mLoginDataBinding.naviLoginContainer.lpmm(mailContainer)

        loginViewModelEvents()
    }

    override fun initViewModelEvents() = mViewModel.run {

    }

    private fun loginViewModelEvents() = mLoginViewModel.run {
        message.set(R.string.navi_require_login_check_mail)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [MailFragmentModule::class])
        abstract fun contributeMailFragmentInjector(): MailFragment
    }

    @dagger.Module
    abstract class MailFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: MailFragment): SavedStateRegistryOwner
    }
}