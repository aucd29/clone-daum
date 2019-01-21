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

class MainWebviewFragment: BaseDaggerFragment<MainWebviewFragmentBinding, MainViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainWebviewFragment::class.java)

        private const val TIMEOUT_RELOAD_ICO = 4L
    }

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var config: Config

    private var mTimerDisposable: CompositeDisposable? = CompositeDisposable()
    private lateinit var mSplashViewModel: SplashViewModel
    private var mIsClosedSplash = false

    var webviewUrl: String? = null

    override fun bindViewModel() {
        super.bindViewModel()


        mSplashViewModel = mViewModelFactory.injectOfActivity(this, SplashViewModel::class.java)
    }

    override fun initViewBinding() = mBinding.run {
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
                if (mLog.isDebugEnabled) {
                    mLog.debug("TAB WEBVIEW PROGRESS: $it")
                }

                if (it == 100)  {
                    // companison 이라서 초기화 안되는 버그 수정 [aucd29][2019. 1. 21.]
                    if (!mIsClosedSplash) {
                        mIsClosedSplash = true

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

        // 초기 로딩이 다소 느려서 이를 해소 하고자 selected 된 tab 만 webview 를
        // 로딩 하도록 수정 [aucd29][2019. 1. 21.]
        val position = arguments?.getInt(MainTabAdapter.K_POSITION)
        if (mViewModel.selectedTabPosition == position) {
            webviewUrl = arguments?.getString(MainTabAdapter.K_URL)

            if (webviewUrl == null) {
                mLog.error("ERROR: URL == null")

                return
            }

            mBinding.webview.loadUrl(webviewUrl)
        }

        super.onResume()
    }

    override fun onDestroyView() {
        mBinding.webview.free()

        if (mLog.isDebugEnabled) {
            mLog.debug("DESTORY VIEW ${mBinding.webview}")
        }

        super.onDestroyView()
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//
//        // https://code.i-harness.com/ko-kr/q/99b47e
//        // https://stackoverflow.com/questions/24161160/setuservisiblehint-called-before-oncreateview-in-fragment
//        if (isVisibleToUser && view != null) {
//
//        }
//    }

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