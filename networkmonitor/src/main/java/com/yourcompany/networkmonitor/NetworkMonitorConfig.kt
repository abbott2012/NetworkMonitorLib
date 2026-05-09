package com.lff.networkmonitor

data class NetworkMonitorConfig(
    val checkCaptivePortal: Boolean = true,
    @Deprecated("Wi-Fi signal strength warning is no longer reported.")
    val checkWifiSignal: Boolean = false,
    val checkCellularWarning: Boolean = true
)
