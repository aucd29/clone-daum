package com.example.clone_daum.ui.main.navigation

import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.NavigationFragmentBinding
import com.example.clone_daum.common.Config
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R
import com.example.clone_daum.ui.Navigator
import dagger.Binds
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 20. <p/>
 */

class NavigationFragment constructor(
) : BaseDaggerFragment<NavigationFragmentBinding, NavigationViewModel>()
    , OnBackPressedListener, DrawerLayout.DrawerListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(NavigationFragment::class.java)
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var config: Config
    @Inject lateinit var adapter: NavigationTabAdapter

    override val layoutId = R.layout.navigation_fragment

    override fun initViewBinding() = mBinding.run {
        naviContainer.apply {
            singleTimer(50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ -> openDrawer(GravityCompat.END) }

            addDrawerListener(this@NavigationFragment)
        }

        naviView.layoutWidth(config.SCREEN.x)
        naviViewpager.adapter = adapter
    }

    override fun initViewModelEvents() = mViewModel.run {
        observe(brsOpenEvent) {
            navigator.browserFragment(it)
        }
    }

    override fun onDestroyView() {
        mBinding.apply {
            naviContainer.removeDrawerListener(this@NavigationFragment)
        }

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // COMMAND
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    override fun onBackPressed() = mBinding.run {
        naviContainer.closeDrawer(GravityCompat.END)

        true
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // DrawerLayout.DrawerListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onDrawerClosed(drawerView: View) {
        finish()
    }
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        mViewModel.backgroundAlpha.set(slideOffset)
    }
    override fun onDrawerStateChanged(newState: Int) { }
    override fun onDrawerOpened(drawerView: View) { }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [NavigationFragmentModule::class])
        abstract fun contributeNavigationFragmentInjector(): NavigationFragment
    }

    @dagger.Module
    abstract class NavigationFragmentModule {
        @Binds
        abstract fun bindNavigationFragment(fragment: NavigationFragment): Fragment

        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: NavigationFragment): SavedStateRegistryOwner

        @dagger.Module
        companion object {
            @JvmStatic
            @Provides
            fun provideNavigationFragmentManager(fragment: Fragment): FragmentManager =
                fragment.childFragmentManager
        }
    }
}
