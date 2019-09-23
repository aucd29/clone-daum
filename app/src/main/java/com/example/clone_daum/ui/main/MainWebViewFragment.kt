package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.clone_daum.common.Config
import com.example.clone_daum.common.PreloadConfig
import brigitte.*
import brigitte.viewmodel.SplashViewModel
import brigitte.widget.*
import dagger.android.ContributesAndroidInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R
import com.example.clone_daum.ui.Navigator
import dagger.Binds

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

class MainWebviewFragment @Inject constructor(
): BaseDaggerWebViewFragment<MainWebviewFragmentBinding, MainViewModel>() {
    override val layoutId = R.layout.main_webview_fragment

    companion object {
        private val mLog = LoggerFactory.getLogger(MainWebviewFragment::class.java)

        private const val TIMEOUT_RELOAD_ICO = 4000L
    }

    init {
        mViewModelScope = SCOPE_ACTIVITY        // MainViewModel 를 공유
    }

    @Inject lateinit var config: Config
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var navigator: Navigator

    private val mSplashViewModel: SplashViewModel by activityInject()

    override val webview: WebView
        get() = mBinding.webview

    private val mPosition: Int
        get() = arguments?.getInt(MainTabAdapter.K_POSITION) ?: 0

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() = mBinding.run {
        webview.defaultSetting(WebViewSettingParams(
            urlLoading = { _, url ->
                url?.let {
                    if (!it.contains("m.daum.net")) {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("OPEN BROWSER FRAGMENT : $url")
                        }

                        navigator.browserFragment(url)
                    } else {
                        // uri 를 redirect 시키는 이유가 뭘까나?
                        if (mLog.isDebugEnabled) {
                            mLog.debug("LOAD URL : $url")
                        }

                        webview.loadUrl(url)
                    }
                }
            }, pageFinished = {
                mainWebViewSwipeRefresh.apply {
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

        mainWebViewSwipeRefresh.setOnRefreshListener {
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

                    mainWebViewSwipeRefresh.isRefreshing = false
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

                // https://stackoverflow.com/questions/30779667/android-collapsingtoolbarlayout-and-mainWebViewSwipeRefreshlayout-get-stuck
                mBinding.mainWebViewSwipeRefresh.isEnabled = it == 0
            }
        }
    }

    override fun onDestroyView() {
        if (mLog.isDebugEnabled) {
            mLog.debug("DESTROY VIEW = ")
        }
        mBinding.mainWebViewSwipeRefresh.isEnabled = false
        mBinding.mainWebViewSwipeRefresh.removeAllViews()

        super.onDestroyView()
    }

    fun disableSwipeRefresh() {
        mBinding.mainWebViewSwipeRefresh.isEnabled = false
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @brigitte.di.dagger.scope.FragmentScope
        @ContributesAndroidInjector(modules = [MainWebviewFragmentModule::class])
        abstract fun contributeMainWebviewFragmentInjector(): MainWebviewFragment
    }

    @dagger.Module
    abstract class MainWebviewFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: MainWebviewFragment): SavedStateRegistryOwner
    }
}