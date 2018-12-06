package com.example.clone_daum

import androidx.multidex.MultiDexApplication
import com.example.clone_daum.model.Repository

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 26. <p/>
 */

class MainApp() : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        Repository.init(this)
    }
}