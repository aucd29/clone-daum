package brigitte.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.lifecycle.LiveData
import brigitte.systemService

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-06 <p/>
 *
 * https://stackoverflow.com/questions/36421930/connectivitymanager-connectivity-action-deprecated
 */

class NetworkStatusLiveData(val context: Context): LiveData<Boolean>() {
    val filter = IntentFilter(ACTION)
    val manager = context.systemService<ConnectivityManager>()
    val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

        }
    }
    val callback: ConnectivityManager.NetworkCallback? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network?) =
                this@NetworkStatusLiveData.postValue(true)
            override fun onLost(network: Network?) =
                this@NetworkStatusLiveData.postValue(false)
        }
    } else {
        null
    }

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }

    @SuppressLint("MissingPermission")
    private fun updateNetworkStatus() {
        val info = context.systemService<ConnectivityManager>()?.activeNetworkInfo
        postValue(info?.isConnectedOrConnecting == true)
    }

    companion object {
        const val ACTION = "brigitte.receiver.NETWORK_STATUS"
    }
}