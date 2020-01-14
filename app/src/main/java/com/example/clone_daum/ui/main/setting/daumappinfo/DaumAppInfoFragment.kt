package com.example.clone_daum.ui.main.setting.daumappinfo

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.RecyclerAdapter
import brigitte.di.dagger.scope.FragmentScope
import brigitte.string
import brigitte.toast
import com.example.clone_daum.R
import com.example.clone_daum.databinding.DaumAppInfoFragmentBinding
import com.example.clone_daum.model.local.SettingType
import com.example.clone_daum.ui.Navigator
import dagger.Binds
import dagger.Module
import dagger.Provides
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
    @Inject lateinit var adapter: RecyclerAdapter<SettingType>

    override fun initViewBinding() {
        adapter.viewModel = viewModel
        binding.daumAppInfoRecycler.adapter = adapter
    }

    override fun initViewModelEvents() {
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            DaumAppInfoViewModel.CMD_DAUMAPP_INFO_EVENT -> {
                val type = data as SettingType

                if (logger.isDebugEnabled) {
                    logger.debug("$cmd : ${data.title}")
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
                if (logger.isDebugEnabled) {
                    logger.debug("UPDATE DAUM APP")
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
            @JvmStatic
            @Provides
            fun provideSettingTypeAdapter(): RecyclerAdapter<SettingType> =
                RecyclerAdapter(arrayOf(R.layout.setting_category_item,
                    R.layout.setting_normal_item,
                    R.layout.setting_color_item,
                    R.layout.setting_switch_item,
                    R.layout.setting_check_item,
                    R.layout.setting_depth_item,
                    R.layout.daum_app_info_item))
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DaumAppInfoFragment::class.java)
    }
}