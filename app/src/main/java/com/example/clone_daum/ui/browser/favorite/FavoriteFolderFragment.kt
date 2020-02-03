package com.example.clone_daum.ui.browser.favorite

import androidx.fragment.app.Fragment
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.FavoriteFolderFragmentBinding
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.browser.BrowserFragment
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteFolderFragment @Inject constructor()
    : BaseDaggerFragment<FavoriteFolderFragmentBinding, FavoriteFolderViewModel>() {
    override val layoutId = R.layout.favorite_folder_fragment

    companion object {
        private val logger = LoggerFactory.getLogger(FavoriteFolderFragment::class.java)

        const val K_FOLDER = "folder"
    }

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
        viewModel.initAdapter(
            R.layout.favorite_item_from_folder,
            R.layout.favorite_item_from_folder)
    }

    override fun initViewModelEvents() {
        arguments?.getInt(K_FOLDER)?.let {
            if (logger.isDebugEnabled) {
                logger.debug("FOLDER ID : $it")
            }

            viewModel.initByFolder(it)
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
        if (logger.isDebugEnabled) {
            logger.debug("SHOW BROWSER $url")
        }

        finish()
        finish()    // fragment 가 2개 쌓여 있어서 이를 2번 호출 해야 한다.

        find<BrowserFragment>()?.loadUrl(url)
    }

    private fun modifyFavorite() {
        arguments?.getInt(K_FOLDER)?.let {
            if (logger.isDebugEnabled) {
                logger.debug("FOLDER ID : $it")
            }

            navigator.favoriteModifyFragment(it)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [FavoriteFolderFragmentModule::class])
        abstract fun contributeFavoriteFolderFragmentInjector(): FavoriteFolderFragment
    }

    @dagger.Module
    abstract class FavoriteFolderFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: FavoriteFolderFragment): SavedStateRegistryOwner
    }
}
