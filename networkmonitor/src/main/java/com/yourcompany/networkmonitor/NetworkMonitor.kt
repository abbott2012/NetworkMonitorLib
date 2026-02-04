package com.lff.networkmonitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.provider.Settings
import android.telephony.TelephonyManager

class NetworkMonitor(
    context: Context,
    private val config: NetworkMonitorConfig = NetworkMonitorConfig()
) {

    interface Listener {
        fun onNetworkStateChanged(state: NetworkState)
    }

    private val appContext = context.applicationContext
    private val cm =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var callback: ConnectivityManager.NetworkCallback? = null

    fun start(listener: Listener) {
        if (callback != null) return

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                notifyState(listener)
            }

            override fun onLost(network: Network) {
                notifyState(listener)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                notifyState(listener)
            }
        }

        try {
            cm.registerNetworkCallback(request, callback as ConnectivityManager.NetworkCallback)
        } catch (_: Exception) {
        }

        notifyState(listener)
    }

    fun stop() {
        val cb = callback ?: return
        try {
            cm.unregisterNetworkCallback(cb)
        } catch (_: Exception) {
        } finally {
            callback = null
        }
    }

    private fun notifyState(listener: Listener) {
        val state = resolveState()
        listener.onNetworkStateChanged(state)
    }

    private fun resolveState(): NetworkState {
        if (isAirplaneModeOn(appContext)) {
            return NetworkState.AirplaneMode
        }

        val activeNetwork = try {
            cm.activeNetwork
        } catch (_: Exception) {
            null
        } ?: return NetworkState.NoNetwork

        val capabilities = try {
            cm.getNetworkCapabilities(activeNetwork)
        } catch (_: Exception) {
            null
        } ?: return NetworkState.NoNetwork

        val hasInternet =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasValidated =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val isCaptivePortal =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
        val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

        if (!hasInternet || !hasValidated) {
            return NetworkState.NoNetwork
        }

        if (config.checkCaptivePortal && isCaptivePortal) {
            return NetworkState.CaptivePortal
        }

        if (isWifi && config.checkWifiSignal) {
            val level = getWifiSignalLevel()
            if (level in 0..1) {
                return NetworkState.WifiWarning(level)
            }
        }

        if (isCellular && config.checkCellularWarning) {
            return NetworkState.Cellular
        }

        return NetworkState.Connected
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON
            ) == 1
        } catch (_: Exception) {
            false
        }
    }

    private fun getWifiSignalLevel(): Int {
        val tm =
            appContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                ?: return -1
        val type = tm.dataNetworkType
        return if (type == TelephonyManager.NETWORK_TYPE_UNKNOWN) -1 else 2
    }
}
