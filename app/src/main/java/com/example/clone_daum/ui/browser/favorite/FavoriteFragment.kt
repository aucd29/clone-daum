package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.R
import com.example.clone_daum.databinding.FavoriteFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.clone_daum.ui.browser.BrowserFragment
import brigitte.BaseDaggerFragment
import brigitte.find
import brigitte.finish
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 *
 * 찜이랑 성격이 같은거 같은데, 폴더 구분이 다르긴 하지만 중복되는 내용은 없어도 될듯한
 */

class FavoriteFragment @Inject constructor()
    : BaseDaggerFragment<FavoriteFragmentBinding, FavoriteViewModel>() {

    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController

    override fun layoutId() = R.layout.favorite_fragment

    override fun initViewBinding() {
        mBinding.favoriteRadio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.favorite_show_all    -> mViewModel.initItems()
                R.id.favorite_show_folder -> mViewModel.initItemsByFolder()
            }
        }
    }

    override fun initViewModelEvents() {
        mViewModel.init(mDisposable)
        mViewModel.initItems()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        if (mLog.isDebugEnabled) {
            mLog.debug("COMMAND : $cmd")
        }

        FavoriteViewModel.apply {
            when (cmd) {
                CMD_BRS_OPEN           -> showBrowser(data.toString())
                CMD_FOLDER_CHOOSE      -> viewController.favoriteFolderFragment(data as Int)
                CMD_SHOW_FOLDER_DIALOG -> FolderDialog.show(this@FavoriteFragment, mViewModel)
                CMD_FAVORITE_MODIFY    -> viewController.favoriteModifyFragment()
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
        @ContributesAndroidInjector
        abstract fun contributeInjector(): FavoriteFragment
    }
}