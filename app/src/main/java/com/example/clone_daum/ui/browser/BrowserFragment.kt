package com.example.clone_daum.ui.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintSet
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BrowserFragmentBinding
import com.example.clone_daum.common.Config
import com.example.clone_daum.ui.ViewController
import brigitte.*
import brigitte.bindingadapter.AnimParams
import com.google.android.material.snackbar.Snackbar
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

class BrowserFragment : BaseDaggerFragment<BrowserFragmentBinding, BrowserViewModel>(), OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(BrowserFragment::class.java)

        private const val WEBVIEW_SLIDING = 3

        const val K_URL = "url"
    }

    @Inject lateinit var viewController: ViewController
    @Inject lateinit var config: Config

    private val webview: WebView
        get() = mBinding.brsWebview

    private var mUrl: String? = null
    private var mScrollListener: () -> Unit = {
        if (webview.scrollY > config.SCREEN.y) {
            if (mBinding.brsMoveTop.alpha == 0f) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("SHOW BUTTON FOR MOVE TOP")
                }
                mViewModel.brsGoTop.set(AnimParams(1f, duration = 200))
            }
        } else {
            if (mBinding.brsMoveTop.alpha == 1f) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("HIDE BUTTON FOR MOVE TOP")
                }
                mViewModel.brsGoTop.set(AnimParams(0f, duration = 200))
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() = mBinding.run {
        mUrl = arguments?.getString(K_URL)

        if (mUrl == null) {
            mLog.error("ERROR: URL : $mUrl")
            return@run
        }

        startAnimation()

        mViewModel.apply {
            webview.defaultSetting(WebViewSettingParams(
                progress = {
                    if (mLog.isTraceEnabled) {
                        mLog.trace("BRS PROGRESS $it")
                    }

                    valProgress.set(it)
                }, receivedError = {
                    mLog.error("ERROR: $it")

                    it?.let { snackbar(it) }
                }, sslError = {
                    mLog.error("ERROR: SSL ")

                    dialog(DialogParam(context = requireContext(),
                        messageId  = R.string.brs_dlg_message_ssl_error,
                        titleId    = R.string.brs_dlg_title_ssl_error,
                        positiveId = R.string.dlg_btn_open,
                        negativeId = R.string.dlg_btn_close,
                        listener   = { res, _ ->
                            if (res) {
                                it?.proceed()
                                sslIconResId.set(R.drawable.ic_vpn_key_red_24dp)
                            } else it?.cancel()
                        }))
                }, pageStarted  = {
                    visibleProgress.set(View.VISIBLE)
                    reloadIconResId.set(R.drawable.ic_clear_black_24dp)
                }, pageFinished = {
                    visibleProgress.set(View.GONE)
                    reloadIconResId.set(R.drawable.ic_replay_black_24dp)

                    if (mLog.isDebugEnabled) {
                        mLog.debug("PAGE FINISHED ${System.currentTimeMillis()}")
                    }

                    syncCookie()
                    addHistory()
                }
                , canGoForward = { enableForward.set(it) }
                , userAgent    = { config.USER_AGENT }
            ))
        }

        loadUrl(mUrl!!)
        webview.viewTreeObserver.addOnScrollChangedListener(mScrollListener)
        webview.setFindListener { active, count, done ->
            mViewModel.innerSearchCount.set("${active + 1}/$count")
        }
    }

    override fun initViewModelEvents() = mViewModel.run {
        if (mUrl == null) {
            return@run
        }

        init(mDisposable)
        applyUrl(mUrl!!)

        // 임시 코드 추후 db 에서 얻어오도록 해야함
        applyBrsCount(mBinding.brsArea.childCount)

        sslIconResId.set(R.drawable.ic_vpn_key_black_24dp)

        innerSearch.observe {
            findAllAsync(it.get())
        }
    }

    private fun addHistory() {
        mViewModel.addHistory(webview.url, webview.title)
    }

    override fun onPause() {
        super.onPause()

        webview.pause()
    }

    override fun onResume() {
        webview.resume()

        activity?.let { fullscreen(it.isFullscreen()) }

        super.onResume()
    }

    override fun onDestroyView() {
        webview.apply {
            free()
            viewTreeObserver.removeOnScrollChangedListener(mScrollListener)
        }

        super.onDestroyView()
    }

    private fun startAnimation() = mViewModel.run {
        brsUrlBarAni.set(AnimParams(0f, config.ACTION_BAR_HEIGHT * -1))
        brsAreaAni.set(AnimParams(0f, config.ACTION_BAR_HEIGHT * WEBVIEW_SLIDING))
    }

    private fun endAnimation() = mViewModel.run {
        brsUrlBarAni.set(AnimParams(config.ACTION_BAR_HEIGHT * -1))
        brsAreaAni.set(AnimParams(config.ACTION_BAR_HEIGHT * WEBVIEW_SLIDING))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onBackPressed(): Boolean {
        if (mLog.isDebugEnabled) {
            mLog.debug("BACK PRESSED")
        }

        mBinding.apply {
            if (brsInnerSearch.viewStub?.visibility == View.VISIBLE) {
                brsInnerSearch.viewStub?.visibility = View.GONE

                return true
            }

            if (mViewModel.visibleInnerSearch.isVisible()) {
                mViewModel.visibleInnerSearch.gone()

                if (mLog.isDebugEnabled) {
                    mLog.debug("${mViewModel.visibleInnerSearch.get()}")
                }

                return true
            }

            if (webview.canGoBack()) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("BRS BACK")
                }

                webview.goBack()
            } else {
                endAnimation()
                finish()
            }
        }

        return true
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // PUBLIC METHODS
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun loadUrl(url: String) {
        webview.loadUrl(url)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        BrowserViewModel.apply {
            when (cmd) {
                CMD_HOME,
                CMD_BACK              -> onBackPressed()
                CMD_SEARCH_FRAGMENT   -> viewController.searchFragment()
                CMD_SUBMENU_FRAGMENT  -> subMenu()
                CMD_SHARE_EVENT       -> shareLink(data.toString())
                CMD_GOTO_TOP          -> webview.scrollTo(0, 0)
                CMD_NORMALSCREEN      -> fullscreen(false)
                CMD_RELOAD            -> webview.reload()
                CMD_INNER_SEARCH_PREV -> searchPrev()
                CMD_INNER_SEARCH_NEXT -> searchNext()
            }
        }
    }

    private fun searchPrev() {
        try {
            webview.findNext(false)
        } catch (e: Exception) {

        }
    }

    private fun searchNext() {
        try {
            webview.findNext(true)
        } catch (e: Exception) {

        }
    }

    private fun subMenu() {
        viewController.browserSubFragment { cmd ->
            when (cmd) {
                "즐겨찾기목록"   -> viewController.favoriteFragment()
                "즐겨찾기추가"   -> addFavorite()
                "방문기록"      -> urlHistory()
                "URL 복사"     -> copyUrl()
                "기타 브라우저"  -> showSystemBrowser()
                "화면 내 검색"  -> searchWithInScreen()
                "화면 캡쳐"     -> capture()
                "글자 크기"     -> resizeText()
                "홈 화면에 추가" -> addIconFromLauncher()
                "전체화면 보기"  -> fullscreen(true)
                "앱설정"       -> appSetting()
            }
        }
    }

    private fun shareLink(url: String) {
        val message = string(R.string.brs_intent_app_for_sharing)

        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, message)
            putExtra(Intent.EXTRA_TEXT, url)
        }, message))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // browserSubFragment
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun addFavorite() {
        // https://stackoverflow.com/questions/8193239/how-to-get-loaded-web-page-title-in-android-webview
        val title = webview.title
        val url   = webview.url

        viewController.favoriteProcessFragment(title, url)
    }

    private fun urlHistory() {
        viewController.urlHistoryFragment()
    }

    private fun copyUrl() {
        context?.toast(R.string.brs_copied_url)

        mUrl?.let {
            requireContext().clipboard(it)
        }
    }

    private fun showSystemBrowser() {
        confirm(R.string.brs_using_base_brs, R.string.common_notify, listener = { res, dlg ->
            if (res) {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = Uri.parse(mUrl)
                })
            }
        })
    }

    private fun searchWithInScreen() {
        // https://code.i-harness.com/en/q/b5bb99
        mViewModel.visibleInnerSearch.visible()
    }

    private fun findAllAsync(keyword: String?) {
        try {
            webview.findAllAsync(keyword)
        } catch (e: Exception) {
            webview.findAll(keyword)

            if (mLog.isDebugEnabled) {
                e.printStackTrace()
            }
        }
    }

    private fun capture() {
        webview.capture()
    }

    private fun resizeText() {

    }

    private fun addIconFromLauncher() {

    }

    private fun fullscreen(fullscreen: Boolean) {
        activity?.apply {
            fullscreen(fullscreen)
            mViewModel.isFullscreen.set(fullscreen)
        }

        // https://www.techotopia.com/index.php/Managing_Constraints_using_ConstraintSet
        mBinding.apply {
            ConstraintSet().apply {
                clone(brsContainer)
                connect(brsMoveTop.id, ConstraintSet.BOTTOM, if (fullscreen) {
                    brsNormalScreen.id
                } else {
                    brsBottomLayout.id
                }, ConstraintSet.TOP, 20.dpToPx(requireContext()))
                applyTo(brsContainer)
            }
        }
    }

    private fun appSetting() {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BrowserFragment
    }
}
