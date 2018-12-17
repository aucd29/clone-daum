package com.example.clone_daum.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.clone_daum.di.module.Config
import com.example.common.di.module.inject
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
        private const val TIMEOUT_RELOAD_ICO = 6L

        var calledFinishSplash = false
    }

    private var mTimerDisposable: CompositeDisposable? = CompositeDisposable()

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var config: Config

    lateinit var splashVm: SplashViewModel

    override fun bindViewModel() {
        super.bindViewModel()

        splashVm = vmfactory.inject(this, SplashViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val url = arguments?.getString("url")
        val webview = mBinding.webview
        val swipeRefresh = mBinding.swipeRefresh

        webview.run {
            loadUrl(url)

            // http://sarangnamu.net/basic/basic_view.php?no=6321&page=1&sCategory=0
            swipeRefresh.setOnRefreshListener {
                if (mLog.isDebugEnabled) {
                    mLog.debug("RELOAD $url")
                }
                this.reload()

                mTimerDisposable?.add(Observable.timer(TIMEOUT_RELOAD_ICO, TimeUnit.SECONDS)
                    .take(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                        if (mLog.isInfoEnabled) {
                            mLog.info("EXPLODE RELOAD ICO TIMER")
                        }

                        swipeRefresh.isRefreshing = false
                    })
            }
        }
    }

    override fun settingEvents() = viewmodel.run {
        val swipeRefresh = mBinding.swipeRefresh

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
                        v?.loadUrl(url)
                    }
                }
            }, pageFinished = {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PAGE FINISHED")
                }

                if (swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = false   // hide refresh icon

                    if (mLog.isDebugEnabled) {
                        mLog.debug("DISPOSABLE COUNT : ${mTimerDisposable?.size()}")
                    }

                    mTimerDisposable?.clear()

                    if (mLog.isDebugEnabled) {
                        mLog.debug("HIDE REFRESH ICON")
                    }
                }
            }
            , userAgent = { config.USER_AGENT }
            , progress = {
                if (it == 100)  {
                    if (!calledFinishSplash) {
                        calledFinishSplash = true
                        splashVm.splashCloseEvent.call()
                    }
                }
            }
        ))
    }

    override fun onPause() {
        super.onPause()

        mBinding.webview.pauseTimers()
        mTimerDisposable?.clear()
    }

    override fun onResume() {
        mBinding.webview.resumeTimers()

        super.onResume()
    }

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MainWebviewFragment
    }
}