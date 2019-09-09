package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.clone_daum.common.Config
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.ui.FragmentFactory
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import brigitte.viewmodel.SplashViewModel
import brigitte.widget.*
import dagger.android.ContributesAndroidInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

class MainWebviewFragment @Inject constructor(
): BaseDaggerWebViewFragment<MainWebviewFragmentBinding, MainViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainWebviewFragment::class.java)

        private const val TIMEOUT_RELOAD_ICO = 4000L
    }

    init {
        mViewModelScope = SCOPE_ACTIVITY        // MainViewModel 를 공유
    }

    @Inject lateinit var config: Config
    @Inject lateinit var preConfig: PreloadConfig

    override val layoutId = R.layout.main_webview_fragment
    override val webview: WebView
        get() = mBinding.webview

    private val mPosition: Int
        get() = arguments?.getInt(MainTabAdapter.K_POSITION) ?: 0

    private lateinit var mSplashViewModel: SplashViewModel

    override fun bindViewModel() {
        super.bindViewModel()

        mSplashViewModel = inject(requireActivity())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() = mBinding.run {
        webview.defaultSetting(WebViewSettingParams(
            urlLoading = { _, url ->
                url?.let {
                    if (!it.contains("m.daum.net")) {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("OPEN BROWSER FRAGMENT : $url")
                        }

                        navigate(R.id.actionGlobalBrowserFragment, Bundle().apply {
                            putString("url", url)
                        })
                    } else {
                        // uri 를 redirect 시키는 이유가 뭘까나?
                        if (mLog.isDebugEnabled) {
                            mLog.debug("LOAD URL : $url")
                        }

                        webview.loadUrl(url)
                    }
                }
            }, pageFinished = {
                swipeRefresh.apply {
                    if (isRefreshing) {
                        isRefreshing = false     // hide refresh icon

                        disposable().clear()
                    }
                }

                if (mPosition == MainViewModel.INDEX_NEWS) {
                    mSplashViewModel.closeSplash()
                }

                syncCookie()
            }
            , userAgent = { config.USER_AGENT }
        ))

        swipeRefresh.setOnRefreshListener {
            if (mLog.isDebugEnabled) {
                mLog.debug("SWIPE RELOAD URL : ${webview.url}")
            }

            webview.reload()

            disposable().add(singleTimer(TIMEOUT_RELOAD_ICO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    if (mLog.isInfoEnabled) {
                        mLog.info("EXPLODE RELOAD ICO TIMER")
                    }

                    swipeRefresh.isRefreshing = false
                })
        }
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            // appbar 이동 시 webview 도 동일하게 이동 시킴
            observe(appbarOffsetLive) {
                if (mLog.isTraceEnabled) {
                    mLog.trace("WEBVIEW TRANSLATION Y : $it")
                }

                webview.translationY = it.toFloat()

                // https://stackoverflow.com/questions/30779667/android-collapsingtoolbarlayout-and-swiperefreshlayout-get-stuck
                mBinding.swipeRefresh.isEnabled = it == 0
            }
        }
    }

    override fun onDestroyView() {
        mBinding.swipeRefresh.removeAllViews()

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector
        abstract fun contributeMainWebviewFragmentInjector(): MainWebviewFragment
    }
}