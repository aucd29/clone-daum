package com.example.clone_daum.ui.main.navigation

import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.clone_daum.databinding.NavigationFragmentBinding
import com.example.clone_daum.common.Config
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R
import com.example.clone_daum.ui.FragmentFactory
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 20. <p/>
 */

class NavigationFragment @Inject constructor() : BaseDaggerFragment<NavigationFragmentBinding, NavigationViewModel>()
    , OnBackPressedListener, DrawerLayout.DrawerListener {
    companion object {
        private val mLog = LoggerFactory.getLogger(NavigationFragment::class.java)
    }

    @Inject lateinit var config: Config
    @Inject lateinit var fragmentFactory: FragmentFactory

    override val layoutId = R.layout.navigation_fragment

    override fun initViewBinding() = mBinding.run {
        naviContainer.apply {
            singleTimer(50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    openDrawer(GravityCompat.END)
                }

//            postDelayed({ openDrawer(GravityCompat.END) }, 50)
            addDrawerListener(this@NavigationFragment)
        }

        naviView.layoutWidth(config.SCREEN.x)
        naviTab.setOnNavigationItemSelectedListener {
            if (mLog.isDebugEnabled) {
                mLog.debug("ITEM ID : ${it.title} : ${it.itemId}\n")
            }

            // R.id.mnu_navi_shortcut 입력 시 unresolved reference 오류가 발생되면
            // R 경로를 수동으로 입력해주면 된다.
            when (it.itemId) {
                R.id.mnu_navi_shortcut -> {
                    fragmentFactory.shortcutFragment(childFragmentManager)
                    true
                }
                R.id.mnu_navi_mail -> {
                    fragmentFactory.mailFragment(childFragmentManager)
                    true
                }
                R.id.mnu_navi_cafe -> {
                    fragmentFactory.cafeFragment(childFragmentManager)
                    true
                }
                else -> false
            }
        }

        fragmentFactory.shortcutFragment(childFragmentManager, true)
    }

    override fun initViewModelEvents() = mViewModel.run {
        observe(brsOpenEvent) {
            fragmentFactory.browserFragment(fragmentManager, it)
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
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    override fun onBackPressed() = mBinding.run {
        naviContainer.closeDrawer(GravityCompat.END)

        true
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // AWARE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun commandFinish(animate: Boolean) { onBackPressed() }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // DrawerLayout.DrawerListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onDrawerClosed(drawerView: View) { finish() }
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        mBinding.naviBackground.alpha = slideOffset
    }
    override fun onDrawerStateChanged(newState: Int) { }
    override fun onDrawerOpened(drawerView: View) { }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector
        abstract fun contributeInjector(): NavigationFragment
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// 시간 날때 대거 소스를 분석해봐야 할듯 -_-
//
////////////////////////////////////////////////////////////////////////////////////