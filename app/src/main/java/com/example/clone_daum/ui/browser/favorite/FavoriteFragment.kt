package com.example.clone_daum.ui.browser.favorite

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.databinding.FavoriteFragmentBinding
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.browser.BrowserFragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import brigitte.find
import brigitte.finish
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 *
 * 찜이랑 성격이 같은거 같은데, 폴더 구분이 다르긴 하지만 중복되는 내용은 없어도 될듯한
 */

class FavoriteFragment @Inject constructor(
): BaseDaggerFragment<FavoriteFragmentBinding, FavoriteViewModel>() {
    override val layoutId  = R.layout.favorite_fragment

    companion object {
        private val logger = LoggerFactory.getLogger(FavoriteFragment::class.java)
    }

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
        binding.favoriteRadio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.favorite_show_all    -> viewModel.initItems()
                R.id.favorite_show_folder -> viewModel.initItemsByFolder()
            }
        }
    }

    override fun initViewModelEvents() {
        viewModel.apply {
            initAdapter(R.layout.favorite_item_folder, R.layout.favorite_item)
            init()
            initItems()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        if (logger.isDebugEnabled) {
            logger.debug("COMMAND : $cmd")
        }

        FavoriteViewModel.apply {
            when (cmd) {
                CMD_BRS_OPEN           -> showBrowser(data.toString())
                CMD_FOLDER_CHOOSE      -> navigator.favoriteFolderFragment(data as Int)
                CMD_SHOW_FOLDER_DIALOG -> FolderDialog.show(this@FavoriteFragment, viewModel)
                CMD_FAVORITE_MODIFY    -> navigator.favoriteModifyFragment()
            }
        }
    }

    private fun showBrowser(url: String) {
        finish()

        find<BrowserFragment>()?.loadUrl(url)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [FavoriteFragmentModule::class])
        abstract fun contributeFavoriteFragmentInjector(): FavoriteFragment
    }

    @dagger.Module
    abstract class FavoriteFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: FavoriteFragment): SavedStateRegistryOwner
    }
}