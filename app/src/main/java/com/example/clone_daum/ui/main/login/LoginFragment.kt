package com.example.clone_daum.ui.main.login

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.LoginFragmentBinding
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class LoginFragment @Inject constructor(
): BaseDaggerFragment<LoginFragmentBinding, LoginViewModel>() {
    override val layoutId = R.layout.login_fragment

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [LoginFragmentModule::class])
        abstract fun contributeLoginFragmentInjector(): LoginFragment
    }

    @dagger.Module
    abstract class LoginFragmentModule {
        @Binds
        abstract fun bindLoginFragment(fragment: LoginFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}