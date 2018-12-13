package com.example.clone_daum

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import com.example.clone_daum.databinding.MainActivityBinding
import com.example.clone_daum.ui.main.MainFragment
import com.example.common.*
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainActivity : BaseActivity<MainActivityBinding>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainActivity::class.java)
    }

    @Inject lateinit var disposable: CompositeDisposable

    override fun layoutId() = R.layout.main_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        if (mLog.isDebugEnabled) {
            mLog.debug("START ACTIVITY")
        }

        if (BuildConfig.DEBUG) {
            // enabled chrome inspector
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("ENABLED CHROME INSPECTOR")
                }

                WebView.setWebContentsDebuggingEnabled(true)
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.show(FragmentParams(R.id.container,
                MainFragment::class.java, commit = FragmentCommit.NOW, backStack = false))
        }
    }

    override fun onDestroy() {
        disposable.clear()

        super.onDestroy()
    }
}