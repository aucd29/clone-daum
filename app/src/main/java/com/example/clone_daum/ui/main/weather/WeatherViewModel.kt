package com.example.clone_daum.ui.main.weather

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.clone_daum.R
import com.example.clone_daum.di.module.Config
import com.example.clone_daum.di.module.PreloadConfig
import com.example.clone_daum.model.remote.Weather
import com.example.clone_daum.model.remote.WeatherDetail
import com.example.common.ICommandEventAware
import com.example.common.IFinishFragmentAware
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import com.patloew.rxlocation.RxLocation
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 14. <p/>
 */

@SuppressLint("MissingPermission")
class WeatherViewModel @Inject constructor(application: Application
    , val config: Config
    , val preConfig: PreloadConfig
    , val disposable: CompositeDisposable
    , val rxLocation: RxLocation
) : RecyclerViewModel<WeatherDetail>(application), ICommandEventAware
    , IFinishFragmentAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(WeatherViewModel::class.java)

        const val CMD_MORE_DETAIL                   = "more-detail"
        const val CMD_REFRESH_LOCATION              = "refresh-location"
        const val CMD_CHECK_PERMISSION_AND_LOAD_GPS = "check-permission-and-load-gps"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()
    override val finishEvent  = SingleLiveEvent<Void>()

    val currentLocation  = ObservableField<String>(config.DEFAULT_LOCATION)
    val thoroughfare     = ObservableField<String>(config.DEFAULT_LOCATION)
    val weather          = ObservableField<Weather>()
    val gridCount        = ObservableInt(3)
    val visibleProgress  = ObservableBoolean(false)

    init {
        refreshCurrentLocation()
        refreshWeather()
    }

    fun refreshCurrentLocation() {
        if (config.HAS_PERMISSION_GPS) {
            disposable.add(rxLocation.location().lastLocation()
                .flatMap { location -> rxLocation.geocoding().fromLocation(location) }
                .subscribe {
                    if (mLog.isInfoEnabled) {
                        mLog.info("CURRENT LOCATION : ${it.locality} ${it.subLocality} ${it.thoroughfare}")
                    }

                    val current = "${it.subLocality} ${it.thoroughfare}"

                    thoroughfare.set(it.thoroughfare)
                    currentLocation.set(current)
                    commandEvent(CMD_REFRESH_LOCATION, current)
                })
        }
    }

    fun refreshWeather() {
        // 날씨 가져오는 OPEN API 들이 죄다 유료라서 이건 일단 생략
        weather.set(Weather(R.drawable.ic_android_black_100dp
            , "눈"
            , "1℃"
            , "어제보다 낮음, 체감온도 낮음"
            , "습도 60%"
            , "풍속 3.5m/s"))
    }

    fun initRecycler() {
        initAdapter(arrayOf("weather_dust_item", "weather_other_item"))
        items.set(preConfig.weatherDetailList)
    }
}