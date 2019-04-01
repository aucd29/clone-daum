package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.R
import com.example.clone_daum.databinding.FavoriteModifyFragmentBinding
import dagger.Module
import dagger.android.ContributesAndroidInjector
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.example.common.*
import org.slf4j.LoggerFactory
import android.content.pm.ShortcutManager
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon


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
                R.id.mnu_move_folder        -> {}
                R.id.mnu_modify_favorite    -> {}
                R.id.mnu_add_home_launcher  -> addIconToHomeLauncher()
            }

            true
        }

        popup.enableAll(mViewModel.enableDelete.get())
    }

    private fun addIconToHomeLauncher() {
        mViewModel.selectedList.forEach {
            addIcon(it.url, it.name)
        }

        finish()
    }

    private fun addIcon(url: String, title: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("ADD ICON TO HOME LAUNCHER : $url")
        }

        // FIXME 라이브러리쪽 으로 기능 이전해야 함 [aucd29][2019. 4. 1.]

        val shortcutIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            // https://stackoverflow.com/questions/46796674/create-shortcut-on-home-screen-in-android-o
            context?.let { ctx ->
                val manager = ctx.systemService(ShortcutManager::class.java)
                manager?.let {
                    if (it.isRequestPinShortcutSupported) {
                        val shortcut = ShortcutInfo.Builder(ctx, url)
                            .setShortLabel("URL")
                            .setLongLabel(title)
                            .setIcon(Icon.createWithResource(ctx, R.mipmap.ic_launcher))
                            .setIntent(shortcutIntent)
                            .build()

                        manager.requestPinShortcut(shortcut, null)
                    }
                }
            }

        } else {
            // shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            val intent = Intent()
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title)
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher))
            intent.putExtra("duplicate", false)

            // add the shortcut
            intent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
            requireContext().sendBroadcast(intent)
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
        abstract fun contributeInjector(): FavoriteModifyFragment
    }
}
