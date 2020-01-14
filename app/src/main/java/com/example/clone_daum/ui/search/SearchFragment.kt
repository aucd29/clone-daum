package com.example.clone_daum.ui.search

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
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
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 29. <p/>
 */

class SearchFragment @Inject constructor(
) : BaseDaggerFragment<SearchFragmentBinding, PopularViewModel>() {
    companion object {
        private val logger = LoggerFactory.getLogger(SearchFragment::class.java)
    }

    override val layoutId = R.layout.search_fragment

    init {
        viewModelScope = SCOPE_ACTIVITY
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var factory: DaggerSavedStateViewModelFactory

    private val searchViewModel: SearchViewModel by stateInject { factory }

    override fun initViewBinding() {
    }

    override fun bindViewModel() {
        binding.model        = searchViewModel
        binding.popularmodel = viewModel

        addCommandEventModel(searchViewModel)
    }

    override fun initViewModelEvents() {
        viewModel.apply {
            // CHIP 레이아웃 의 아이템을 선택할 경우에 대해 처리 한다.
            initAdapter(R.layout.search_recycler_popular_item)
            init()
        }
        searchViewModel.apply {
            initAdapter(R.layout.search_recycler_history_item, R.layout.search_recycler_suggest_item)
            init()
        }
    }

    override fun onDestroyView() {
        binding.searchEdit.hideKeyboard()

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
        if (logger.isDebugEnabled) {
            logger.debug("HIDE SEARCH FRAGMENT ${this}")
        }

        navigator.browserFragment(url.toString(), true)
        binding.root.postDelayed({ hideKeyboard(binding.searchEdit) }, 100)
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