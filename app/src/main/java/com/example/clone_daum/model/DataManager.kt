package com.example.clone_daum.model

import android.content.Context
import io.reactivex.Observable

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

object DataManager {
    fun tabList(context: Context) =
        Observable.just(context.assets.open("res/tab.json").readBytes())
}