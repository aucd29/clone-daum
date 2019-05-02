package com.example.clone_daum.model.remote

import brigitte.IRecyclerDiff
import brigitte.IRecyclerItem

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
    val weatherType: Int,
    val icon: String,
    val description: String,
    val value : String
) : IRecyclerDiff, IRecyclerItem {
    override var type: Int = weatherType

    override fun compare(item: IRecyclerDiff): Boolean {
        val newItem = item as WeatherDetail
        return weatherType == newItem.weatherType && icon == newItem.icon && value == newItem.value
    }
}