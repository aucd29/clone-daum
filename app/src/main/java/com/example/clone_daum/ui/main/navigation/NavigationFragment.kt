package com.example.clone_daum.ui.main.navigation

import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.NavigationFragmentBinding
import com.example.clone_daum.common.Config
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject
import com.example.clone_daum.R
import com.example.clone_daum.model.remote.Sitemap
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.main.login.LoginViewModel
import com.example.clone_daum.ui.main.navigation.shortcut.FrequentlySiteViewModel
import com.example.clone_daum.ui.main.navigation.shortcut.SitemapViewModel
import com.google.android.material.navigation.NavigationView
import dagger.Binds
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 20. <p/>
 *
 * 디자인이 변경되었었네 [aucd29][2019-10-16]
 */

class NavigationFragment constructor(
) : BaseDaggerFragment<NavigationFragmentBinding, NavigationViewModel>(),
    OnBackPressedListener, DrawerLayout.DrawerListener {
    override val layoutId = R.layout.navigation_fragment

    companion object {
        private val mLog = LoggerFactory.getLogger(NavigationFragment::class.java)

        private val URI_CAFE = "https://m.cafe.daum.net"
        private val URI_MAIL = "https://m.mail.daum.net/"
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var config: Config

    private val mTranslationX: Float by lazy { (-30f).dpToPx(requireContext()) }
    private val mSitemapModel: SitemapViewModel by inject()
    private val mFrequentlySiteModel: FrequentlySiteViewModel by inject()
    private val mLoginViewModel: LoginViewModel by activityInject()

    private val mStackChanger: () -> Unit = {
        val name = this@NavigationFragment.javaClass.simpleName
        val currentName = activity?.supportFragmentManager?.current?.javaClass?.simpleName

        if (name == currentName) {
            translationRight()
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()

        // sitemap, frequently 의 view model 은 shortcut fragment 내에서만 동작해야 하므로
        // injectOf 를 이용 한다.
        mBinding.apply {
            sitemapModel        = mSitemapModel
            frequentlySiteModel = mFrequentlySiteModel
            loginModel          = mLoginViewModel
        }

        addCommandEventModels(mSitemapModel, mFrequentlySiteModel)
    }

    override fun initViewBinding() = mBinding.run {
        naviContainer.apply {
            singleTimer(50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ -> openDrawer(GravityCompat.END) }

            addDrawerListener(this@NavigationFragment)
        }

        naviView.layoutWidth(config.SCREEN.x)
        addStackChangeListener()
        mLoginViewModel.checkIsLoginSession()

        Unit
    }

    override fun initViewModelEvents() {
        mFrequentlySiteModel.init(disposable())

        observe(mLoginViewModel.status) {
            if (mLog.isInfoEnabled) {
                mLog.info("LOGIN STATUS : $it")
            }
        }
    }

    override fun onDestroyView() {
        removeStackChangeListener()
        mBinding.naviContainer.removeDrawerListener(this@NavigationFragment)

        super.onDestroyView()
    }

    override fun commandFinish() {
        mBinding.naviContainer.closeDrawer(GravityCompat.END)
    }

    private fun addStackChangeListener() =
        activity?.supportFragmentManager?.addOnBackStackChangedListener(mStackChanger)

    private fun removeStackChangeListener() =
        activity?.supportFragmentManager?.removeOnBackStackChangedListener(mStackChanger)

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // COMMAND
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        if (mLog.isDebugEnabled) {
            mLog.debug("COMMAND EVENT $cmd = $data")
        }

        if (checkRequireLogin(cmd)) {
            return
        }

        when (cmd) {
            NavigationViewModel.CMD_SETTING        -> settingFragment()

            NavigationViewModel.CMD_LOGIN          -> loginFragment()
            NavigationViewModel.CMD_ALARM          -> alarmFragment()
            NavigationViewModel.CMD_BOOKMARK       -> bookmarkFragment()

            NavigationViewModel.CMD_MAIL           -> mailFragment()
            NavigationViewModel.CMD_CAFE           -> cafeFragment()
            NavigationViewModel.CMD_URL_HISTORY    -> urlHistoryFragment()

            NavigationViewModel.CMD_BROWSER        -> browserFragment(data.toString())

            NavigationViewModel.CMD_EDIT_HOME_MENU -> editHomeMenuFragment()
            NavigationViewModel.CMD_TEXT_SIZE      -> resizeTextFragment()

            SitemapViewModel.CMD_OPEN_APP          -> openApp(data as Sitemap)
        }

        NavigationViewModel.run {
            when(cmd) {
                CMD_SETTING,
                CMD_LOGIN,
                CMD_ALARM,
                CMD_BOOKMARK,
                CMD_MAIL,
                CMD_CAFE,
                CMD_URL_HISTORY,
                CMD_EDIT_HOME_MENU -> translationLeft()
            }
        }
    }

    private fun checkRequireLogin(cmd: String) = when(cmd) {
        // TODO 로그인이 필요한 항목의 경우 이곳에서 정의 한다.
        NavigationViewModel.CMD_BOOKMARK,
        NavigationViewModel.CMD_MAIL->
            if (mLoginViewModel.status.value == true) {
                loginFragment()

                true
            } else {
                false
            }
        else -> false
    }

    private fun settingFragment() =
        navigator.settingFragment()

    private fun loginFragment() {
        if (mLoginViewModel.status.value == true) {
            return
        }

        navigator.loginFragment()
    }

    private fun alarmFragment() =
        navigator.alarmFragment()

    private fun bookmarkFragment() =
        navigator.bookmarkFragment()

    private fun mailFragment() =
        browserFragment(URI_MAIL)

    private fun cafeFragment() =
        navigator.cafeFragment()

    private fun urlHistoryFragment() =
        navigator.urlHistoryFragment()

    private fun editHomeMenuFragment() =
        navigator.homeMenuFragment()

    private fun resizeTextFragment() =
        navigator.homeTextFragment()

    private fun browserFragment(url: String) =
        navigator.browserFragment(url)

    private fun openApp(item: Sitemap) {
        if (item.isApp) {
            requireContext().launchApp(item.url)
        } else {
            navigator.browserFragment(item.url)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // BACKGROUND TRANSLATION
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private fun translationLeft() {
        mBinding.root.animate().translationX(mTranslationX).start()
    }

    private fun translationRight() {
        with(mBinding.root) {
            if (translationX == mTranslationX) {
                animate().translationX(0f).start()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // OnBackPressedListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onBackPressed() = mBinding.run {
        if (naviContainer.isDrawerVisible(mBinding.naviView)) {
            naviContainer.closeDrawer(GravityCompat.END)
        } else {
            finish()
        }

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
