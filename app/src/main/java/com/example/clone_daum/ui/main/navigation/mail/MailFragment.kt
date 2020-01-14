package com.example.clone_daum.ui.main.navigation.mail

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MailFragmentBinding
import com.example.clone_daum.databinding.NavigationLoginViewBinding
import com.example.clone_daum.ui.main.navigation.NavigationLoginViewModel
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 28. <p/>
 *
 * 디자인 변경으로 삭제 [aucd29][2019-10-17]
 */

class MailFragment @Inject constructor(
) : BaseDaggerFragment<MailFragmentBinding, MailViewModel>() {
    override val layoutId = R.layout.mail_fragment

    private val loginViewModel: NavigationLoginViewModel by inject()
    private lateinit var loginDataBinding: NavigationLoginViewBinding

    override fun initViewBinding() = binding.run {
        // LOGOUT STATUS
        loginDataBinding       = dataBinding(R.layout.navigation_login_view)
        loginDataBinding.model = loginViewModel

        mailContainer.addView(loginDataBinding.naviLoginContainer)
        loginDataBinding.naviLoginContainer.lpmm(mailContainer)

        loginViewModelEvents()
    }

    override fun initViewModelEvents() = viewModel.run {

    }

    private fun loginViewModelEvents() = loginViewModel.run {
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