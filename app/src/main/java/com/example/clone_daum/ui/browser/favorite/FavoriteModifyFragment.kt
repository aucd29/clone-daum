package com.example.clone_daum.ui.browser.favorite

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.databinding.FavoriteModifyFragmentBinding
import dagger.android.ContributesAndroidInjector
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import org.slf4j.LoggerFactory
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.ui.Navigator
import dagger.Binds
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 25. <p/>
 */

class FavoriteModifyFragment @Inject constructor(
) : BaseDaggerFragment<FavoriteModifyFragmentBinding, FavoriteModifyViewModel>() {
    override val layoutId = R.layout.favorite_modify_fragment
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteModifyFragment::class.java)

        const val K_FOLDER = "folder"
    }

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
        mBinding.apply {
            favoriteModifyBar.fadeColorResource(android.R.color.white,
                R.color.colorAccent)
        }
    }

    override fun initViewModelEvents() {
        arguments?.getInt(K_FOLDER)?.let {
            if (mLog.isDebugEnabled) {
                mLog.debug("FOLDER : $it")
            }

            mViewModel.init(it, disposable())
        }
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

        // 팝업 메뉴의 경우 folder 를 1개 이상 선택 시,
        // 폴더와 link 를 같이 선택 시
        // 아무것도 선택 안했을때 에 모두 비활성화 처리 한다.
        if (mViewModel.selectedList.size == 0 || folderCount > 1 ||
            folderCount > 0 && favCount > 0) {
            popup.enableAll(false)
            return
        }

        // 그리고 link 를 1개 이상 선택 하거나 폴더를 1개 이상 선택 했을때에는
        // favorite 수정 메뉴를 비활성화 처리 한다.
        val enableMoveFolder = favCount > 0 && folderCount == 0
        val enableFavorite = favCount < 2 || folderCount == 1
        popup.apply {
            enable(R.id.mnu_move_folder, enableMoveFolder)
            enable(R.id.mnu_modify_favorite, enableFavorite)
            enable(R.id.mnu_add_home_launcher, enableMoveFolder)
        }
    }

    private fun moveFavoriteFolder() {
        val fav = mViewModel.selectedList[0]
        navigator.folderFragment(childFragmentManager, fav.folderId, R.id.favorite_modify_container)
    }

    private fun modifyFavorite() {
        val fav = mViewModel.selectedList[0]
        when (fav.favType) {
            MyFavorite.T_FOLDER  -> modifyFavoriteFolderName(fav)
            MyFavorite.T_DEFAULT -> modifyFavoriteLink(fav)
        }
    }

    private fun modifyFavoriteFolderName(fav: MyFavorite) {
        FolderDialog.show(this, mViewModel, fav)
    }

    private fun modifyFavoriteLink(fav: MyFavorite) {
        finish()

        navigator.favoriteProcessFragment(fav)
    }

    private fun addIconToHomeLauncher() {
        mViewModel.selectedList.forEach {
            if (mLog.isDebugEnabled) {
                mLog.debug("ADD ICON TO HOME LAUNCHER : $it.url")
            }

            shortcut(ShortcutParams(it.url, R.mipmap.ic_launcher, it.name, it.name))
        }

        finish()
    }

    fun changeFolderName(pos: Int, fav: MyFavorite) {
        if (mLog.isDebugEnabled) {
            mLog.debug("CHANGE FOLDER ${fav.name} ($pos)")
        }

        val modifyFav = mViewModel.selectedList[0]
        modifyFav.folderId = fav._id
        // FIXME fav.folderId = if (name == string(R.string.folder_favorite)) "" else name

        mViewModel.updateFavorite(modifyFav) { finish() }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [FavoriteModifyFragmentModule::class])
        abstract fun contributeFavoriteModifyFragmentInjector(): FavoriteModifyFragment
    }

    @dagger.Module
    abstract class FavoriteModifyFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: FavoriteModifyFragment): SavedStateRegistryOwner
    }
}
