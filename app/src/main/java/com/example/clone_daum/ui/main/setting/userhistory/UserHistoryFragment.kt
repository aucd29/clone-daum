package com.example.clone_daum.ui.main.setting.userhistory

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.RecyclerHolder
import brigitte.di.dagger.scope.FragmentScope
import brigitte.string
import com.example.clone_daum.R
import com.example.clone_daum.databinding.SettingCheckItemBinding
import com.example.clone_daum.databinding.UserHistoryFragmentBinding
import com.example.clone_daum.model.local.SettingType
import com.example.clone_daum.ui.main.setting.SettingViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-18 <p/>
 */

class UserHistoryFragment @Inject constructor(
): BaseDaggerFragment<UserHistoryFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.user_history_fragment

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        mViewModel.apply {
            title(R.string.setting_privacy_policy_remove_history)
            userHistorySettingType()
        }

        livePreference()
    }

    private fun livePreference() {
        var i = 0
        val keys = arrayOf(SettingViewModel.PREF_REMOVE_HISTORY,
            SettingViewModel.PREF_REMOVE_CACHE,
            SettingViewModel.PREF_AUTOCOMPLETE_DATA,
            SettingViewModel.PREF_MULTI_BRS_TAB,
            SettingViewModel.PREF_RECENT_KEYWORD)

        // live data 를 observe 하여 내용 변경 시 preference 에 적용 하도록 한다.
        mViewModel.items.get()?.forEach {
            if (mLog.isDebugEnabled) {
                mLog.debug("ITEM SIZE = ${mViewModel.items.get()?.size}, KEY SIZE = ${keys.size}")
            }

            val key = keys[i]
            editPreference(it.checked, key) {
                if (mLog.isDebugEnabled) {
                    mLog.debug("$key : $it")
                }
            }

            ++i
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
            string(R.string.setting_remove_history_history),
            string(R.string.setting_remove_history_cache),
            string(R.string.setting_remove_history_auto_complete),
            string(R.string.setting_remove_history_multi_browser_tab),
            string(R.string.setting_remove_history_recent_keyword) ->
                checkboxPerformClick(type)
        }
    }

    inline private fun viewHolder(pos: Int) =
        mBinding.userhistoryLayout.settingRecycler
            .findViewHolderForLayoutPosition(pos)

    inline private fun checkboxPerformClick(type: SettingType) {
        val vh = viewHolder(type.position)
        if (vh is RecyclerHolder) {
            val binding = vh.mBinding
            if (binding is SettingCheckItemBinding) {
                binding.settingCheckOption.performClick()
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
        @ContributesAndroidInjector(modules = [UserHistoryFragmentModule::class])
        abstract fun contributeUserHistoryFragmentInjector(): UserHistoryFragment
    }

    @dagger.Module
    abstract class UserHistoryFragmentModule {
        @Binds
        abstract fun bindUserHistoryFragment(fragment: UserHistoryFragment): Fragment

        @dagger.Module
        companion object {
        }
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(UserHistoryFragment::class.java)
    }
}