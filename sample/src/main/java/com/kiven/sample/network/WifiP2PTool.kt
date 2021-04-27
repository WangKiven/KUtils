package com.kiven.sample.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.MacAddress
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kiven.kutils.tools.KAppHelper
import com.kiven.kutils.tools.KUtil
import com.kiven.kutils.widget.KNormalItemView
import com.kiven.sample.R
import com.sxb.kutils_ktx.util.RxBus
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

        val devices = mutableListOf<DeviceState>()
        val devices1 = mutableListOf<DeviceState>()
        val devices2 = mutableListOf<DeviceState>()

        val dialog = BottomSheetDialog(activity)
        dialog.setContentView(R.layout.layout_bottom_sheet_p2p_ui)
        dialog.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = P2PDeviceAdapter(devices) { deviceState ->
                if (!deviceState.isConnected) {
                    val config = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        WifiP2pConfig.Builder().apply {
                            // 1 必须设置，不能为空和空字符串
                            // 2 必须以DIRECT-xy.开头（xy是任意两个大小写字母或数字）
                            // 3 转化为字节数组后的长度不能大于32
                            setNetworkName("DIRECT-xy.海无盐")
                            setPassphrase("13141314") // 必须设置，长度为8~63
                            // 默认为 "02:00:00:00:00:00"
                            setDeviceAddress(MacAddress.fromString(deviceState.wifiP2pDevice!!.deviceAddress))
                            // 设置组的的频率根据频段选取，默认从所有频段选取，与 setGroupOperatingFrequency 互斥
                            setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_AUTO)
                            // 设置组的的频率（单位MHz），与 setGroupOperatingBand 互斥
//                                setGroupOperatingFrequency(1)
//                                enablePersistentMode(true) // 默认false，不保存组配置
                        }.build()
                    } else {
                        WifiP2pConfig().apply {
                            deviceAddress = deviceState.wifiP2pDevice!!.deviceAddress
                        }
                    }
                    wifiP2pManager?.connect(wifiP2pChannel!!, config, object :WifiP2pManager.ActionListener{
                        override fun onSuccess() {
                            activity.showSnack("请求连接操作成功（不代表连接成功）")

                        }

                        override fun onFailure(reason: Int) {
                            activity.showSnack("请求连接操作失败：$reason")
                        }
                    })
                }
            }
        }


        val requestDeviceAction:()->Unit = {
            wifiP2pManager?.requestPeers(wifiP2pChannel!!) { peers: WifiP2pDeviceList? ->
                devices1.clear()
                if (peers != null && peers.deviceList.isNotEmpty()) {
                    devices1.addAll(peers.deviceList.map { DeviceState(false, it, null) })
                }


                devices.clear()
                devices.addAll(devices1 + devices2)

                dialog.recyclerView.adapter?.notifyDataSetChanged()
            }

            wifiP2pManager?.requestConnectionInfo(wifiP2pChannel!!) {
                devices2.clear()
                if (it != null) {
                    devices2.add(DeviceState(true, null, it))
                }

                devices.clear()
                devices.addAll(devices1 + devices2)
                dialog.recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        requestDeviceAction()

        RxBus.register<Intent>(dialog, wiFiP2PBroadcastReceiverTag) { intent ->
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
                    // 应用可使用 requestPeers()。
                    activity.showSnack("搜索设备结束，可连接设备已刷新")
                    requestDeviceAction()
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    // 应用可使用 requestConnectionInfo()、requestNetworkInfo() 或 requestGroupInfo() 来检索当前连接信息。
                    activity.showSnack("连接新设备结束，可获取连接信息了")
                    requestDeviceAction()
                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    // 应用可使用 requestDeviceInfo() 来检索当前连接信息。
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
            RxBus.unregister(dialog)
        }
        dialog.show()
    }

    private class P2PDeviceAdapter(private val devices: List<DeviceState>, private val onClick:(DeviceState)->Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                if (device.isConnected) {
                    device.wifiP2pInfo?.apply {
                        setTextName(groupOwnerAddress?.hostAddress ?: "null")
                        setTextInfo(">拥有者：$isGroupOwner，连接成功：$groupFormed")
                        setOnClickListener { onClick(device) }
                    }
                } else {
                    device.wifiP2pDevice?.apply {
                        setTextName("$deviceName($deviceAddress)")
                        setTextInfo("拥有者：$isGroupOwner, status: $status")
                        setOnClickListener { onClick(device) }
                    }
                }
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
        }
    }

    private class DeviceState(val isConnected:Boolean, val wifiP2pDevice:WifiP2pDevice?, val wifiP2pInfo: WifiP2pInfo?)
}