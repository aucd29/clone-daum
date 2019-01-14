package com.example.clone_daum.ui.main.weather

import android.os.Bundle
import com.example.clone_daum.R
import com.example.clone_daum.databinding.WeatherFragmentBinding
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 14. <p/>
 */

class WeatherFragment
    : BaseDaggerBottomSheetDialogFragment<WeatherFragmentBinding, WeatherViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(WeatherFragment::class.java)

        // 전국 날씨
        private const val MORE_DETAIL_URL = """https://m.search.daum.net/search?w=tot&q=%EC%A0%84%EA%B5%AD%EB%82%A0%EC%94%A8&DA=G29&f=androidapp&DN=ADDA&nil_app=daumapp&enc_all=utf8"""
    }

    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var viewController: ViewController

    // 라운드 다이얼로그로 수정
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(context!!, R.style.round_bottom_sheet_dialog)

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
    }

    override fun onCommandEvent(cmd: String, data: Any?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("COMMAND EVENT : $cmd")
        }

        when (cmd) {
            WeatherViewModel.CMD_MORE_DETAIL -> {
                viewController.browserFragment(MORE_DETAIL_URL)
                dismiss()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): WeatherFragment
    }
}