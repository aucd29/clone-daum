package com.example.clone_daum.ui.main.weather

import android.annotation.SuppressLint
import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.clone_daum.di.module.Config
import com.example.common.ICommandEventAware
import com.example.common.IFinishFragmentAware
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
    , val disposable: CompositeDisposable
    , val rxLocation: RxLocation
) : AndroidViewModel(application), ICommandEventAware
    , IFinishFragmentAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(WeatherViewModel::class.java)

        const val CMD_MORE_DETAIL       = "more-detail"
        const val CMD_REFRESH_LOCATION  = "refresh-location"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any?>>()
    override val finishEvent  = SingleLiveEvent<Void>()

    val currentLocation = ObservableField<String>()

    init {
        refreshCurrentLocation()
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

                    currentLocation.set(current)
                    commandEvent(CMD_REFRESH_LOCATION, current)
                })
        }
    }
}