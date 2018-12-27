package com.example.clone_daum.ui.main.navigation

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.clone_daum.databinding.NavigationFragmentBinding
import com.example.clone_daum.di.module.Config
import com.example.common.BaseDaggerFragment
import com.example.common.OnBackPressedListener
import com.example.common.finish
import com.example.common.layoutWidth
import dagger.Binds
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 20. <p/>
 */

class NavigationFragment: BaseDaggerFragment<NavigationFragmentBinding, NavigationViewModel>(), OnBackPressedListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(NavigationFragment::class.java)
    }

    @Inject lateinit var config: Config
    @Inject lateinit var drawerListener: DrawerCloseCallback

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewBinding()
    }

    override fun settingEvents() = mViewModel.run {

    }

    private fun viewBinding() = mBinding.run {
        navContainer.run {
            postDelayed({ openDrawer(GravityCompat.END) }, 50)
            addDrawerListener(drawerListener)
        }

        navView.run {
            val lp = layoutParams
            lp.width = config.SCREEN.x

            if (mLog.isDebugEnabled) {
                mLog.debug("RESIZE NAV VIEW (${lp.width}")
            }

            layoutParams = lp
        }

//        navLayout.layoutWidth(config.SCREEN.x)
    }

    override fun onBackPressed() = mBinding.run {
        navContainer.closeDrawer(GravityCompat.END)

        true
    }

    override fun onDestroyView() {
        mBinding.run {
            navContainer.removeDrawerListener(drawerListener)
        }

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): NavigationFragment

        @Binds
        abstract fun bindDrawerCloseCallback(callback: DrawerCloseCallback): DrawerLayout.DrawerListener
    }
}

class DrawerCloseCallback @Inject constructor(val mFragment: NavigationFragment) : DrawerLayout.DrawerListener {
    override fun onDrawerClosed(drawerView: View) {
        mFragment.finish()
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        // TODO background alpha 처리 필요
    }

    override fun onDrawerStateChanged(newState: Int) { }
    override fun onDrawerOpened(drawerView: View) { }
}