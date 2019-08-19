package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import com.example.clone_daum.databinding.MainWebviewFragmentBinding
import com.example.clone_daum.common.Config
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.ui.ViewController
import brigitte.*
import dagger.android.ContributesAndroidInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

// 기존 앱의 경우 좌우 swipe 만 진행하면 scroll y 값을 초기화 해버리던데.. -_ -? 이게 맞나?? 싶은데?
// 메모리 때문에 tab 은 초기화 하긴 해야 되고.. 그럼 y pos 값을 저장해두었다고 offset 해야 되나? 싶은 ?
class MainWebviewFragment @Inject constructor(): BaseDaggerFragment<MainWebviewFragmentBinding, MainViewModel>() {
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

    private var mTimerDisposable = CompositeDisposable()

    override fun layoutId() = R.layout.main_webview_fragment

    override fun bindViewModel() {
//        super.bindViewModel()
        mSplashViewModel = inject(requireActivity())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() = mBinding.run {
//        if (mLog.isDebugEnabled) {
//            mLog.debug("INIT WEBVIEW SETTING")
//        }
//
//        webview.settings.apply {
//            textZoom = 100
//            javaScriptEnabled                = true
//            domStorageEnabled                = true
//            allowFileAccessFromFileURLs      = true
//            allowUniversalAccessFromFileURLs = true
//
//            setAppCacheEnabled(true)
//            setNeedInitialFocus(false)
//
//            userAgentString = config.USER_AGENT
//        }
//
//        webview.webViewClient = object : WebViewClient() {
//            private val mLog = LoggerFactory.getLogger(WebView::class.java)
//            var redirectFlag = false
//
//            override fun shouldOverrideUrlLoading(view: WebView?, loadingUrl: String?): Boolean {
//                if (mLog.isDebugEnabled) {
//                    mLog.debug("URL LOADING #1($this) : $loadingUrl")
//                }
//
//                internalShouldOverrideUrlLoading(view, loadingUrl)
//                redirectFlag = true
//
//                return true
//            }
//
//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//                if (mLog.isDebugEnabled) {
//                    mLog.debug("URL LOADING #2($this, $view) : ${request?.url.toString()}")
//                }
//
//                request?.let { r -> internalShouldOverrideUrlLoading(view, r.url.toString()) }
//                redirectFlag = true
//
//                return super.shouldOverrideUrlLoading(view, request)
//            }
//
//            private fun internalShouldOverrideUrlLoading(view: WebView?, loadingUrl: String?) {
//                view?.loadUrl(loadingUrl)
//            }
//
//            // https://stackoverflow.com/questions/3149216/how-to-listen-for-a-webview-finishing-loading-a-url
//            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                super.onPageStarted(view, url, favicon)
//
//                if (!redirectFlag) {
//                    if (mLog.isInfoEnabled) {
//                        mLog.info("PAGE STARTED : $url")
//                    }
//
////                    view?.let { canGoForward?.invoke(it.canGoForward()) }
////                    pageStarted?.invoke(url)
//                }
//
//                redirectFlag = false
//            }
//
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//
//                // 어래 수정한다고 했는데 callback 이 2번 나가서 다시 수정 =_ = [aucd29][2019. 4. 17.]
//                if (!redirectFlag) {
//                    if (mLog.isInfoEnabled) {
//                        mLog.info("PAGE FINISHED")
//                    }
//
//                    val position = arguments?.getInt(MainTabAdapter.K_POSITION)
//
//                    swipeRefresh.apply {
//                        if (isRefreshing) {
//                            isRefreshing = false     // hide refresh icon
//
//                            mTimerDisposable.let {
//                                if (mLog.isDebugEnabled) {
//                                    mLog.debug("DISPOSABLE COUNT : ${it.size()}")
//                                }
//
//                                it.clear()
//                            }
//
//                            if (mLog.isDebugEnabled) {
//                                mLog.debug("HIDE REFRESH ICON")
//                            }
//                        }
//                    }
//
//                    if (position == MainViewModel.INDEX_NEWS) {
//                        mSplashViewModel.closeSplash()
//                    }
//
//                    syncCookie()
//                }
//            }
//
////            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
////                super.onReceivedError(view, errorCode, description, failingUrl)
////
////                receivedError?.invoke(failingUrl)
////
////                view?.let { canGoForward?.invoke(it.canGoForward()) }
////            }
////
////            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
////                //super.onReceivedSslError(view, handler, error)
////                // http://theeye.pe.kr/archives/2721
////                sslError?.invoke(handler)
////            }
//        }

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
                val position = arguments?.getInt(MainTabAdapter.K_POSITION)

                swipeRefresh.apply {
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

                if (position == MainViewModel.INDEX_NEWS) {
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

            mTimerDisposable.add(singleTimer(TIMEOUT_RELOAD_ICO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    if (mLog.isInfoEnabled) {
                        mLog.info("EXPLODE RELOAD ICO TIMER")
                    }

                    swipeRefresh.isRefreshing = false
                })
        }

//        arguments?.getInt(MainTabAdapter.K_POSITION)?.let {
//            mUrl = preConfig.tabLabelList.get(it).url
//
//            if (mLog.isDebugEnabled) {
//                mLog.debug("URL : $mUrl")
//            }
//        }
//
//        Unit
    }

    private fun initWebViewSetting() {

    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            // appbar 이동 시 webview 도 동일하게 이동 시킴
            observe(appbarOffsetLive) {
                if (mLog.isTraceEnabled) {
                    mLog.trace("WEBVIEW TRANSLATION Y : $it")
                }

                mBinding.webview.translationY = it.toFloat()

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

            observe(tabChangedLive) { tab ->
                val tabPosition = tab?.position ?: -1
                val pos = arguments!!.getInt(MainTabAdapter.K_POSITION)

                if (pos == tabPosition) {
                    preConfig.tabLabelList[pos].url.let {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("LOAD URL ($pos) $it")
                        }

                        mBinding.webview.loadUrl(it)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        mBinding.webview.pause()
        mTimerDisposable.clear()
    }

    override fun onResume() {
        mBinding.webview.resume()

        super.onResume()
    }

    override fun onDestroyView() {
        mTimerDisposable.dispose()
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