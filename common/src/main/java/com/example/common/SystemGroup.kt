@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Looper

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 22. <p/>
 */

@SuppressLint("MissingPermission")
inline fun Context.isNetworkConntected(): Boolean {
    systemService<ConnectivityManager>()?.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (network in allNetworks) {
                if (getNetworkInfo(network).state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        } else {
            for (network in allNetworkInfo) {
                if (network == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
    }

    return false
}

fun validateMainThread() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
        throw IllegalStateException("Must be called from the main thread.")
    }
}