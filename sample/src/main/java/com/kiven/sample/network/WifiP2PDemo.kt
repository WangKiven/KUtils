package com.kiven.sample.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Looper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.util.showDialog
import com.kiven.sample.util.showDialogClose
import com.kiven.sample.util.showListDialog
import com.kiven.sample.util.showSnack

class WifiP2PDemo : BaseFlexActivityHelper() {
    var receiver: BroadcastReceiver? = null
    var mChannel: WifiP2pManager.Channel? = null

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        activity.apply {
            val manager: WifiP2pManager  = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager? ?: return@apply showDialogClose("WifiP2pManager 获取失败")
            mChannel = manager.initialize(this, Looper.getMainLooper(), null) ?: return@apply showDialogClose("WifiP2pManager 初始失败")

            addBtn("启用监听") {
                if (receiver != null) return@addBtn showSnack("已启用")

                receiver = WiFiDirectBroadcastReceiver(manager, mChannel!!, this)
                val intentFilter = IntentFilter().apply {
                    addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
                }
                registerReceiver(receiver, intentFilter)
            }

            addBtn("寻找设备") {
                if (receiver == null) return@addBtn showSnack("需先启用监听")

                manager.discoverPeers(mChannel!!, object :WifiP2pManager.ActionListener{
                    override fun onSuccess() {
                        showSnack("开启发现设备功能成功")
                    }

                    override fun onFailure(reason: Int) {
                        showSnack("开启发现设备功能失败：$reason")
                    }
                })
            }

            addBtn("连接设备") {
                manager.requestPeers(mChannel!!) { peers: WifiP2pDeviceList? ->
                    // Handle peers list
                    if (peers == null) return@requestPeers activity.showSnack("peers = null")
                    val deviceList = peers.deviceList.toList()
                    if (deviceList.isEmpty()) return@requestPeers activity.showSnack("没有可连接设备")

                    activity.showListDialog(deviceList.map { it.deviceName }) { index, _ ->
                        val config = WifiP2pConfig()
                        config.deviceAddress = deviceList[index].deviceAddress
                        manager.connect(mChannel!!, null, object :WifiP2pManager.ActionListener{
                            override fun onSuccess() {
                                activity.showSnack("连接设备成功")

                            }

                            override fun onFailure(reason: Int) {
                                activity.showSnack("连接设备失败：$reason")
                            }
                        })
                    }
                }
            }

            addBtn("启用消息监听") {
                showDialog("")
            }
            addBtn("发送消息") {
                showDialog("")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        receiver?.apply { mActivity.unregisterReceiver(this) }
        mChannel?.close()
    }

    private class WiFiDirectBroadcastReceiver(
            val manager: WifiP2pManager,
            val channel: WifiP2pManager.Channel,
            val activity: KHelperActivity) :BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
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
                }
            }
        }
    }
}