package com.example.clone_daum.ui.browser.urlhistory

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.UrlHistoryFragmentBinding
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.browser.BrowserFragment
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryFragment @Inject constructor(
) : BaseDaggerFragment<UrlHistoryFragmentBinding, UrlHistoryViewModel>(), OnBackPressedListener {
    override val layoutId  = R.layout.url_history_fragment

    companion object {
        private val logger = LoggerFactory.getLogger(UrlHistoryFragment::class.java)
    }

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        viewModel.apply {
            initAdapter(R.layout.url_history_item, R.layout.url_history_expandable_item)
            initItems()
        }

        urlHistoryBarBackground()
    }

    private fun urlHistoryBarBackground() {
        viewModel.editMode.observe {
            binding.urlhistoryBar.apply {
                if (it.get()) {
                    fadeColorResource(android.R.color.white, com.example.clone_daum.R.color.colorAccent)
                } else {
                    fadeColorResource(com.example.clone_daum.R.color.colorAccent, android.R.color.white)
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onBackPressed(): Boolean {
        viewModel.apply {
            if (editMode.get()) {
                editMode.set(false)
                return true
            }
        }

        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        UrlHistoryViewModel.apply {
            when (cmd) {
                CMD_BRS_OPEN -> showBrowser(data as String)
            }
        }
    }

    private fun showBrowser(url: String) {
        if (logger.isDebugEnabled) {
            logger.debug("SHOW BROWSER $url")
        }

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
        @FragmentScope
        @ContributesAndroidInjector(modules = [UrlHistoryFragmentModule::class])
        abstract fun contributeUrlHistoryFragmentInjector(): UrlHistoryFragment
    }

    @dagger.Module
    abstract class UrlHistoryFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: UrlHistoryFragment): SavedStateRegistryOwner
    }
}
