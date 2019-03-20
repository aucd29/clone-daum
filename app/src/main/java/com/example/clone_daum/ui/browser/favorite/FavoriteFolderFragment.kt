package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.databinding.FavoriteFolderFragmentBinding
import com.example.clone_daum.databinding.FavoriteFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.common.BaseDaggerFragment
import com.example.common.find
import com.example.common.finish
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
        val folder = arguments?.getString("folder")

        if (mLog.isDebugEnabled) {
            mLog.debug("FOLDER NAME : $folder")
        }

        folder?.let {
            mViewModel.initByFolder(it, mDisposable)
        }
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        FavoriteFolderViewModel.apply {
            when (cmd) {
                CMD_BRS_OPEN -> {
                    finish()

                    val frgmt = fragmentManager?.find(BrowserFragment::class.java)
                    if (frgmt is BrowserFragment) {
                        frgmt.loadUrl(data.toString())
                    }
                }
            }
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