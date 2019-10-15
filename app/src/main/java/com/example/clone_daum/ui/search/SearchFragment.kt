package com.example.clone_daum.ui.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.databinding.SearchFragmentBinding
import com.example.clone_daum.ui.Navigator
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.di.module.AssistedViewModelKey
import com.example.clone_daum.di.module.DaggerSavedStateViewModelFactory
import com.example.clone_daum.di.module.ViewModelAssistedFactory
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchFragment @Inject constructor(
) : BaseDaggerFragment<SearchFragmentBinding, PopularViewModel>() {
    private val mLog = LoggerFactory.getLogger(SearchFragment::class.java)

    override val layoutId = R.layout.search_fragment

    init {
        mViewModelScope = SCOPE_ACTIVITY
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var layoutManager: ChipsLayoutManager
    @Inject lateinit var factory: DaggerSavedStateViewModelFactory

    private val mSearchViewModel: SearchViewModel by stateInject { factory }

    override fun initViewBinding() {
    }

    override fun bindViewModel() {
//        super.bindViewModel()
        mBinding.model        = mSearchViewModel
        mBinding.popularmodel = mViewModel

        addCommandEventModel(mSearchViewModel)
    }

    override fun initViewModelEvents() {
        mSearchViewModel.init()
        mViewModel.apply {
            init()
            chipLayoutManager.set(layoutManager)
        }
    }

    override fun onDestroyView() {
        mBinding.searchEdit.hideKeyboard()

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            SearchViewModel.CMD_BRS_OPEN    -> browserFragment(data.toString())
            PopularViewModel.CMD_BRS_SEARCH -> browserFragment(
                "https://m.search.daum.net/search?w=tot&q=${data.toString().urlencode()}&DA=13H")
        }
    }

    private fun browserFragment(url: Any) {
        if (mLog.isDebugEnabled) {
            mLog.debug("HIDE SEARCH FRAGMENT ${this}")
        }

        navigator.browserFragment(url.toString())
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [SearchFragmentModule::class])
        abstract fun contributeSearchFragmentInjector(): SearchFragment
    }

    @dagger.Module
    abstract class SearchFragmentModule {
        @Binds
        abstract fun bindSearchFragment(fragment: SearchFragment): Fragment

        @Binds
        @IntoMap
        @AssistedViewModelKey(SearchViewModel::class)
        abstract fun bindSearchViewModelFactory(vm: SearchViewModel.Factory)
                : ViewModelAssistedFactory<out ViewModel>

        @Binds
        abstract fun bindSavedStateRegistryOwner(fragment: SearchFragment): SavedStateRegistryOwner
    }
}