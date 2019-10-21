package com.example.clone_daum.ui.main.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.OnBackPressedListener
import brigitte.di.dagger.scope.FragmentScope
import brigitte.finish
import brigitte.toast
import com.example.clone_daum.R
import com.example.clone_daum.databinding.LoginFragmentBinding
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.util.exception.KakaoException
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-20 <p/>
 *
 * 카카오 로그인
 * https://developers.kakao.com/docs/android/user-management
 */

class LoginFragment @Inject constructor(
): BaseDaggerFragment<LoginFragmentBinding, LoginViewModel>(),
   ISessionCallback, OnBackPressedListener {
    override val layoutId = R.layout.login_fragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Session.getCurrentSession().addCallback(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        Session.getCurrentSession().removeCallback(this)
        super.onDestroyView()
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
    }

    // h/w back pressed 가 동작하지 않도록 수정
    override fun onBackPressed() =
        true

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ISessionCallback
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, requestCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSessionOpened() {
        // 어떠한 윈도우를 열어야 함
        finish()
    }

    override fun onSessionOpenFailed(exception: KakaoException?) {
        exception?.let {
            toast(exception.message?.run { this } ?: "Login Failed: Kakao")
        }
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