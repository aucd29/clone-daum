package com.example.clone_daum.ui.main.setting.daumappinfo

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import brigitte.string
import brigitte.toast
import com.example.clone_daum.R
import com.example.clone_daum.databinding.DaumAppInfoFragmentBinding
import com.example.clone_daum.model.local.SettingType
import com.example.clone_daum.ui.Navigator
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-17 <p/>
 */

class DaumAppInfoFragment @Inject constructor(
): BaseDaggerFragment<DaumAppInfoFragmentBinding, DaumAppInfoViewModel>() {
    override val layoutId = R.layout.daum_app_info_fragment

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            DaumAppInfoViewModel.CMD_DAUMAPP_INFO_EVENT -> {
                val type = data as SettingType

                if (mLog.isDebugEnabled) {
                    mLog.debug("$cmd : ${data.title}")
                }

                // 경로는 임시로 설정한다.
                when (type.title) {
                    string(R.string.setting_daumapp_move_mobile_web) ->
                        navigator.browserFragment("https://m.daum.net")
                    string(R.string.setting_daumapp_other_app) ->
                        navigator.browserFragment("https://m.daum.net")
                    string(R.string.setting_daumapp_open_source_license) ->
                        navigator.browserFragment("https://m.daum.net")
                    string(R.string.setting_daumapp_terms_of_use) ->
                        navigator.browserFragment("https://m.daum.net")
                    string(R.string.setting_daumapp_privacy_policy) ->
                        navigator.browserFragment("https://m.daum.net")
                    string(R.string.setting_daumapp_contact_us) ->
                        navigator.browserFragment("https://m.daum.net")
                }
            }

            DaumAppInfoViewModel.CMD_UPDATE -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("UPDATE DAUM APP")
                }

                toast(R.string.setting_daumapp_update_daumapp)
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
        @FragmentScope
        @ContributesAndroidInjector(modules = [DaumAppInfoFragmentModule::class])
        abstract fun contributeDaumAppInfoFragmentInjector(): DaumAppInfoFragment
    }

    @dagger.Module
    abstract class DaumAppInfoFragmentModule {
        @Binds
        abstract fun bindDaumAppInfoFragment(fragment: DaumAppInfoFragment): Fragment

        @dagger.Module
        companion object {
        }
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(DaumAppInfoFragment::class.java)
    }
}