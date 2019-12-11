package brigitte.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-06 <p/>
 */

class NetworkStatusReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

    }

    companion object {
        const val ACTION = "brigitte.receiver.NETWORK_STATUS"
    }
}