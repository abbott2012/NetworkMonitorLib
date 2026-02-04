package com.lff.networkmonitor

data class NetworkMonitorConfig(
    val checkCaptivePortal: Boolean = true,
    val checkWifiSignal: Boolean = true,
    val checkCellularWarning: Boolean = true
)
