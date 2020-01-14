package com.example.clone_daum

import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.example.clone_daum.databinding.MainActivityBinding
import brigitte.*
import brigitte.viewmodel.SplashViewModel
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.main.login.LoginViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainActivity : BaseDaggerActivity<MainActivityBinding, SplashViewModel>(
) {
    override val layoutId = R.layout.main_activity

    @Inject lateinit var navigator: Navigator

    private val loginViewModel: LoginViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        chromeInspector { if (logger.isInfoEnabled) { logger.info(it) }}
        exceptionCatcher { logger.error("ERROR: $it") }
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        if (logger.isDebugEnabled) {
            logger.debug("START ACTIVITY")
        }

        initCookieManager()

        if (savedInstanceState == null) {
            navigator.mainFragment()
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

    override fun initViewModelEvents() = viewModel.run {
        observe(closeEvent) {
            binding.root.removeView(binding.splash)
        }

        loginViewModel.checkIsLoginSession()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainActivity::class.java)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // TEST
    //
    ////////////////////////////////////////////////////////////////////////////////////

    // https://github.com/chiuki/espresso-samples/tree/master/idling-resource-okhttp
    @VisibleForTesting
    @Inject lateinit var okhttp: OkHttpClient
}