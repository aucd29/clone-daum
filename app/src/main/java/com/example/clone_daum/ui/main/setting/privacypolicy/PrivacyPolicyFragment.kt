@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.clone_daum.ui.main.setting.privacypolicy

import android.Manifest
import android.content.Intent
import androidx.fragment.app.Fragment
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import brigitte.runtimepermission.PermissionParams
import brigitte.runtimepermission.runtimePermissions
import com.example.clone_daum.R
import com.example.clone_daum.databinding.PrivacyPolicyFragmentBinding
import com.example.clone_daum.databinding.SettingNormalItemBinding
import com.example.clone_daum.databinding.SettingSwitchItemBinding
import com.example.clone_daum.model.local.SettingType
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-17 <p/>
 */

class PrivacyPolicyFragment @Inject constructor(
): BaseDaggerFragment<PrivacyPolicyFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.privacy_policy_fragment

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        viewModel.apply {
            initAdapter(R.layout.setting_category_item,
                R.layout.setting_normal_item,
                R.layout.setting_color_item,
                R.layout.setting_switch_item,
                R.layout.setting_check_item,
                R.layout.setting_depth_item,
                R.layout.daum_app_info_item
            )

            title(R.string.setting_privacy)
            privacyPolicySettingType()
            livePreference()
        }
    }

    private fun livePreference() {
        viewModel.items.get()?.forEach {
            when (it.title) {
                string(R.string.setting_privacy_policy_save_keyword) -> {
                    editPreference(it.checked, SettingViewModel.PREF_SAVE_KEYWORD) {
                        if (logger.isDebugEnabled) {
                            logger.debug("PREF_SAVE_KEYWORD : $it")
                        }
                    }
                }

                string(R.string.setting_privacy_policy_save_history) -> {
                    editPreference(it.checked, SettingViewModel.PREF_SAVE_HISTORY) {
                        if (logger.isDebugEnabled) {
                            logger.debug("PREF_SAVE_HISTORY : $it")
                        }
                    }
                }
            }
        }
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            SettingViewModel.CMD_SETTING_EVENT ->
                settingEvent(data as SettingType)
        }
    }

    private fun settingEvent(type: SettingType) {
        when (type.title) {
            string(R.string.setting_privacy_policy_save_keyword) ->
                switchPerformClick(type)

            string(R.string.setting_privacy_policy_save_history) ->
                switchPerformClick(type)

            string(R.string.setting_privacy_policy_remove_history) ->
                navigator.userHistoryFragment()

            string(R.string.setting_privacy_policy_set_download_path) ->
                runtimePermissions(PermissionParams(requireActivity(),
                    arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), { _, res ->
                        if (res) {
                            navigator.downloadPathFragment()?.closeCallback = {
                                if (logger.isDebugEnabled) {
                                    logger.debug("CLOSE CALLBACK $it")
                                }
                                summaryChange(type, it)
                            }
                        }
                    }, 0))

            string(R.string.setting_privacy_policy_manage_downloaded_file) ->
                fileManager()
        }
    }

    inline private fun viewHolder(pos: Int) =
        binding.privacyPolicyLayout.settingRecycler
            .findViewHolderForLayoutPosition(pos)

    inline private fun switchPerformClick(type: SettingType) {
        val vh = viewHolder(type.position)
        if (vh is RecyclerHolder) {
            val binding = vh.binding
            if (binding is SettingSwitchItemBinding) {
                binding.settingSwitchOption.performClick()
            }
        }
    }

    inline private fun summaryChange(type: SettingType, summary: String) {
        val vh = viewHolder(type.position)
        if (vh is RecyclerHolder) {
            val binding = vh.binding
            if (binding is SettingNormalItemBinding) {
                binding.settingNormalSummary.text = summary
            }
        }
    }

    private fun fileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivity(Intent.createChooser(intent, "Choose a file"), null)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [PrivacyPolicyFragmentModule::class])
        abstract fun contributePrivacyPolicyFragmentInjector(): PrivacyPolicyFragment
    }

    @dagger.Module
    abstract class PrivacyPolicyFragmentModule {
        @Binds
        abstract fun bindPrivacyPolicyFragment(fragment: PrivacyPolicyFragment): Fragment
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PrivacyPolicyFragment::class.java)
    }
}