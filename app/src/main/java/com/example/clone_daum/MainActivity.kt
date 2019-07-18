package com.example.clone_daum

import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.clone_daum.databinding.MainActivityBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.main.SplashViewModel
import brigitte.*
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainActivity : BaseDaggerActivity<MainActivityBinding, SplashViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainActivity::class.java)
    }

    @Inject lateinit var viewController: ViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        chromeInspector { if (mLog.isInfoEnabled) { mLog.info(it) }}
        exceptionCatcher { mLog.error("ERROR: $it") }
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        if (mLog.isDebugEnabled) {
            mLog.debug("START ACTIVITY")
        }

        initCookieManager()

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
}