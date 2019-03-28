package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.R
import com.example.clone_daum.databinding.FavoriteModifyFragmentBinding
import com.example.common.BaseDaggerFragment
import com.example.common.fadeColorResource
import com.example.common.popupMenu
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 25. <p/>
 */

class FavoriteModifyFragment: BaseDaggerFragment<FavoriteModifyFragmentBinding, FavoriteModifyViewModel>() {
    override fun initViewBinding() {
        mBinding.apply {
            favoriteModifyBar.fadeColorResource(android.R.color.white, R.color.colorAccent)
        }
    }

    override fun initViewModelEvents() {
        val folder = arguments?.getString("folder")
        mViewModel.init(folder, mDisposable)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            FavoriteModifyViewModel.CMD_POPUP_MENU -> showPopupMenu()
        }
    }

    private fun showPopupMenu() {
        popupMenu(R.menu.favorite, mBinding.favoriteFolderMenu) { true }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): FavoriteModifyFragment
    }
}
