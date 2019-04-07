package com.example.clone_daum.ui.browser.favorite

import com.example.clone_daum.databinding.FavoriteProcessFragmentBinding
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerFragment
import com.example.common.hideKeyboard
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */
class FavoriteProcessFragment
    : BaseDaggerFragment<FavoriteProcessFragmentBinding, FavoriteProcessViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteProcessFragment::class.java)

        const val K_TITLE  = "title"
        const val K_URL    = "url"
        const val K_MODIFY = "modify"
    }

    @Inject lateinit var viewController: ViewController

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        arguments?.let { mViewModel.run {
            init(mDisposable)

            val fav = it.getSerializable(K_MODIFY)
            if (fav is MyFavorite) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("MODIFY FAVORITE")
                    mLog.debug(fav.toString())
                }

                // 이상하긴 하지만 클래스를 새로 만들기 뭐해서 일단 이렇게 진행
                // 클래스 명을 전체적으로 변경해야 될 듯
                favorite(fav)
            } else {
                if (mLog.isDebugEnabled) {
                    mLog.debug("ADD FAVORITE")
                }

                // ui 에서 name 으로 되어 있어 title -> name 으로 변경
                name.set(it.getString("title"))
                url.set(it.getString("url"))
            }
        } }
    }

    override fun onDestroyView() {
        mBinding.root.hideKeyboard()

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FOLDER FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun changeFolderName(pos: Int, name: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("CHANGE FOLDER $name ($pos)")
        }

        mViewModel.folder.set(name)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) = FavoriteProcessViewModel.run {
        when (cmd) {
            CMD_FOLDER_DETAIL -> viewController.folderFragment(childFragmentManager, mViewModel.folder.get())
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
        abstract fun contributeInjector(): FavoriteProcessFragment
    }
}