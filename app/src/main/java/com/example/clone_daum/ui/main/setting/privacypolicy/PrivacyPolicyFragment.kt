@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.clone_daum.ui.main.setting.privacypolicy

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.RecyclerHolder
import brigitte.di.dagger.scope.FragmentScope
import brigitte.prefs
import brigitte.runtimepermission.PermissionParams
import brigitte.runtimepermission.runtimePermissions
import brigitte.string
import com.example.clone_daum.R
import com.example.clone_daum.databinding.PrivacyPolicyFragmentBinding
import com.example.clone_daum.databinding.SettingSwitchItemBinding
import com.example.clone_daum.model.local.SettingType
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-17 <p/>
 */

class PrivacyPolicyFragment @Inject constructor(
): BaseDaggerFragment<PrivacyPolicyFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.privacy_policy_fragment

    private var mSettingType: List<SettingType>? = null

    @Inject lateinit var navigator: Navigator

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.title(R.string.setting_privacy)
        mSettingType = mViewModel.privacyPolicySettingType()
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            SettingViewModel.CMD_SETTING_EVENT -> {
                val type = data as SettingType

                when (type.title) {
                    string(R.string.setting_save_keyword) -> {
                        editPreference(type.checked, SettingViewModel.PREF_SAVE_KEYWORD) {
                            if (mLog.isDebugEnabled) {
                                mLog.debug("PREF_SAVE_KEYWORD : $it")
                            }
                        }

                        switchPerformClick(type)
                    }
                    string(R.string.setting_save_history) -> {
                        editPreference(type.checked, SettingViewModel.PREF_SAVE_HISTORY) {
                            if (mLog.isDebugEnabled) {
                                mLog.debug("PREF_SAVE_HISTORY : $it")
                            }
                        }

                        switchPerformClick(type)
                    }
                    string(R.string.setting_privacy_policy_remove_history) -> navigator.userHistoryFragment()
                    string(R.string.setting_privacy_policy_set_download_path) -> {
                        runtimePermissions(PermissionParams(requireActivity(),
                            arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            { _, res ->
                                if (res) navigator.downloadPathFragment()
                            }, 0))
                    }
                    string(R.string.setting_privacy_policy_manage_downloaded_file) ->
                        fileManager()
                }
            }
        }
    }

    inline private fun viewHolder(pos: Int) =
        mBinding.privacyPolicyLayout.settingRecycler
            .findViewHolderForLayoutPosition(pos)

    inline private fun switchPerformClick(type: SettingType) {
        val vh = viewHolder(type.position)
        if (vh is RecyclerHolder) {
            val binding = vh.mBinding
            if (binding is SettingSwitchItemBinding) {
                binding.settingSwitchOption.performClick()
            }
        }
    }

    private fun fileManager() {
        startActivity(Intent(Intent.ACTION_GET_CONTENT).apply {
            val dnPath = prefs().getString(SettingViewModel.PREF_DOWNLOADPATH,
                Environment.getExternalStorageDirectory().toString())!!
            val file = File(dnPath)
            setDataAndType(Uri.fromFile(file), "*/*")
        })
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
        private val mLog = LoggerFactory.getLogger(PrivacyPolicyFragment::class.java)
    }
}