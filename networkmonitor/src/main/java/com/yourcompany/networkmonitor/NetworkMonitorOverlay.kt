package com.lff.networkmonitor

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Simple one-line API to show a network status overlay on top-right of the screen.
 */
object NetworkMonitorOverlay {

    private var monitor: NetworkMonitor? = null
    private var container: LinearLayout? = null
    private var messageView: TextView? = null
    private var actionView: TextView? = null

    /**
     * Start monitoring network state and show overlay in the given activity.
     */
    @JvmStatic
    fun start(activity: Activity) {
        if (monitor != null && container != null) {
            return
        }

        val context = activity
        val overlayContainer = LinearLayout(context)
        overlayContainer.orientation = LinearLayout.VERTICAL
        overlayContainer.setPadding(
            dpToPx(activity, 12f),
            dpToPx(activity, 8f),
            dpToPx(activity, 12f),
            dpToPx(activity, 8f)
        )
        overlayContainer.setBackgroundColor(Color.parseColor("#B3000000"))
        overlayContainer.visibility = View.GONE

        val overlayMessageView = TextView(context)
        overlayMessageView.setTextColor(Color.WHITE)
        overlayMessageView.textSize = 14f

        val overlayActionView = TextView(context)
        overlayActionView.setTextColor(Color.parseColor("#4A90E2"))
        overlayActionView.textSize = 14f
        overlayActionView.paint.isUnderlineText = true
        overlayActionView.visibility = View.GONE

        overlayContainer.addView(overlayMessageView)
        overlayContainer.addView(overlayActionView)

        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.TOP or Gravity.END
        layoutParams.topMargin = dpToPx(activity, 16f)
        layoutParams.marginEnd = dpToPx(activity, 16f)

        activity.addContentView(overlayContainer, layoutParams)

        container = overlayContainer
        messageView = overlayMessageView
        actionView = overlayActionView

        monitor = NetworkMonitor(context).also { networkMonitor ->
            networkMonitor.start(object : NetworkMonitor.Listener {
                override fun onNetworkStateChanged(state: NetworkState) {
                    activity.runOnUiThread {
                        updateOverlayWithState(activity, state)
                    }
                }
            })
        }
    }

    /**
     * Stop monitoring and hide overlay.
     */
    @JvmStatic
    fun stop() {
        monitor?.stop()
        monitor = null

        actionView?.setOnClickListener(null)
        container?.visibility = View.GONE

        container = null
        messageView = null
        actionView = null
    }

    private fun updateOverlayWithState(activity: Activity, state: NetworkState) {
        val overlayContainer = container ?: return
        val overlayMessageView = messageView ?: return
        val overlayActionView = actionView ?: return

        when (state) {
            NetworkState.Connected -> {
                overlayContainer.visibility = View.GONE
                overlayActionView.visibility = View.GONE
                overlayActionView.setOnClickListener(null)
            }

            NetworkState.NoNetwork -> {
                overlayContainer.visibility = View.VISIBLE
                overlayMessageView.text = "当前无可用网络连接"
                overlayActionView.visibility = View.VISIBLE
                overlayActionView.text = "去网络设置"
                overlayActionView.setOnClickListener {
                    try {
                        activity.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    } catch (_: Exception) {
                    }
                }
            }

            NetworkState.AirplaneMode -> {
                overlayContainer.visibility = View.VISIBLE
                overlayMessageView.text = "当前为飞行模式，网络已关闭"
                overlayActionView.visibility = View.VISIBLE
                overlayActionView.text = "去系统设置"
                overlayActionView.setOnClickListener {
                    try {
                        activity.startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
                    } catch (_: Exception) {
                    }
                }
            }

            NetworkState.CaptivePortal -> {
                overlayContainer.visibility = View.VISIBLE
                overlayMessageView.text = "当前网络需要网页登录或认证"
                overlayActionView.visibility = View.VISIBLE
                overlayActionView.text = "打开网络设置"
                overlayActionView.setOnClickListener {
                    try {
                        activity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    } catch (_: Exception) {
                    }
                }
            }

            is NetworkState.WifiWarning -> {
                overlayContainer.visibility = View.VISIBLE
                overlayMessageView.text = "Wi‑Fi 信号较弱"
                overlayActionView.visibility = View.VISIBLE
                overlayActionView.text = "检查Wi‑Fi"
                overlayActionView.setOnClickListener {
                    try {
                        activity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    } catch (_: Exception) {
                    }
                }
            }

            NetworkState.Cellular -> {
                overlayContainer.visibility = View.VISIBLE
                overlayMessageView.text = "当前使用移动网络，可能产生流量费用"
                overlayActionView.visibility = View.VISIBLE
                overlayActionView.text = "切换到Wi‑Fi"
                overlayActionView.setOnClickListener {
                    try {
                        activity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }

    private fun dpToPx(activity: Activity, dp: Float): Int {
        val density = activity.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}
