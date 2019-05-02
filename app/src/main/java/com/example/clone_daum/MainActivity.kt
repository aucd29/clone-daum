package com.example.clone_daum

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import com.example.clone_daum.databinding.MainActivityBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.main.SplashViewModel
import brigitte.*
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import javax.inject.Inject

class MainActivity : BaseDaggerRuleActivity<MainActivityBinding, SplashViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainActivity::class.java)
    }

    @Inject lateinit var viewController: ViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        exceptionCatcher()
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        if (mLog.isDebugEnabled) {
            mLog.debug("START ACTIVITY")
        }

        initCookieManager()
        chromeInspector()

        if (savedInstanceState == null) {
            viewController.mainFragment()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        // https://github.com/InflationX/Calligraphy
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onResume() {
        super.onResume()

        startCookieSync()
    }

    override fun onPause() {
        stopCookieSync()

        super.onPause()
    }

    override fun initViewBinding() {

    }

    override fun initViewModelEvents() = mViewModel.run {
        observe(closeSplashEvent) {
            visibleSplash.set(View.GONE)
            mBinding.root.removeView(mBinding.splash)
        }
    }

    private fun chromeInspector() {
        if (BuildConfig.DEBUG) {
            // enabled chrome inspector
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (mLog.isInfoEnabled) {
                    mLog.info("ENABLED CHROME INSPECTOR")
                }

                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
    }

    private fun exceptionCatcher() {
        // setting exception
        val handler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            val os = ByteArrayOutputStream()
            val s  = PrintStream(os)
            e.printStackTrace(s)
            s.flush()

            mLog.error("ERROR: $os")

            if (handler != null) {
                handler.uncaughtException(t, e)
            } else {
                mLog.error("ERROR: EXCEPTION HANDLER == null")

                android.os.Process.killProcess(android.os.Process.myPid())
            }
        }
    }
}