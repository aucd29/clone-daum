package com.example.clone_daum.model

import com.example.clone_daum.model.local.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

@Singleton
class DbRepository @Inject constructor(
    val frequentlySiteDao: FrequentlySiteDao
)