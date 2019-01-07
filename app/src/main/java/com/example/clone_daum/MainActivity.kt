package com.example.clone_daum

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import com.example.clone_daum.databinding.MainActivityBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.main.SplashViewModel
import com.example.common.*
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainActivity : BaseDaggerRuleActivity<MainActivityBinding, SplashViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainActivity::class.java)
    }

    @Inject lateinit var viewController: ViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        if (mLog.isDebugEnabled) {
            mLog.debug("START ACTIVITY")
        }

        chromeInspector()

        if (savedInstanceState == null) {
            viewController.mainFragment()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        // https://github.com/InflationX/Calligraphy
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() = mViewModel.run {
        observe(closeEvent) {
            if (mLog.isInfoEnabled) {
                mLog.info("GONE SPLASH")
            }

            visibleSplash.set(View.GONE)
            // remove view
            mBinding.root.removeView(mBinding.splash)
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
}