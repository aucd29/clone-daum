package com.example.clone_daum.ui.browser.favorite

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.FolderFragmentBinding
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import brigitte.finish
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 8. <p/>
 */

// 화면내 폴더 목록을 보여주고 그중에 하나 선택하게 끔 함
class FolderFragment @Inject constructor(
) : BaseDaggerFragment<FolderFragmentBinding, FolderViewModel>() {
    override val layoutId  = R.layout.folder_fragment

    companion object {
        private val logger = LoggerFactory.getLogger(FolderFragment::class.java)

        const val K_CURRENT_FOLDER = "current-folder"
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        val currentFolderId = arguments?.getInt(K_CURRENT_FOLDER, 0) ?: 0

        viewModel.apply {
            initAdapter(R.layout.folder_item)
            initFolder(currentFolderId)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) = FolderViewModel.run {
        when (cmd) {
            CMD_SHOW_FOLDER_DIALOG -> FolderDialog.show(this@FolderFragment, viewModel)
            CMD_CHANGE_FOLDER      -> changeFolderName()
        }
    }

    private fun changeFolderName() {
        val frgmt    = parentFragment
        val pair = viewModel.currentFolder()

        when (frgmt) {
            is FavoriteProcessFragment -> {
                if (logger.isDebugEnabled) {
                    logger.debug("FROM PROCESS")
                }

                frgmt.changeFolderName(pair.first, pair.second)
            }
            is FavoriteModifyFragment -> {
                if (logger.isDebugEnabled) {
                    logger.debug("FROM MODIFY")
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
        @FragmentScope
        @ContributesAndroidInjector(modules = [FolderFragmentModule::class])
        abstract fun contributeFolderFragmentInjector(): FolderFragment
    }

    @dagger.Module
    abstract class FolderFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: FolderFragment): SavedStateRegistryOwner
    }
}
