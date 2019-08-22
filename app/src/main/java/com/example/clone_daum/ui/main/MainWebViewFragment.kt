package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import android.webkit.WebView
import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.clone_daum.common.Config
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.ui.ViewController
import brigitte.*
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

// 기존 앱의 경우 좌우 swipe 만 진행하면 scroll y 값을 초기화 해버리던데.. -_ -? 이게 맞나?? 싶은데?
// 메모리 때문에 tab 은 초기화 하긴 해야 되고.. 그럼 y pos 값을 저장해두었다고 offset 해야 되나? 싶은 ?
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
    @Inject lateinit var viewController: ViewController

    private lateinit var mSplashViewModel: SplashViewModel

    override val webview: WebView
        get() = mBinding.webview

    private val mPosition: Int
        get() = arguments?.getInt(MainTabAdapter.K_POSITION) ?: 0

    override fun layoutId() =
        R.layout.main_webview_fragment

    override fun bindViewModel() {
        super.bindViewModel()

        mSplashViewModel = inject(requireActivity())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() = mBinding.run {
        super.initViewBinding()

        webview.defaultSetting(WebViewSettingParams(
            urlLoading = { _, url ->
                url?.let {
                    if (!it.contains("m.daum.net")) {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("OPEN BROWSER FRAGMENT : $url")
                        }

                        viewController.browserFragment(it)
                    } else {
                        // uri 를 redirect 시키는 이유가 뭘까나?
                        webview.loadUrl(url)
                    }
                }
            }, pageFinished = {
                swipeRefresh.apply {
                    if (isRefreshing) {
                        isRefreshing = false     // hide refresh icon

                        mDisposable.clear()
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

            //brsEvent.set(WebViewEvent.RELOAD)
            // 해당 brs 에만 작용하기 위해서 수정
            webview.reload()

            mDisposable.add(singleTimer(TIMEOUT_RELOAD_ICO)
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

            // appbar 에 가려져 있는 progress 를 보이게 하기 위해 offset 값이 필요함
            observe(progressViewOffsetLive) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("PROGRESS VIEW OFFSET $it")
                }

                mBinding.swipeRefresh.setProgressViewOffset(false,
                    it.toInt(), (it * 1.3f).toInt())
            }

//            observe(tabChangedLive) { tab ->
//                val tabPosition = tab?.position ?: -1
//
//                if (mPosition == tabPosition) {
//                    webview.load
//                    preConfig.tabLabelList[pos].url.let {
//                        if (mLog.isDebugEnabled) {
//                            mLog.debug("LOAD URL ($pos) $it")
//                        }
//
//                        mBinding.webview.loadUrl(it)
//                    }
//                }
//            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeMainWebviewFragmentInjector(): MainWebviewFragment
    }
}