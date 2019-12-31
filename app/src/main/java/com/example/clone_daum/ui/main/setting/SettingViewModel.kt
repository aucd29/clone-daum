package com.example.clone_daum.ui.main.setting

import android.app.Application
import android.os.Environment
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import brigitte.RecyclerViewModel
import brigitte.prefs
import brigitte.string
import brigitte.widget.viewpager.OffsetDividerItemDecoration
import com.example.clone_daum.R
import com.example.clone_daum.model.local.SettingType
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-20 <p/>
 */

class SettingViewModel @Inject constructor(
    app: Application
) : RecyclerViewModel<SettingType>(app) {

    var title: ObservableField<String>? = null
    val itemDecoration = ObservableField(OffsetDividerItemDecoration(app,
        R.drawable.shape_divider_gray,  0, 0))

    init {
        initAdapter(R.layout.setting_category_item,
            R.layout.setting_normal_item,
            R.layout.setting_color_item,
            R.layout.setting_switch_item,
            R.layout.setting_check_item,
            R.layout.setting_depth_item,
            R.layout.daum_app_info_item
        )
    }

    fun title(@StringRes resid: Int) {
        title = ObservableField(app.string(resid))
    }

    fun mainSettingType(): ArrayList<SettingType> {
        if (mLog.isDebugEnabled) {
            mLog.debug("MAIN SETTING TYPE")
        }

        var nickName: String
        var blockingPopup: Boolean
        var characterSet: String
        var mediaAutoplay: String
        var useLocationInfo: Boolean
        var simpleSearch: Boolean
        var daumAppInfo: String

        app.prefs().run {
            nickName        = getString(PREF_NICK_NAME, string(R.string.setting_pls_login))!!
            blockingPopup   = getBoolean(PREF_BLOCKING_POPUP, true)
            characterSet    = getString(PREF_CHARACTER_SET, string(R.string.setting_character_set_euckr))!!
            mediaAutoplay   = getString(PREF_MEDIA_AUTOPLAY, string(R.string.setting_media_autoplay_wifi_only))!!
            useLocationInfo = getBoolean(PREF_USE_LOCATION_INFO, true)
            simpleSearch    = getBoolean(PREF_SIMPLE_SEARCH, false)
            daumAppInfo     = getString(PREF_DAUMAPP_INFO, string(R.string.setting_daumapp_use_lastest_app))!!
        }

        val autoLogin = if (nickName != string(R.string.setting_pls_login)) {
            string(R.string.setting_auto_login_summary)
        } else null

        var idx = 0

        val data = arrayListOf(
            SettingType(idx++, string(R.string.setting_category_myinfo), type = SettingType.T_CATEGORY),
            SettingType(idx++, string(R.string.setting_login_info), option = MutableLiveData(nickName)),
            SettingType(idx++, string(R.string.setting_auto_login), summary = autoLogin,
                enabled = MutableLiveData(false)),
            SettingType(idx++, string(R.string.setting_privacy)),


            SettingType(idx++, string(R.string.setting_category_alarm), type = SettingType.T_CATEGORY),
            SettingType(idx++, string(R.string.setting_alarm_item),
                string(R.string.setting_alarm_item_summary)),
            SettingType(idx++, string(R.string.setting_alarm_preference),
                string(R.string.setting_alarm_preference_summary)),


            SettingType(idx++, string(R.string.setting_category_browser), type = SettingType.T_CATEGORY),
            SettingType(idx++, string(R.string.setting_popup_blocking),
                string(R.string.setting_popup_blocking_summary),
                checked = MutableLiveData(blockingPopup),
                type = SettingType.T_SWITCH),
            SettingType(idx++, string(R.string.setting_character_set),
                option = MutableLiveData(characterSet),
                type = SettingType.T_COLOR),
            SettingType(idx++, string(R.string.setting_media_autoplay),
                string(R.string.setting_media_autoplay_summary),
                option = MutableLiveData(mediaAutoplay),
                type = SettingType.T_COLOR),


            SettingType(idx++, string(R.string.setting_category_other), type = SettingType.T_CATEGORY),
            SettingType(idx++, string(R.string.setting_used_location),
                string(R.string.setting_used_location_summary),
                checked = MutableLiveData(useLocationInfo),
                type = SettingType.T_SWITCH),
            SettingType(idx++, string(R.string.setting_simple_search),
                string(R.string.setting_simple_search_summary),
                checked = MutableLiveData(simpleSearch),
                type = SettingType.T_SWITCH),


            SettingType(idx++, string(R.string.setting_category_service_info), type = SettingType.T_CATEGORY),
            SettingType(idx++, string(R.string.setting_daumapp_info),
                option = MutableLiveData(daumAppInfo),
                type = SettingType.T_COLOR),
            SettingType(idx, string(R.string.setting_research))
        )

        items.set(data)

        return data
    }

    fun privacyPolicySettingType(): ArrayList<SettingType> {
        if (mLog.isDebugEnabled) {
            mLog.debug("PRIVACY POLICY TYPE")
        }

        val envDownloadPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).toString()
        val downloadPath = app.prefs().getString(PREF_DOWNLOADPATH, envDownloadPath)
        val saveKeyword = app.prefs().getBoolean(PREF_SAVE_KEYWORD, true)
        val saveHistory = app.prefs().getBoolean(PREF_SAVE_HISTORY, true)

        var idx = 0
        val data = arrayListOf(
            SettingType(idx++, string(R.string.setting_privacy_policy_save_keyword),
                checked = MutableLiveData(saveKeyword),
                type = SettingType.T_SWITCH),
            SettingType(idx++, string(R.string.setting_privacy_policy_save_history),
                checked = MutableLiveData(saveHistory),
                type = SettingType.T_SWITCH),
            SettingType(idx++, string(R.string.setting_privacy_policy_remove_history)),
            SettingType(idx++, string(R.string.setting_privacy_policy_set_download_path),
                summary = downloadPath),
            SettingType(idx, string(R.string.setting_privacy_policy_manage_downloaded_file))
        )

        items.set(data)

        return data
    }

    fun removeHistorySettingType(): ArrayList<SettingType> {
        if (mLog.isDebugEnabled) {
            mLog.debug("REMOVE HISTORY TYPE")
        }

        var idx = 0

        var removeHistory: Boolean
        var cache: Boolean
        var autoComplete: Boolean
        var multiBrowserTab: Boolean
        var recentKeyword: Boolean

        app.prefs().run {
            removeHistory   = getBoolean(PREF_REMOVE_HISTORY, true)
            cache           = getBoolean(PREF_REMOVE_CACHE, true)
            autoComplete    = getBoolean(PREF_AUTOCOMPLETE_DATA, true)
            multiBrowserTab = getBoolean(PREF_MULTI_BRS_TAB, false)
            recentKeyword   = getBoolean(PREF_RECENT_KEYWORD, false)
        }

        val data = arrayListOf(
            SettingType(idx++, string(R.string.setting_remove_history_history),
                checked = MutableLiveData(removeHistory)),
            SettingType(idx++, string(R.string.setting_remove_history_cache),
                checked = MutableLiveData(cache)),
            SettingType(idx++, string(R.string.setting_remove_history_auto_complete),
                checked = MutableLiveData(autoComplete)),
            SettingType(idx++, string(R.string.setting_remove_history_multi_browser_tab),
                checked = MutableLiveData(multiBrowserTab)),
            SettingType(idx, string(R.string.setting_remove_history_recent_keyword),
                checked = MutableLiveData(recentKeyword))
        )

        items.set(data)

        return data
    }

    fun alarmSettingType(): ArrayList<SettingType> {
        if (mLog.isDebugEnabled) {
            mLog.debug("ALARM SETTING TYPE")
        }

        var idx = 0

        var breakingNews: Boolean
        var todaysPick: Boolean
        var weather: Boolean
        var lotto: Boolean
        var eventAlarm: Boolean
        var fortune: Boolean
        var mail: Boolean
        var cafe: Boolean

        app.prefs().run {
            breakingNews    = getBoolean(PREF_BREAKING_NEWS, true)
            todaysPick      = getBoolean(PREF_TODAYS_PICK, true)
            weather         = getBoolean(PREF_WEATHER, true)
            lotto           = getBoolean(PREF_LOTTO, false)
            eventAlarm      = getBoolean(PREF_EVENT_ALARM, true)
            fortune         = getBoolean(PREF_FORTUNE, false)

            mail = getBoolean(PREF_MAIL, true)
            cafe = getBoolean(PREF_CAFE, true)
        }

        val data = arrayListOf(
            SettingType(idx++, string(R.string.setting_breaking_news),
                string(R.string.setting_breaking_news_summary),
                checked = MutableLiveData(breakingNews)),
            SettingType(idx++, string(R.string.setting_today_pick),
                string(R.string.setting_today_pick_summary),
                checked = MutableLiveData(todaysPick)),
            SettingType(idx++, string(R.string.setting_weather),
                string(R.string.setting_weather_summary),
                checked = MutableLiveData(weather)),
            SettingType(idx++, string(R.string.setting_lotte),
                string(R.string.setting_lotte_summary),
                checked = MutableLiveData(lotto)),
            SettingType(idx++, string(R.string.setting_event_alarm),
                string(R.string.setting_event_alarm_summary),
                checked = MutableLiveData(eventAlarm)),
            SettingType(idx++, string(R.string.setting_fortune),
                string(R.string.setting_fortune_summary),
                checked = MutableLiveData(fortune)),

            // ----

            SettingType(idx++, string(R.string.setting_mail),
                checked = MutableLiveData(mail)),
            SettingType(idx++, string(R.string.setting_mail_all_alarm),
                type = SettingType.T_DEPTH),
            SettingType(idx++, string(R.string.setting_cafe),
                checked = MutableLiveData(cafe)),
            SettingType(idx, string(R.string.setting_cafe_new_article_alarm),
                type = SettingType.T_DEPTH)
        )

        items.set(data)

        return data
    }

    fun alarmPreferenceSettingType(): ArrayList<SettingType> {
        if (mLog.isDebugEnabled) {
            mLog.debug("ALARM PREFERENCE SETTING TYPE")
        }

        var idx = 0

        var alarmMode: String
        var enableScreen: Boolean
        var etiquette: Boolean

        app.prefs().run {
            alarmMode    = getString(PREF_ALARM_MODE, string(R.string.setting_alarmmode_sound))!!
            enableScreen = getBoolean(PREF_ENABLE_SCREEN, true)
            etiquette    = getBoolean(PREF_ETIQUETTE, false)
        }

        val data = arrayListOf(
            SettingType(idx++, string(R.string.setting_breaking_news),
                option = MutableLiveData(alarmMode)),

            SettingType(idx++, string(R.string.setting_alarm_env_set_enable_screen),
                string(R.string.setting_alarm_env_set_enable_screen_summary),
                checked = MutableLiveData(enableScreen)),
            SettingType(idx, string(R.string.setting_alarm_env_set_etiquette),
                string(R.string.setting_alarm_env_set_etiquette_summary),
                checked = MutableLiveData(etiquette))
        )

        items.set(data)

        return data
    }

    fun researchSettingType(): ArrayList<SettingType> {
        if (mLog.isDebugEnabled) {
            mLog.debug("RESEARCH SETTING TYPE")
        }

        var idx = 0
        var fixedAddressBar: Boolean

        app.prefs().run {
            fixedAddressBar = getBoolean(PREF_SEARCH_FIXED_ADDR, false)
        }

        val data = arrayListOf(
            SettingType(idx, string(R.string.setting_research_fixed_address_bar),
                string(R.string.setting_research_fixed_address_bar_summary),
                checked = MutableLiveData(fixedAddressBar),
                type = SettingType.T_SWITCH)
        )

        items.set(data)

        return data
    }

    fun userHistorySettingType(): ArrayList<SettingType>  {
        if (mLog.isDebugEnabled) {
            mLog.debug("USER HISTORY SETTING TYPE")
        }

        var idx = 0
        var userHistory: Boolean
        var cache: Boolean
        var autoComplete: Boolean
        var multiTab: Boolean
        var recentKeyword: Boolean

        app.prefs().run {
            userHistory   = getBoolean(PREF_REMOVE_HISTORY, true)
            cache         = getBoolean(PREF_REMOVE_CACHE, true)
            autoComplete  = getBoolean(PREF_AUTOCOMPLETE_DATA, true)
            multiTab      = getBoolean(PREF_MULTI_BRS_TAB, false)
            recentKeyword = getBoolean(PREF_RECENT_KEYWORD, false)
        }

        val data = arrayListOf(
            SettingType(idx++, string(R.string.setting_remove_history_history),
                checked = MutableLiveData(userHistory),
                type = SettingType.T_CHECK),
            SettingType(idx++, string(R.string.setting_remove_history_cache),
                checked = MutableLiveData(cache),
                type = SettingType.T_CHECK),
            SettingType(idx++, string(R.string.setting_remove_history_auto_complete),
                checked = MutableLiveData(autoComplete),
                type = SettingType.T_CHECK),
            SettingType(idx++, string(R.string.setting_remove_history_multi_browser_tab),
                checked = MutableLiveData(multiTab),
                type = SettingType.T_CHECK),
            SettingType(idx, string(R.string.setting_remove_history_recent_keyword),
                checked = MutableLiveData(recentKeyword),
                type = SettingType.T_CHECK)
        )

        items.set(data)

        return data
    }

    fun notifyItemChanged(pos: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("INVALIDATE $pos")
        }

        adapter.get()?.notifyItemChanged(pos)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            CMD_SETTING_EVENT -> if (data is SettingType) {
                if (data.enabled != null && data.enabled.value == false) {
                    return
                }

                super.command(cmd, data)
            }

            else -> super.command(cmd, data)
        }
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(SettingViewModel::class.java)

        const val CMD_SETTING_EVENT       = "setting-event"
        const val CMD_REMOVE_USER_HISTORY = "remove-user-history"

        const val PREF_NICK_NAME         = "pref-set-nick-name"

        const val PREF_BLOCKING_POPUP    = "pref-set-blocking-popup"
        const val PREF_CHARACTER_SET     = "pref-set-character-set"
        const val PREF_MEDIA_AUTOPLAY    = "pref-set-media-autoplay"
        const val PREF_USE_LOCATION_INFO = "pref-set-use-location-info"
        const val PREF_SIMPLE_SEARCH     = "pref-set-simple-search"
        const val PREF_DAUMAPP_INFO      = "pref-set-daumapp-info"

        const val PREF_SAVE_KEYWORD      = "pref-set-save-keyword"
        const val PREF_SAVE_HISTORY      = "pref-set-save-history"
        const val PREF_DOWNLOADPATH      = "pref-set-download-path"


        const val PREF_REMOVE_HISTORY    = "pref-set-remove-history"
        const val PREF_REMOVE_CACHE      = "pref-set-remove-cache"
        const val PREF_AUTOCOMPLETE_DATA = "pref-set-autocomplete-data"
        const val PREF_MULTI_BRS_TAB     = "pref-set-multi-brs-tab"
        const val PREF_RECENT_KEYWORD    = "pref-set-recent-keyword"

        const val PREF_BREAKING_NEWS     = "pref-set-breaking-news"
        const val PREF_TODAYS_PICK       = "pref-set-todays-pick"
        const val PREF_WEATHER           = "pref-set-weather"
        const val PREF_LOTTO             = "pref-set-lotto"
        const val PREF_EVENT_ALARM       = "pref-set-event-alarm"
        const val PREF_FORTUNE           = "pref-set-fortune"
        const val PREF_MAIL              = "pref-set-mail"
        const val PREF_CAFE              = "pref-set-cafe"

        const val PREF_ALARM_MODE        = "pref-set-alarm-mode"
        const val PREF_ENABLE_SCREEN     = "pref-set-enable-screen"
        const val PREF_ETIQUETTE         = "pref-set-etiquette"

        const val PREF_SEARCH_FIXED_ADDR = "pref-set-search_fixed_addr"
    }
}