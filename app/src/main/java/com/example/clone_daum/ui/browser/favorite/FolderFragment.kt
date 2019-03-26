package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.databinding.FolderFragmentBinding
import com.example.common.BaseDaggerFragment
import com.example.common.finish
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 8. <p/>
 */

class FolderFragment
    : BaseDaggerFragment<FolderFragmentBinding, FavoriteViewModel>() {

    override fun initViewBinding() {

    }

    override fun initViewModelEvents() {
        mViewModel.initFolder(mDisposable)
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        FavoriteViewModel.apply {
            when (cmd) {
                CMD_SHOW_FOLDER_DIALOG -> FavoriteAddFolder.showDialog(context!!, mViewModel, true)
                CMD_CHECKED_FOLDER     -> {
                    val frgmt = parentFragment
                    if (frgmt is FavoriteAddFragment) {
                        val pair = mViewModel.currentFolder()
                        frgmt.changeFolderName(pair.first, pair.second)
                    }

                    finish()
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
        abstract fun contributeInjector(): FolderFragment
    }
}