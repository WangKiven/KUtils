package com.kiven.sample.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kiven.kutils.tools.KAppHelper
import com.kiven.kutils.tools.KUtil
import com.kiven.kutils.widget.KNormalItemView
import com.kiven.sample.R
import com.kiven.sample.util.RxBus
import com.kiven.sample.util.showSnack
import kotlinx.android.synthetic.main.layout_bottom_sheet_p2p_ui.*

class WifiP2PTool {
    fun sendMessage() {

        if (initWifiP2pManager())
            showP2PUI()
    }
    fun registerMessageListener() {}
    fun unRegisterMessageListener() {}

    companion object {
        const val wiFiP2PBroadcastReceiverTag = "WifiP2PTool.wiFiP2PBroadcastReceiverTag"


        private var wiFiP2PBroadcastReceiver: WiFiP2PBroadcastReceiver? = null
        fun registerReceiver() {
            if (wiFiP2PBroadcastReceiver == null) {
                wiFiP2PBroadcastReceiver = WiFiP2PBroadcastReceiver()
                val intentFilter = IntentFilter().apply {
                    addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
                }
                KUtil.getApp().registerReceiver(wiFiP2PBroadcastReceiver, intentFilter)
            }
        }

        fun unRegisterReceiver() {
            if (wiFiP2PBroadcastReceiver != null) {
                KUtil.getApp().unregisterReceiver(wiFiP2PBroadcastReceiver)
                wiFiP2PBroadcastReceiver = null
            }
        }
    }

    private var wifiP2pManager: WifiP2pManager? = null
    private var wifiP2pChannel: WifiP2pManager.Channel? = null
    private fun initWifiP2pManager():Boolean {
        if (wifiP2pChannel == null) {
            val app = KUtil.getApp()
            wifiP2pManager = app.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
                    ?: return false
            wifiP2pChannel = wifiP2pManager?.initialize(app, Looper.getMainLooper(), null)
                    ?: return false


            registerReceiver()
        }

        return true

    }

    private fun showP2PUI() {
        val activity = KAppHelper.getInstance().topActivity ?: return

        val devices = mutableListOf<WifiP2pDevice>()

        val dialog = BottomSheetDialog(activity)
        dialog.setContentView(R.layout.layout_bottom_sheet_p2p_ui)
        dialog.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = P2PDeviceAdapter(devices) {

            }
        }


        val requestDeviceAction:()->Unit = {
            wifiP2pManager?.requestPeers(wifiP2pChannel!!) { peers: WifiP2pDeviceList? ->
                devices.clear()
                if (peers != null && peers.deviceList.isNotEmpty()) {
                    devices.addAll(peers.deviceList)
                }

                dialog.recyclerView.adapter?.notifyDataSetChanged()
            }

            wifiP2pManager?.requestConnectionInfo(wifiP2pChannel!!) {

            }
        }
        requestDeviceAction()

        RxBus.register<Intent>(this, wiFiP2PBroadcastReceiverTag) { intent ->
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                        WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                            // Wifi P2P is enabled
                            activity.showSnack("Wifi P2P 可用")
                        }
                        else -> {
                            // Wi-Fi P2P is not enabled
                            activity.showSnack("Wi-Fi P2P 不可用")
                        }
                    }
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    activity.showSnack("搜索设备结束，可连接设备已刷新")
                    requestDeviceAction()
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    activity.showSnack("连接新设备结束，可获取连接信息了")
                    requestDeviceAction()
                }
            }
        }


        wifiP2pManager?.discoverPeers(wifiP2pChannel!!, object :WifiP2pManager.ActionListener{
            override fun onSuccess() {
                activity.showSnack("开启发现设备功能成功")
            }

            override fun onFailure(reason: Int) {
                activity.showSnack("开启发现设备功能失败：$reason")
            }
        })

        dialog.setOnDismissListener {

        }
        dialog.show()
    }

    private class P2PDeviceAdapter(private val devices: List<WifiP2pDevice>, private val onClick:(WifiP2pDevice)->Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_p2p_device, parent, false)) {}
            return object : RecyclerView.ViewHolder(KNormalItemView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(-1, -2)
            }) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as KNormalItemView).apply {
                val device = devices[position]
                setTextName(device.deviceName)
                setTextInfo(device.isGroupOwner.toString())
                setOnClickListener { onClick(device) }
            }
        }

        override fun getItemCount(): Int {
            return devices.size
        }
    }

    private class WiFiP2PBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent != null) {
                RxBus.post(wiFiP2PBroadcastReceiverTag, intent)
            }
            /*val activity = KAppHelper.getInstance().topActivity

            when (intent?.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                        WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                            // Wifi P2P is enabled
                            activity?.showSnack("Wifi P2P 可用")
                        }
                        else -> {
                            // Wi-Fi P2P is not enabled
                            activity?.showSnack("Wi-Fi P2P 不可用")
                        }
                    }
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    activity?.showSnack("搜索设备结束，可连接设备已刷新")
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    activity?.showSnack("连接新设备结束，可获取连接信息了")
                }
            }*/
        }
    }
}