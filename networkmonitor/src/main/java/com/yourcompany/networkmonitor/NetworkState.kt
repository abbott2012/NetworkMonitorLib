package com.lff.networkmonitor

sealed class NetworkState {
    object Connected : NetworkState()
    object NoNetwork : NetworkState()
    object AirplaneMode : NetworkState()
    object CaptivePortal : NetworkState()
    data class WifiWarning(val signalLevel: Int) : NetworkState()
    object Cellular : NetworkState()
}
