package com.example.clone_daum.ui.main.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.LoginFragmentBinding
import com.kakao.auth.ISessionCallback
import com.kakao.auth.KakaoSDK
import com.kakao.auth.Session
import com.kakao.auth.authorization.authcode.KakaoWebViewActivity
import com.kakao.network.ServerProtocol
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.Utility
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-20 <p/>
 *
 * 카카오 로그인
 * https://developers.kakao.com/docs/android/user-management
 *
 * merge 된 android manifest xml 을 보면서 깜놀 왜????
 * 이렇게 정리되지 않은 activity 목록들이 많은것인가?
 * kakao sdk 하나만 넣었는데 =_ =? 온갖 activity 가...
 */

class LoginFragment @Inject constructor(
): BaseDaggerFragment<LoginFragmentBinding, LoginViewModel>() {
    override val layoutId = R.layout.login_fragment

    init {
        mViewModelScope = SCOPE_ACTIVITY
    }

    override fun onDestroyView() {
        Session.getCurrentSession().removeCallback(mViewModel)
        super.onDestroyView()
    }

    override fun initViewBinding() {
        // 오타네?
        mBinding.comKakaoLogin.setSuportFragment(this)

        Session.getCurrentSession().addCallback(mViewModel)
    }

    override fun initViewModelEvents() {
        observe(mViewModel.status) {
            if (mLog.isDebugEnabled) {
                mLog.debug("LOGIN STATUS : $it")
            }

            if (it) {
                // 로그인 상태가 true 가 되면 Login Fragment 를 종료 시키고
                // 변경된 정보를 화면에 반영한다.
                finish()
            }
        }
    }

//    // h/w back pressed 가 동작하지 않도록 수정
//    override fun onBackPressed() =
//        true

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ISessionCallback
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("ACTIVITY RESULT, REQUEST CODE: $requestCode, RESULT CODE: $resultCode")
        }

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            if (mLog.isDebugEnabled) {
                mLog.debug("HANDLE ACTIVITY RESULT : $requestCode, $resultCode")
            }

            return
        }

        super.onActivityResult(requestCode, resultCode, data)
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

    companion object {
        private val mLog = LoggerFactory.getLogger(LoginFragment::class.java)
    }
}