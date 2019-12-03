package com.example.clone_daum.model.remote.kakao

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-10-31 <p/>
 */

data class AccessToken(
    val userId: Long,
    val expiresInMillis: Long
) {
    fun isValidToken() =
        expiresInMillis > 0
}