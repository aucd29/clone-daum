package com.example.clone_daum.di.module

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import dagger.Module
import dagger.Provides

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 15. <p/>
 *
 * RxLocation
 *  - https://github.com/patloew/RxLocation
 */

@Module
class RxLocationModule {
    companion object {
        private const val LOCATION_PRIORITY = LocationRequest.PRIORITY_LOW_POWER
    }

    @Provides
    fun provideRxLocation(context: Context): RxLocation {
        return RxLocation(context)
    }

    @Provides
    fun provideLocationRequest() =
        LocationRequest.create().apply { priority = LOCATION_PRIORITY }
}