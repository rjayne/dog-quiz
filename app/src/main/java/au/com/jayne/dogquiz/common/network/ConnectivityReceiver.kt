package au.com.jayne.dogquiz.common.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import timber.log.Timber

class ConnectivityReceiver(val connectionStateMonitor: ConnectionStateMonitor): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.i("Network Status Changed")
        connectionStateMonitor.onNetworkStatusChanged()
    }

    fun registerConnectivityReceiver(context: Context) {
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

        context.registerReceiver(this, filter)
    }

}