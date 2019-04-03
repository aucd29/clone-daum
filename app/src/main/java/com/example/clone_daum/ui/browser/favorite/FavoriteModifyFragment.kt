package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.R
import com.example.clone_daum.databinding.FavoriteModifyFragmentBinding
import dagger.android.ContributesAndroidInjector
import com.example.common.*
import org.slf4j.LoggerFactory
import com.example.clone_daum.model.local.MyFavorite


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 25. <p/>
 */

class FavoriteModifyFragment: BaseDaggerFragment<FavoriteModifyFragmentBinding, FavoriteModifyViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteModifyFragment::class.java)
    }

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
        val popup = popupMenu(R.menu.favorite, mBinding.favoriteFolderMenu) {
            when (it.itemId) {
                R.id.mnu_move_folder       -> moveFavoriteFolder()
                R.id.mnu_modify_favorite   -> modifyFavorite()
                R.id.mnu_add_home_launcher -> addIconToHomeLauncher()
            }

            true
        }

        var folderCount = 0
        var favCount    = 0
        mViewModel.selectedList.forEach {
            when (it.favType) {
                MyFavorite.T_FOLDER  -> ++folderCount
                MyFavorite.T_DEFAULT -> ++favCount
            }
        }

        if (mViewModel.selectedList.size == 0 || folderCount > 1 ||
            folderCount > 0 && favCount > 0) {
            popup.enableAll(false)
            return
        }

        val enableMoveFolder = favCount > 0 && folderCount == 0
        val enableFavorite = favCount < 2 || folderCount == 1
        popup.apply {
            enable(R.id.mnu_move_folder, enableMoveFolder)
            enable(R.id.mnu_modify_favorite, enableFavorite)
            enable(R.id.mnu_add_home_launcher, enableMoveFolder)
        }
    }

    private fun moveFavoriteFolder() {
        val fav = mViewModel.selectedList.get(0)
    }

    private fun modifyFavorite() {
        val fav = mViewModel.selectedList.get(0)
        when (fav.favType) {
            MyFavorite.T_FOLDER  -> modifyFavoriteFolderName(fav)
            MyFavorite.T_DEFAULT -> {}
        }
    }

    private fun modifyFavoriteFolderName(fav: MyFavorite) {
        FolderDialog.show(this, mViewModel, fav)
    }

    private fun addIconToHomeLauncher() {
        mViewModel.selectedList.forEach {
            if (mLog.isDebugEnabled) {
                mLog.debug("ADD ICON TO HOME LAUNCHER : $it.url")
            }

            shortcut(ShortcutParams(it.url, R.mipmap.ic_launcher, it.name))
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
        abstract fun contributeInjector(): FavoriteModifyFragment
    }
}
