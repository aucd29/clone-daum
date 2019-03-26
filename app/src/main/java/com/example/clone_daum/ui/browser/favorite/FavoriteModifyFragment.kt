package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.R
import com.example.clone_daum.databinding.FavoriteModifyFragmentBinding
import com.example.common.BaseDaggerFragment
import com.example.common.fadeColorResource
import com.example.common.popupMenu

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
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            FavoriteModifyViewModel.CMD_POPUP_MENU -> {
                popupMenu(R.menu.favorite, mBinding.favoriteFolderMenu) {
                    true
                }
            }
        }
    }
}