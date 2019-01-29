package com.example.clone_daum.ui.main.weather

import android.Manifest
import android.os.Bundle
import android.view.View
import com.example.clone_daum.R
import com.example.clone_daum.databinding.WeatherFragmentBinding
import com.example.clone_daum.di.module.Config
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerBottomSheetDialogFragment
import com.example.common.runtimepermission.PermissionParams
import com.example.common.runtimepermission.runtimePermissions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.ContributesAndroidInjector
import io.reactivex.Observable
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
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

    init {
        // WeatherViewModel 를 MainFragment 와 공유
        mViewModelScope = SCOPE_ACTIVITY
    }

    @Inject lateinit var config: Config
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var viewController: ViewController

    // 라운드 다이얼로그로 수정
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(context!!, R.style.round_bottom_sheet_dialog)

    // content 높이만큼 bottom sheet dialog 가 transition (height) 되도록 함
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wrapContentHeight()
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() = mViewModel.run {
        initRecycler()
    }

    override fun onCommandEvent(cmd: String, data: Any?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("COMMAND EVENT : $cmd")
        }

        WeatherViewModel.apply {
            when (cmd) {
                CMD_MORE_DETAIL -> {
                    viewController.browserFragment(MORE_DETAIL_URL)

                    dismiss()
                }

                CMD_REFRESH_LOCATION -> mViewModel.apply {
                    visibleProgress.set(false)

                }

                CMD_CHECK_PERMISSION_AND_LOAD_GPS -> mViewModel.apply {
                    visibleProgress.set(true)

                    // 뷰를 위해서 타이머를 주긴했는데
                    // 원래는 안줘야 됨 ...
                    disposable.add(Observable.interval(2, TimeUnit.SECONDS)
                        .take(1)
                        .subscribe {
                            runtimePermissions(PermissionParams(activity()
                                , arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                                , listener = { req, res ->
                                    config.HAS_PERMISSION_GPS = res

                                    if (res) {
                                        refreshCurrentLocation()
                                    }
                                }))
                        })
                }
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