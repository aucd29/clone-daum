package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.R
import com.example.clone_daum.databinding.FavoriteFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.browser.BrowserFragment
import com.example.common.BaseDaggerFragment
import com.example.common.find
import com.example.common.finish
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 *
 * 찜이랑 성격이 같은거 같은데, 폴더 구분이 다르긴 하지만 중복되는 내용은 없어도 될듯한
 */

class FavoriteFragment
    : BaseDaggerFragment<FavoriteFragmentBinding, FavoriteViewModel>() {

    @Inject lateinit var viewController: ViewController

    override fun initViewBinding() {
        mBinding.favoriteRadio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.favorite_show_all    -> mViewModel.initShowAll(mDisposable)
                R.id.favorite_show_folder -> mViewModel.initShowFolder(mDisposable)
            }
        }
    }

    override fun initViewModelEvents() {
        mViewModel.initShowAll(mDisposable)
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        FavoriteViewModel.apply {
            when (cmd) {
                CMD_BRS_OPEN -> {
                    finish()

                    val frgmt = fragmentManager?.find(BrowserFragment::class.java)
                    if (frgmt is BrowserFragment) {
                        frgmt.loadUrl(data.toString())
                    }
                }
                CMD_CHOOSE_FOLDER      -> viewController.favoriteFolderFragment(data.toString())
                CMD_SHOW_FOLDER_DIALOG -> FavoriteAddFolder.showDialog(context!!, mViewModel, false)
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
        abstract fun contributeInjector(): FavoriteFragment
    }
}