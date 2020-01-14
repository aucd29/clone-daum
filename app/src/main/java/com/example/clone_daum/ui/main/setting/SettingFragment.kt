package com.example.clone_daum.ui.main.setting

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.RecyclerAdapter
import brigitte.di.dagger.scope.FragmentScope
import brigitte.string
import com.example.clone_daum.R
import com.example.clone_daum.databinding.SettingFragmentBinding
import com.example.clone_daum.model.local.SettingType
import com.example.clone_daum.ui.Navigator
import com.example.clone_daum.ui.main.login.LoginViewModel
import dagger.Binds
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class SettingFragment @Inject constructor(
): BaseDaggerFragment<SettingFragmentBinding, SettingViewModel>() {
    override val layoutId = R.layout.setting_fragment

    private val loginViewModel: LoginViewModel by activityInject()

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var adapter: RecyclerAdapter<SettingType>

    override fun initViewBinding() {
        adapter.viewModel = viewModel
        binding.settingInclude.settingRecycler.adapter = adapter
    }

    override fun initViewModelEvents() {
        viewModel.apply {
            title(R.string.setting_title)
            mainSettingType()
        }
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            SettingViewModel.CMD_SETTING_EVENT -> {
                val type = data as SettingType
                val title = type.title

                when (title) {
                    string(R.string.setting_login_info)   -> setLoginInfo()
                    string(R.string.setting_privacy)  -> setPrivacy()
                    string(R.string.setting_alarm_item) -> setAlarmItem()
                    string(R.string.setting_alarm_preference) -> setAlarmPreference()
                    string(R.string.setting_popup_blocking)     -> setBlockingPopup(type)
                    string(R.string.setting_character_set)    -> setCharacterSet(type)
                    string(R.string.setting_media_autoplay) -> setMediaAutoPlay(type)
                    string(R.string.setting_used_location) -> setUsedLocation(type)
                    string(R.string.setting_simple_search) -> setSimpleSearch(type)
                    string(R.string.setting_daumapp_info)   -> setDaumAppInfo()
                    string(R.string.setting_research)       -> setResearch()
                }
            }
        }
    }

    private fun setLoginInfo() {
        if (loginViewModel.status.value == false) {
            navigator.loginFragment()
        } else {
            // TODO login 된 화면이 보이면 되는데
        }
    }

    private fun setPrivacy() {
        navigator.privacyPolicyFragment()
    }

    private fun setAlarmItem() {
        navigator.alarmSettingFragment()
    }

    private fun setAlarmPreference() {
        navigator.alarmPreferenceFragment()
    }

    private fun setBlockingPopup(type: SettingType) {
//        editPreference(type.checked, SettingViewModel.PREF_BLOCKING_POPUP) {
//            if (logger.isDebugEnabled) {
//                logger.debug("PREFERENCE BLOCKING POPUP : $it")
//            }
//        }
    }

    private fun setCharacterSet(type: SettingType) {
        type.option?.let {
            editPreference(it, SettingViewModel.PREF_CHARACTER_SET) {
                if (logger.isDebugEnabled) {
                    logger.debug("PREFERENCE CHARACTER SET : $it")
                }
            }
        }

        var i = 0
        val data = arrayOf(
            string(R.string.setting_character_set_euckr),
            string(R.string.setting_character_set_latin),
            string(R.string.setting_character_set_unicode),
            string(R.string.setting_character_set_jap_iso),
            string(R.string.setting_character_set_jap_jis),
            string(R.string.setting_character_set_jap_euc)
        )

        val defaultString = type.option?.value!!
        while (i < data.size) {
            if (defaultString == data[i]) {
                if (logger.isDebugEnabled) {
                    logger.debug("DEFAULT STRING : $defaultString ($i)")
                }
                break
            }

            ++i
        }

        val defaultItem = i
        val dlg = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.setting_character_set)
            .setSingleChoiceItems(data, defaultItem) { dlg, which ->
                val chooseItem = data[which]
                if (logger.isDebugEnabled) {
                    logger.debug("CHARACTER SET: WHICH $chooseItem($which)")
                }

                type.option.value = chooseItem
                adapter.notifyItemChanged(type.position)

                dlg.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dlg, _ ->
                dlg.dismiss()
            }

        dlg.show()
    }

    private fun setMediaAutoPlay(type: SettingType) {
        type.option?.let {
            editPreference(it, SettingViewModel.PREF_MEDIA_AUTOPLAY) {
                if (logger.isDebugEnabled) {
                    logger.debug("PREFERENCE MEDIA AUTOPLAY : $it")
                }
            }
        }

        var i = 0
        val data = arrayOf(
            string(R.string.setting_media_autoplay_always),
            string(R.string.setting_media_autoplay_wifi_only),
            string(R.string.setting_media_autoplay_not_used)
        )

        val defaultString = type.option?.value!!
        while (i < data.size) {
            if (defaultString == data[i]) {
                if (logger.isDebugEnabled) {
                    logger.debug("DEFAULT STRING : $defaultString ($i)")
                }
                break
            }

            ++i
        }

        val defaultItem = i
        val dlg = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.setting_media_autoplay)
            .setSingleChoiceItems(data, defaultItem) { dlg, which ->
                val chooseItem = data[which]
                if (logger.isDebugEnabled) {
                    logger.debug("MEDIA AUTO PLAY : WHICH $chooseItem($which)")
                }

                type.option.value = chooseItem
                adapter.notifyItemChanged(type.position)

                dlg.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dlg, _ ->
                dlg.dismiss()
            }

        dlg.show()
    }

    private fun setUsedLocation(type: SettingType) {
        editPreference(type.checked, SettingViewModel.PREF_USE_LOCATION_INFO) {
            if (logger.isDebugEnabled) {
                logger.debug("PREFERENCE USE LOCATION INFO : $it")
            }
        }

        type.checked.value = !type.checked.value!!

        adapter.notifyItemChanged(type.position)
    }

    private fun setSimpleSearch(type: SettingType) {
        editPreference(type.checked, SettingViewModel.PREF_SIMPLE_SEARCH) {
            if (logger.isDebugEnabled) {
                logger.debug("PREFERENCE SIMPLE SEARCH : $it")
            }
        }
    }

    private fun setDaumAppInfo() {
        navigator.daumAppInfoFragment()
    }

    private fun setResearch() {
        navigator.researchFragment()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [SettingFragmentModule::class])
        abstract fun contributeSettingFragmentInjector(): SettingFragment
    }

    @dagger.Module
    abstract class SettingFragmentModule {
        @Binds
        abstract fun bindSettingFragment(fragment: SettingFragment): Fragment

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
        private val logger = LoggerFactory.getLogger(SettingFragment::class.java)
    }
}

// PreferenceFragmentCompat 를 써볼까 했는데 어차피 다 커스텀 해야 되는데
// 무슨 의미가 있나 싶어서 그냥 만드는 걸로 변경 하기로 [aucd29][2019-12-16]

////////////////////////////////////////////////////////////////////////////////////
//
//
//
////////////////////////////////////////////////////////////////////////////////////

//class SettingDetailFragment: PreferenceFragmentCompat() {
//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        addPreferencesFromResource(R.xml.setting_root)
//    }
//
//    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
//        toast("preference: ${preference?.key}")
//
//        return super.onPreferenceTreeClick(preference)
//    }
//}