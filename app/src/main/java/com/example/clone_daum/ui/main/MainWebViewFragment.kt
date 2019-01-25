package com.example.clone_daum.ui.main

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
class MainWebviewFragment: BaseDaggerFragment<MainWebviewFragmentBinding, MainWebViewViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainWebviewFragment::class.java)

        private const val TIMEOUT_RELOAD_ICO = 4L
    }

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var config: Config

    private lateinit var mSplashViewModel: SplashViewModel
    private lateinit var mMainViewModel: MainViewModel

    private var mTimerDisposable: CompositeDisposable = CompositeDisposable()
    private var mIsClosedSplash = false

    var webviewUrl: String? = null

    init {
        mViewModelScope = SCOPE_FRAGMENT
    }

    override fun bindViewModel() {
        super.bindViewModel()

        mSplashViewModel = mViewModelFactory.injectOfActivity(this, SplashViewModel::class.java)
        mMainViewModel   = mViewModelFactory.injectOfActivity(this, MainViewModel::class.java)
    }

    override fun initViewBinding() = mBinding.run {
        webviewUrl = arguments?.getString(MainTabAdapter.K_URL)
        if (webviewUrl == null) {
            mLog.error("ERROR: URL == null")

            snackbar(mBinding.root, "Invalid URL")?.show()
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

            mTimerDisposable.add(Observable.timer(TIMEOUT_RELOAD_ICO, TimeUnit.SECONDS)
                .take(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    if (mLog.isInfoEnabled) {
                        mLog.info("EXPLODE RELOAD ICO TIMER")
                    }

                    swipeRefresh.isRefreshing = false
                })
        }
    }

    override fun initViewModelEvents() {
        mMainViewModel.run {
            // appbar 이동 시 webview 도 동일하게 이동 시킴
            observe(appbarOffsetLive) {
                if (mLog.isTraceEnabled()) {
                    mLog.trace("WEBVIEW TRANSLATION Y : $it")
                }

                mBinding.webview.translationY = it.toFloat()
            }

            // appbar 에 가려져 있는 progress 를 보이게 하기 위해 offset 값이 필요함
            observe(progressViewOffsetLive) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PROGRESS VIEW OFFSET $it")
                }

                mBinding.swipeRefresh.setProgressViewOffset(false,
                    it, (it * 1.3f).toInt())
            }
        }

        mViewModel.run {
            brsSetting.set(WebViewSettingParams(
                urlLoading = { _, url ->
                    if (mLog.isInfoEnabled) {
                        mLog.info("URL LOADING : $url")
                    }

                    url?.let {
                        if (!it.contains("m.daum.net")) {
                            if (mLog.isDebugEnabled) {
                                mLog.debug("OPEN BROWSER FRAGMENT : $url")
                            }

                            viewController.browserFragment(it)
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

                            mTimerDisposable.let {
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
                , progress = {
                    if (mLog.isTraceEnabled) {
                        mLog.trace("TAB WEBVIEW PROGRESS: $it")
                    }

                    // 이 값이 구 버전에서는 제대로 안들어왔던 기억이...
                    if (it == 100) {
                        if (!mIsClosedSplash) {
                            mIsClosedSplash = true

                            val position = arguments?.getInt(MainTabAdapter.K_POSITION)
                            if (position == 0) {
                                mSplashViewModel.closeEvent.call()
                            }
                        }
                    }
                }
            ))
        }
    }

    override fun onPause() {
        super.onPause()

        mViewModel.brsEvent.set(WebViewEvent.PAUSE_TIMER)
        mTimerDisposable.clear()
    }

    override fun onResume() {
        mViewModel.brsEvent.set(WebViewEvent.RESUME_TIMER)

        super.onResume()
    }

    override fun onDestroyView() {
        mBinding.webview.free()

        if (mLog.isDebugEnabled) {
            mLog.debug("DESTORY VIEW ${mBinding.webview}")
        }

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): MainWebviewFragment
    }
}