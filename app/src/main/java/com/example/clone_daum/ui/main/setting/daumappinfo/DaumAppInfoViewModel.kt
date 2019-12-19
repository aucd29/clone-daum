package com.example.clone_daum.ui.main.setting.daumappinfo

import android.app.Application
import androidx.databinding.ObservableField
import brigitte.RecyclerViewModel
import brigitte.widget.viewpager.OffsetDividerItemDecoration
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.R
import com.example.clone_daum.model.local.SettingType
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-17 <p/>
 */

class DaumAppInfoViewModel @Inject constructor(
    app: Application
) : RecyclerViewModel<SettingType>(app) {

    val itemDecoration = ObservableField(OffsetDividerItemDecoration(app,
            R.drawable.shape_divider_gray,  0, 0))

    val currentVersion = ObservableField<String>()
    val lastestVersion = ObservableField<String>()

    init {
        var i = 0
        val data = arrayListOf(
            SettingType(i++, string(R.string.setting_daumapp_move_mobile_web),
                type = SettingType.T_DAUM),
            SettingType(i++, string(R.string.setting_daumapp_other_app),
                type = SettingType.T_DAUM),
            SettingType(i++, string(R.string.setting_daumapp_open_source_license),
                type = SettingType.T_DAUM),
            SettingType(i++, string(R.string.setting_daumapp_terms_of_use),
                type = SettingType.T_DAUM),
            SettingType(i++, string(R.string.setting_daumapp_privacy_policy),
                type = SettingType.T_DAUM),
            SettingType(i, string(R.string.setting_daumapp_contact_us),
                type = SettingType.T_DAUM)
        )

        val current = String.format(string(R.string.setting_daumapp_current_version), BuildConfig.VERSION_NAME)
        val lastest = String.format(string(R.string.setting_daumapp_lastest_version), BuildConfig.VERSION_NAME)

        currentVersion.set(current)
        lastestVersion.set(lastest)

        initAdapter(R.layout.setting_category_item,
            R.layout.setting_normal_item,
            R.layout.setting_color_item,
            R.layout.setting_switch_item,
            R.layout.setting_check_item,
            R.layout.setting_depth_item,
            R.layout.daum_app_info_item)

        items.set(data)
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(DaumAppInfoViewModel::class.java)

        const val CMD_DAUMAPP_INFO_EVENT = "daumappinfo-event"
        const val CMD_UPDATE             = "daumappinfo-update"
    }
}