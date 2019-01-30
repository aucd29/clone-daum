package com.example.clone_daum.ui.main

import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.clone_daum.di.module.Config
import com.example.clone_daum.di.module.PreloadConfig
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

// 기존 앱의 경우 좌우 swipe 만 진행하면 scroll y 값을 초기화 해버리던데.. -_ -? 이게 맞나?? 싶은데?
// 메모리 때문에 tab 은 초기화 하긴 해야 되고.. 그럼 y pos 값을 저장해두었다고 offset 해야 되나? 싶은 ?
class MainWebviewFragment: BaseDaggerFragment<MainWebviewFragmentBinding, MainWebViewViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainWebviewFragment::class.java)

        private const val TIMEOUT_RELOAD_ICO = 4L
    }

    @Inject lateinit var config: Config
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var viewController: ViewController

    private lateinit var mSplashViewModel: SplashViewModel
    private lateinit var mMainViewModel: MainViewModel

    private var mTimerDisposable: CompositeDisposable = CompositeDisposable()
    private var mIsClosedSplash = false

    override fun bindViewModel() {
        super.bindViewModel()

        mSplashViewModel = mViewModelFactory.injectOfActivity(this, SplashViewModel::class.java)
        mMainViewModel   = mViewModelFactory.injectOfActivity(this, MainViewModel::class.java)
    }

    override fun initViewBinding() = mBinding.run {
        if (mLog.isDebugEnabled) {
            mLog.debug("INIT WEBVIEW SETTING")
        }

        webview.defaultSetting(WebViewSettingParams(
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

                        webview.loadUrl(url)
                    }
                }
            }, pageFinished = {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PAGE FINISHED")
                }

                swipeRefresh.run {
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

        swipeRefresh.setOnRefreshListener {
            if (mLog.isDebugEnabled) {
                mLog.debug("SWIPE RELOAD URL : ${webview.url}")
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

            observe(currentTabPositionLive) {
                // current pos 에 web 만 load url 을 하도록 수정
                mBinding.run {
                    if (webview.url.isNullOrEmpty()) {
                        val pos = arguments!!.getInt(MainTabAdapter.K_POSITION)

                        if (pos == it.toInt()) {
                            preConfig.tabLabelList.get(pos).url.let {
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("TAB CHANGED ($pos) : $it")
                                }

                                webview.loadUrl(it)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        mViewModel.webviewEvent(WebViewEvent.PAUSE)
        mTimerDisposable.clear()
    }

    override fun onResume() {
        if (mLog.isDebugEnabled) {
            mLog.debug("RESUME WEBVIEW URL : ${mBinding.webview.url}")
        }

        mViewModel.webviewEvent(WebViewEvent.RESUME)

        super.onResume()
    }

    override fun onDestroyView() {
        mBinding.webview.free()

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