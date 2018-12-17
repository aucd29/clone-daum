package com.example.clone_daum

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import com.example.clone_daum.databinding.MainActivityBinding
import com.example.clone_daum.di.module.common.DaggerViewModelFactory
import com.example.clone_daum.di.module.common.inject
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.main.SplashViewModel
import com.example.common.*
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainActivity : BaseActivity<MainActivityBinding>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainActivity::class.java)
    }

    @Inject lateinit var disposable: CompositeDisposable
    @Inject lateinit var viewController: ViewController
    @Inject lateinit var vmFactory: DaggerViewModelFactory

    lateinit var splashVm: SplashViewModel

    override fun layoutId() = R.layout.main_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        if (mLog.isDebugEnabled) {
            mLog.debug("START ACTIVITY")
        }

        splashVm = vmFactory.inject(this, SplashViewModel::class.java)

        settingsEvents()
        chromeInspector()

        if (savedInstanceState == null) { viewController.mainFragment() }
    }

    private fun settingsEvents() = splashVm.run {
        mBinding.model = this

        observe(splashCloseEvent) {
            if (mLog.isInfoEnabled) {
                mLog.info("GONE SPLASH")
            }

            visibleSplash.set(View.GONE)
        }
    }

    private fun chromeInspector() {
        if (BuildConfig.DEBUG) {
            // enabled chrome inspector
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("ENABLED CHROME INSPECTOR")
                }

                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
    }

    override fun onDestroy() {
        disposable.clear()

        super.onDestroy()
    }
}