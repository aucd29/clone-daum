package com.example.clone_daum.model.remote.kakao

import androidx.databinding.ObservableField

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-10-29 <p/>
 */

data class UserInfo(
    val id: Long,
    val thumbnail: ObservableField<String>,
    val nickname: ObservableField<String>
)