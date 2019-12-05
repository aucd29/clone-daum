package com.example.clone_daum.ui.main.weather

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
import com.example.clone_daum.common.Config
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.Weather
import com.example.clone_daum.model.remote.WeatherDetail
import brigitte.RecyclerViewModel
import com.patloew.rxlocation.RxLocation
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 14. <p/>
 *
 * 디자인 변경으로 사용되지 않음 [aucd29][2019. 2. 26.]
 */

@SuppressLint("MissingPermission")
class WeatherViewModel @Inject constructor(
    app: Application,
    val config: Config,
    val preConfig: PreloadConfig,
    val disposable: CompositeDisposable,
    private val rxLocation: RxLocation
) : RecyclerViewModel<WeatherDetail>(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(WeatherViewModel::class.java)

        const val CMD_MORE_DETAIL                   = "more-detail"
        const val CMD_REFRESH_LOCATION              = "refresh-location"
        const val CMD_CHECK_PERMISSION_AND_LOAD_GPS = "check-permission-and-load-gps"
    }

    val currentLocation  = ObservableField(config.DEFAULT_LOCATION)
    val thoroughfare     = ObservableField(config.DEFAULT_LOCATION)
    val weather          = ObservableField<Weather>()
    val gridCount        = ObservableInt(3)

    val visibleProgress         = ObservableBoolean(false)
    val visibleProgressFromMain = ObservableInt(View.VISIBLE)

    init {
        refreshCurrentLocation()
        refreshWeather()
    }

    fun refreshCurrentLocation() {
        if (config.HAS_PERMISSION_GPS) {
            disposable.add(rxLocation.location().lastLocation()
                .flatMap { location -> rxLocation.geocoding().fromLocation(location) }
                .subscribe ({
                    if (mLog.isInfoEnabled) {
                        mLog.info("CURRENT LOCATION : ${it.locality} ${it.subLocality} ${it.thoroughfare}")
                    }

                    val current = "${it.subLocality} ${it.thoroughfare}"

                    thoroughfare.set(it.thoroughfare)
                    currentLocation.set(current)
                    command(CMD_REFRESH_LOCATION, current)
                }, ::errorLog))
        }
    }

    fun refreshWeather() {
        visibleProgressFromMain.set(View.GONE)

        // 날씨 가져오는 OPEN API 들이 죄다 유료라서 이건 일단 생략
        weather.set(Weather(R.drawable.ic_android_black
            , "눈"
            , "1℃"
            , "어제보다 낮음, 체감온도 낮음"
            , "습도 60%"
            , "풍속 3.5m/s"))
    }

    fun initRecycler() {
        preConfig.weatherData {
            initAdapter(R.layout.weather_dust_item, R.layout.weather_other_item)
            items.set(it)
        }
    }
}