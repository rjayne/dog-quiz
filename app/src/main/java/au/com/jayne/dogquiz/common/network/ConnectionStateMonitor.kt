package au.com.jayne.dogquiz.common.network

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import au.com.jayne.dogquiz.domain.model.Event
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Keep
@Singleton
class ConnectionStateMonitor @Inject constructor(val applicationContext: Context): ConnectivityManager.NetworkCallback() {

    private val connectivityManager: ConnectivityManager
    private val networkRequest: NetworkRequest

    private var connectivityReceiver: ConnectivityReceiver? = null

    private val _internetConnected = MutableLiveData<Event<Boolean>>()
    val internetConnected: LiveData<Event<Boolean>>
        get() = _internetConnected

    init{
        networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        _internetConnected.value = Event<Boolean>(hasInternetConnection())

        initAccordingToSDKVersion()
    }

    fun initAccordingToSDKVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            connectivityManager.registerNetworkCallback(networkRequest, this, Handler(Looper.getMainLooper()))
        } else {
            connectivityReceiver = ConnectivityReceiver(this).apply {
                registerConnectivityReceiver(applicationContext)
            }
        }
    }

    fun hasInternetConnection(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            return hasInternetConnectionFromMarshmallowUp(applicationContext)
        }
    }

    @TargetApi(23)
    private fun hasInternetConnectionFromMarshmallowUp(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onLost(network: Network?) {
        onNetworkStatusChanged()
    }

    override fun onAvailable(network: Network?) {
        onNetworkStatusChanged()
    }

    internal fun onNetworkStatusChanged() {
        val currentInternetConnection = hasInternetConnection()
        if(_internetConnected.value?.peek() != currentInternetConnection) {
            Timber.i("onNetworkStatusChanged - Now: $currentInternetConnection")
            _internetConnected.value = Event<Boolean>(currentInternetConnection)
        }
    }

}