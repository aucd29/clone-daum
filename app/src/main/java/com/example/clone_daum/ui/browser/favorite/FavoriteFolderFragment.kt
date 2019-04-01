package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.databinding.FavoriteFolderFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.common.*
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteFolderFragment
    : BaseDaggerFragment<FavoriteFolderFragmentBinding, FavoriteFolderViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteFolderFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        arguments?.getString("folder")?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("FOLDER NAME : $it")
            }

            mViewModel.initByFolder(it, mDisposable)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        FavoriteFolderViewModel.apply {
            when (cmd) {
                CMD_BRS_OPEN        -> showBrowser(data.toString())
                CMD_FAVORITE_MODIFY -> modifyFavorite()
            }
        }
    }

    private fun showBrowser(url: String) {
        finish()
        finish()    // fragment 가 2개 쌓여 있어서 이를 2번 호출 해야 한다.

        val frgmt = fragmentManager?.find(BrowserFragment::class.java)
        if (frgmt is BrowserFragment) {
            frgmt.loadUrl(url)
        }
    }

    private fun modifyFavorite() {
        arguments?.getString("folder")?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("FOLDER NAME : $it")
            }

            viewController.favoriteModifyFragment()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): FavoriteFolderFragment
    }
}