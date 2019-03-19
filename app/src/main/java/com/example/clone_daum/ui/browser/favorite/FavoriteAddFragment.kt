package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.databinding.FavoriteAddFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerFragment
import com.example.common.showKeyboard
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */
class FavoriteAddFragment
    : BaseDaggerFragment<FavoriteAddFragmentBinding, FavoriteAddViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteAddFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController

    override fun initViewBinding() {
        mBinding.apply {
//            favoriteName.let { it.postDelayed({ requireActivity().showKeyboard(it) }, 200) }
        }
    }

    override fun initViewModelEvents() {
        arguments?.let { mViewModel.run {
                dp = mDisposable
            // ui 에서 name 으로 되어 있어 title -> name 으로 변경
            name.set(it.getString("title"))
            url.set(it.getString("url"))
        } }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) = FavoriteAddViewModel.run {
        when (cmd) {
            CMD_FOLDER_DETAIL -> viewController.folderFragment()
            CMD_FOLDER_ADD    -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("ADD FOLDER : $data")
                }
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
        abstract fun contributeInjector(): FavoriteAddFragment
    }
}