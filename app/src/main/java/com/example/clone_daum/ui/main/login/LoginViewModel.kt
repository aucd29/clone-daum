package com.example.clone_daum.ui.main.login

import android.app.Application
import androidx.databinding.ObservableField
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
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class LoginViewModel @Inject constructor(
    private val userManagement: UserManagement,
    private val authService: AuthService,
    app: Application
) : CommandEventViewModel(app), ISessionCallback {
    companion object {
        private val logger = LoggerFactory.getLogger(LoginViewModel::class.java)
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
        if (logger.isDebugEnabled) {
            logger.debug("REQUEST LOGOUT")
        }

        userManagement.requestLogout(object: LogoutResponseCallback() {
            override fun onCompleteLogout() {
                if (logger.isDebugEnabled) {
                    logger.debug("COMPLETE LOGOUT")
                }

                status.postValue(false)
            }
        })
    }

    private fun requestMe() {
        if (logger.isDebugEnabled) {
            logger.debug("REQUEST ME (GET USER INFO)")
        }

        userManagement.me(object: MeV2ResponseCallback() {
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
                        if (logger.isDebugEnabled) {
                            logger.debug("\n====\nLOGIN INFO\n====\n" +
                                "id        : $id\n" +
                                "thumbnail : ${thumbnail.get()}\n" +
                                "nickname  : ${nickname.get()}")
                        }
                    }
                }
            }

            override fun onSessionClosed(e: ErrorResult?) {
                logger.error("ERROR: SESSION CLOSED")

                status.postValue(false)
                e?.let {
                    logger.error("${e.errorCode} : ${e.errorMessage}")
                    snackbar(e.errorMessage)
                }

                // TODO 오류 시 별도로 처리 해야 하는 부분 존재
            }
        })
    }

    private fun accessTokenInfo() {
        if (logger.isDebugEnabled) {
            logger.debug("REQUEST ACCESS TOKEN")
        }

        authService.requestAccessTokenInfo(object: ApiResponseCallback<AccessTokenInfoResponse>() {
            override fun onSuccess(result: AccessTokenInfoResponse) {
                val token = AccessToken(result.userId, result.expiresInMillis)

                if (logger.isDebugEnabled) {
                    logger.debug("RECEIVED ACCESS TOKEN\n" +
                            "id  : ${token.userId}\n" +
                            "exp : ${token.expiresInMillis}")
                }

                if (token.isValidToken()) {
                    if (logger.isDebugEnabled) {
                        logger.debug("VALID ACCESS TOKEN")
                    }

                    requestMe()
                } else {
                    if (logger.isDebugEnabled) {
                        logger.debug("INVALID ACCESS TOKEN : ${token.expiresInMillis}")
                    }
                }
            }

            override fun onSessionClosed(e: ErrorResult?) {
                logger.error("ERROR: ${e?.errorCode} = ${e?.errorMessage}")
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ISessionCallback
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onSessionOpenFailed(e: KakaoException?) {
        logger.error("ERROR: SESSION OPEN FAILED ${e?.message}")
        e?.message?.let { snackbar(it) }

        status.postValue(false)
    }

    override fun onSessionOpened() {
        if (logger.isInfoEnabled) {
            logger.info("SESSION OPENED")
        }

        requestMe()
    }
}