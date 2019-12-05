package com.example.clone_daum.di.module.libs

import android.content.Context
import com.kakao.auth.*
import com.kakao.usermgmt.UserManagement
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-10-21 <p/>
 *
 * https://developers.kakao.com/docs/android/user-management
 */

@Module
class KakaoModule {
    @Singleton
    @Provides
    fun provideSdkAdapter(context: Context) =
        object: KakaoAdapter() {
            override fun getApplicationConfig() =
                IApplicationConfig { context }

            override fun getSessionConfig() = object: ISessionConfig {
                // Kakao SDK 에서 사용되는 WebView에서 email 입력폼에서 data를 save할지여부를 결정한다. Default true.
                override fun isSaveFormData() =
                    true

                // 로그인시 인증받을 타입을 지정한다. 지정하지 않을 시 가능한 모든 옵션이 지정된다
                override fun getAuthTypes() =
                    arrayOf(AuthType.KAKAO_LOGIN_ALL) // all type
//                arrayOf(AuthType.KAKAO_ACCOUNT) // webview only 일 경우 화면에 아무것도 나오지 않아 =_ =

                // 로그인시 access token과 refresh token을 저장할 때의 암호화 여부를 결정한다.
                override fun isSecureMode() =
                    false

                override fun getApprovalType() =
                    ApprovalType.INDIVIDUAL

                // SDK 로그인시 사용되는 WebView에서 pause와 resume시에 Timer를 설정하여 CPU소모를 절약한다.
                // true 를 리턴할경우 webview로그인을 사용하는 화면서 모든 webview에 onPause와 onResume
                // 시에 Timer를 설정해 주어야 한다. 지정하지 않을 시 false로 설정된다.
                override fun isUsingWebviewTimer() =
                    false
            }
        }

    @Singleton
    @Provides
    fun provideUserManagement() =
        UserManagement.getInstance()

    @Singleton
    @Provides
    fun provideAuthService() =
        AuthService.getInstance()
}