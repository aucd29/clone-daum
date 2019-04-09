package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.databinding.FolderFragmentBinding
import com.example.clone_daum.model.local.MyFavorite
import com.example.common.BaseDaggerFragment
import com.example.common.finish
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 8. <p/>
 */

class FolderFragment
    : BaseDaggerFragment<FolderFragmentBinding, FolderViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(FolderFragment::class.java)

        const val K_CURRENT_FOLDER = "currentFolder"
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        val currentFolder = arguments?.getString(K_CURRENT_FOLDER, null)

        mViewModel.initFolder(mDisposable, currentFolder)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) = FolderViewModel.run {
        when (cmd) {
            CMD_SHOW_FOLDER_DIALOG -> FolderDialog.show(this@FolderFragment, mViewModel)
            CMD_CHANGE_FOLDER      -> changeFolderName()
        }
    }

    private fun changeFolderName() {
        val frgmt    = parentFragment
        val pair = mViewModel.currentFolder()

        when (frgmt) {
            is FavoriteProcessFragment -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FROM PROCESS")
                }

                frgmt.changeFolderName(pair.first, pair.second)
            }
            is FavoriteModifyFragment -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FROM MODIFY")
                }

                frgmt.changeFolderName(pair.first, pair.second)
            }
        }

        finish()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): FolderFragment
    }
}
