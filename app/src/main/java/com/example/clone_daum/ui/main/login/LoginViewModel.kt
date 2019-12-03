package com.example.clone_daum.ui.main.login

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import brigitte.arch.SingleLiveEvent
import brigitte.viewmodel.CommandEventViewModel
import com.example.clone_daum.model.remote.kakao.AccessToken
import com.example.clone_daum.model.remote.kakao.UserInfo
import com.kakao.auth.ApiResponseCallback
import com.kakao.auth.AuthService
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.auth.network.response.AccessTokenInfoResponse
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class LoginViewModel @Inject constructor(
    private val mUserManagement: UserManagement,
    private val mAuthService: AuthService,
    app: Application
) : CommandEventViewModel(app), ISessionCallback {
    companion object {
        private val mLog = LoggerFactory.getLogger(LoginViewModel::class.java)
    }

    // public
    val status   = SingleLiveEvent<Boolean>()
    val userInfo = MutableLiveData<UserInfo>()

    init {
        status.value = false
    }


    fun checkIsLoginSession() {
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            accessTokenInfo()
        }
    }

    fun logout() {
        if (mLog.isDebugEnabled) {
            mLog.debug("REQUEST LOGOUT")
        }

        mUserManagement.requestLogout(object: LogoutResponseCallback() {
            override fun onCompleteLogout() {
                if (mLog.isDebugEnabled) {
                    mLog.debug("COMPLETE LOGOUT")
                }

                status.postValue(false)
            }
        })
    }

    private fun requestMe() {
        if (mLog.isDebugEnabled) {
            mLog.debug("REQUEST ME (GET USER INFO)")
        }

        mUserManagement.me(object: MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
                result?.let {
                    // 이 정도인데 맵퍼가 필요할까?
                    val info = UserInfo(
                        it.id,
                        ObservableField(it.kakaoAccount.profile.thumbnailImageUrl),
                        ObservableField(it.kakaoAccount.profile.nickname)
                    )

                    userInfo.postValue(info)
                    status.postValue(true)

                    info.run {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("\n====\nLOGIN INFO\n====\n" +
                                    "id        : $id\n" +
                                    "thumbnail : ${thumbnail.get()}\n" +
                                    "nickname  : ${nickname.get()}")
                        }
                    }
                }
            }

            override fun onSessionClosed(e: ErrorResult?) {
                mLog.error("ERROR: SESSION CLOSED")

                status.postValue(false)
                e?.let {
                    mLog.error("${e.errorCode} : ${e.errorMessage}")
                    snackbar(e.errorMessage)
                }

                // TODO 오류 시 별도로 처리 해야 하는 부분 존재
            }
        })
    }

    private fun accessTokenInfo() {
        if (mLog.isDebugEnabled) {
            mLog.debug("REQUEST ACCESS TOKEN")
        }

        mAuthService.requestAccessTokenInfo(object: ApiResponseCallback<AccessTokenInfoResponse>() {
            override fun onSuccess(result: AccessTokenInfoResponse) {
                val token = AccessToken(result.userId, result.expiresInMillis)

                if (mLog.isDebugEnabled) {
                    mLog.debug("RECEIVED ACCESS TOKEN\n" +
                            "id  : ${token.userId}\n" +
                            "exp : ${token.expiresInMillis}")
                }

                if (token.isValidToken()) {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("VALID ACCESS TOKEN")
                    }

                    requestMe()
                } else {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("INVALID ACCESS TOKEN : ${token.expiresInMillis}")
                    }
                }
            }

            override fun onSessionClosed(e: ErrorResult?) {
                mLog.error("ERROR: ${e?.errorCode} = ${e?.errorMessage}")
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ISessionCallback
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onSessionOpenFailed(e: KakaoException?) {
        mLog.error("ERROR: SESSION OPEN FAILED ${e?.message}")
        e?.message?.let { snackbar(it) }

        status.postValue(false)
    }

    override fun onSessionOpened() {
        if (mLog.isInfoEnabled) {
            mLog.info("SESSION OPENED")
        }

        requestMe()
    }
}