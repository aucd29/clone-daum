package com.example.clone_daum.ui.main

import android.os.Bundle
import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.clone_daum.di.module.Config
import com.example.common.di.module.injectOfActivity
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import dagger.android.ContributesAndroidInjector
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

class MainWebviewFragment: BaseDaggerFragment<MainWebviewFragmentBinding, MainViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainWebviewFragment::class.java)

        private const val TIMEOUT_RELOAD_ICO = 4L
        private var IS_CLOSED_SPLASH = false
    }

    private var mTimerDisposable: CompositeDisposable? = CompositeDisposable()
    private lateinit var mSplashViewModel: SplashViewModel
    var webviewUrl: String? = null

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var config: Config

    override fun bindViewModel() {
        super.bindViewModel()

        mSplashViewModel = mViewModelFactory.injectOfActivity(this, SplashViewModel::class.java)
    }

    override fun initViewBinding() = mBinding.run {
        webviewUrl = arguments?.getString("url")

        if (webviewUrl == null) {
            mLog.error("ERROR: URL == null")

            return
        }

        webview.loadUrl(webviewUrl)

        swipeRefresh.setOnRefreshListener {
            if (mLog.isDebugEnabled) {
                mLog.debug("RELOAD $webviewUrl")
            }

            //brsEvent.set(WebViewEvent.RELOAD)
            // 해당 brs 에만 작용하기 위해서 수정
            webview.reload()

            mTimerDisposable?.add(Observable.timer(TIMEOUT_RELOAD_ICO, TimeUnit.SECONDS)
                .take(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    if (mLog.isInfoEnabled) {
                        mLog.info("EXPLODE RELOAD ICO TIMER")
                    }

                    swipeRefresh.isRefreshing = false
                })
        }

    }

    override fun initViewModelEvents() = mViewModel.run {
        // appbar 이동 시 webview 도 동일하게 이동 시킴
        observe(appbarOffsetLiveEvent) {
            if (mLog.isTraceEnabled()) {
                mLog.trace("WEBVIEW TRANSLATION Y : $it")
            }

            mBinding.webview.translationY = it.toFloat()
        }

        brsSetting.set(WebViewSettingParams(
            urlLoading = { v, url ->
                if (mLog.isInfoEnabled) {
                    mLog.info("url : $url")
                }

                url?.let {
                    if (!it.contains("m.daum.net")) {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("OPEN BROWSER FRAGMENT : $url")
                        }

                        val bundle = Bundle()
                        bundle.putString("url", it)

                        viewController.browserFragment(bundle)
                    } else {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("JUST LOAD URL : $url")
                        }

                        mBinding.webview.loadUrl(url)
                    }
                }
            }, pageFinished = {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PAGE FINISHED")
                }

                mBinding.swipeRefresh.run {
                    if (isRefreshing) {
                        isRefreshing = false     // hide refresh icon

                        mTimerDisposable?.let {
                            if (mLog.isDebugEnabled) {
                                mLog.debug("DISPOSABLE COUNT : ${it.size()}")
                            }

                            it.clear()
                        }

                        if (mLog.isDebugEnabled) {
                            mLog.debug("HIDE REFRESH ICON")
                        }
                    }
                }
            }
            , userAgent = { config.USER_AGENT }
            , progress  = {
                if (it == 100)  {
                    if (!IS_CLOSED_SPLASH) {
                        IS_CLOSED_SPLASH = true

                        mSplashViewModel.closeEvent.call()
                    }
                }
            }
        ))
    }

    override fun onPause() {
        super.onPause()

        mViewModel.brsEvent.set(WebViewEvent.PAUSE_TIMER)
        mTimerDisposable?.clear()
    }

    override fun onResume() {
        mViewModel.brsEvent.set(WebViewEvent.RESUME_TIMER)

        super.onResume()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MainWebviewFragment
    }
}