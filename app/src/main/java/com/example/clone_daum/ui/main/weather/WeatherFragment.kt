package com.example.clone_daum.ui.main.weather

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.R
import com.example.clone_daum.common.Config
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.databinding.WeatherFragmentBinding
import com.example.clone_daum.ui.Navigator
import brigitte.BaseDaggerBottomSheetDialogFragment
import brigitte.RecyclerAdapter
import brigitte.SCOPE_ACTIVITY
import brigitte.di.dagger.scope.FragmentScope
import brigitte.runtimepermission.PermissionParams
import brigitte.runtimepermission.runtimePermissions
import com.example.clone_daum.model.remote.WeatherDetail
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.Binds
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 14. <p/>
 *
 * 디자인 변경으로 사용되지 않음 [aucd29][2019. 2. 26.]
 */

class WeatherFragment
    : BaseDaggerBottomSheetDialogFragment<WeatherFragmentBinding, WeatherViewModel>() {
    override val layoutId = R.layout.weather_fragment
    companion object {
        private val logger = LoggerFactory.getLogger(WeatherFragment::class.java)

        // 전국 날씨
        private const val MORE_DETAIL_URL = """https://m.search.daum.net/search?w=tot&q=%EC%A0%84%EA%B5%AD%EB%82%A0%EC%94%A8&DA=G29&f=androidapp&DN=ADDA&nil_app=daumapp&enc_all=utf8"""
    }

    init {
        // WeatherViewModel 를 MainFragment 와 공유
        viewModelScope = SCOPE_ACTIVITY
    }

    @Inject lateinit var config: Config
    @Inject lateinit var preConfig: PreloadConfig
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var adapter: RecyclerAdapter<WeatherDetail>

    // 라운드 다이얼로그로 수정
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(requireContext(), R.style.round_bottom_sheet_dialog)

    // content 높이만큼 bottom sheet dialog 가 transition (height) 되도록 함
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wrapContentHeight()
    }

    override fun initViewBinding() {
        adapter.viewModel = viewModel
        binding.weatherRecycler.adapter = adapter
    }

    override fun initViewModelEvents() {
        viewModel.init()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        if (logger.isDebugEnabled) {
            logger.debug("COMMAND EVENT : $cmd")
        }

        WeatherViewModel.apply {
            when (cmd) {
                CMD_MORE_DETAIL -> {
                    navigator.browserFragment(MORE_DETAIL_URL)

                    dismiss()
                }

                CMD_REFRESH_LOCATION -> viewModel.apply {
                    visibleProgress.set(false)
                }

                CMD_CHECK_PERMISSION_AND_LOAD_GPS -> viewModel.apply {
                    visibleProgress.set(true)

                    // 뷰를 위해서 타이머를 주긴했는데
                    // 원래는 안줘야 됨 ...
                    disposable.add(Observable.interval(1, TimeUnit.SECONDS)
                        .take(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            runtimePermissions(PermissionParams(requireActivity()
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
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [WeatherFragmentModule::class])
        abstract fun contributeWeatherFragmentInjector(): WeatherFragment
    }

    @dagger.Module
    abstract class WeatherFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: WeatherFragment): SavedStateRegistryOwner

        @dagger.Module
        companion object {
            @JvmStatic
            @Provides
            fun provideWeatherDetailAdapter(): RecyclerAdapter<WeatherDetail> =
                RecyclerAdapter(arrayOf(
                    R.layout.weather_dust_item, R.layout.weather_other_item))
        }
    }
}