package com.example.clone_daum

import android.os.Bundle
import com.example.clone_daum.databinding.MainActivityBinding
import com.example.clone_daum.ui.main.MainFragment
import com.example.common.*
import org.slf4j.LoggerFactory

class MainActivity : BaseActivity<MainActivityBinding>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainActivity::class.java)
    }
    override fun layoutId() = R.layout.main_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.run {
                add(FragmentParams(R.id.container, MainFragment::class.java,
                    commit = FragmentCommit.NOW))
            }
        }
    }
}
