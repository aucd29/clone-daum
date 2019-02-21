package com.example.clone_daum.model.remote

import com.example.clone_daum.model.IWeatherRecyclerData
import com.example.common.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 15. <p/>
 */

data class Weather(
    val largeIcon: Int,
    val weatherText: String,
    val temperature: String,
    val temperatureDescription: String,
    val humidity: String,
    val wind: String
)

data class WeatherDetail(
    val type: Int,
    val icon: String,
    val description: String,
    val value : String
) : IWeatherRecyclerData {
    override fun compare(item: IRecyclerDiff): Boolean {
        val newItem = item as WeatherDetail
        return type == newItem.type && icon == newItem.icon && value == newItem.value
    }

    override fun type() = type
}