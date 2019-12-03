package com.example.clone_daum.ui.browser.favorite

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.FavoriteProcessFragmentBinding
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.ui.Navigator
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import brigitte.hideKeyboard
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */
class FavoriteProcessFragment @Inject constructor()
    : BaseDaggerFragment<FavoriteProcessFragmentBinding, FavoriteProcessViewModel>() {
    override val layoutId = R.layout.favorite_process_fragment

    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteProcessFragment::class.java)

        const val K_TITLE  = "title"
        const val K_URL    = "url"
        const val K_MODIFY = "modify"
    }

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        arguments?.let { mViewModel.run {
            init(disposable())

            val fav = it.getSerializable(K_MODIFY)
            if (fav is MyFavorite) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("MODIFY FAVORITE")
                    mLog.debug(fav.toString())
                }

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

    fun changeFolderName(pos: Int, fav: MyFavorite) {
        if (mLog.isDebugEnabled) {
            mLog.debug("CHANGE FOLDER ${fav.name} ($pos)")
        }

        mViewModel.apply {
            folder.set(fav.name)
            folderId = fav._id
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) = FavoriteProcessViewModel.run {
        when (cmd) {
            CMD_FOLDER_DETAIL -> navigator.folderFragment(childFragmentManager, mViewModel.folderId)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [FavoriteProcessFragmentModule::class])
        abstract fun contributeFavoriteProcessFragmentInjector(): FavoriteProcessFragment
    }

    @dagger.Module
    abstract class FavoriteProcessFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: FavoriteProcessFragment): SavedStateRegistryOwner
    }
}