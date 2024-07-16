package com.kiven.sample.network

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.MacAddress
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Looper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.util.*

class WifiP2PDemo : BaseFlexActivityHelper() {
    private var receiver: BroadcastReceiver? = null
    private var mChannel: WifiP2pManager.Channel? = null

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        activity.apply {
            KGranting.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (it) {
                    initView()
                } else {
                    showDialogClose("没有权限")
                }
            }
        }
    }

    private fun initView() {
        mActivity?.apply {
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

                manager.discoverPeers(mChannel!!, object : WifiP2pManager.ActionListener {
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
                    if (peers == null) return@requestPeers showSnack("peers = null")
                    val deviceList = peers.deviceList.toList()
                    if (deviceList.isEmpty()) return@requestPeers showSnack("没有可连接设备")

                    showListDialog(deviceList.map { it.deviceName }) { index, _ ->
//                        val config = WifiP2pConfig()
//                        config.deviceAddress = deviceList[index].deviceAddress
                        val config = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            WifiP2pConfig.Builder().apply {
                                // 1 必须设置，不能为空和空字符串
                                // 2 必须以DIRECT-xy.开头（xy是任意两个大小写字母或数字）
                                // 3 转化为字节数组后的长度不能大于32
                                setNetworkName("DIRECT-xy.海无盐")
                                setPassphrase("13141314") // 必须设置，长度为8~63
                                // 默认为 "02:00:00:00:00:00"
                                setDeviceAddress(MacAddress.fromString(deviceList[index].deviceAddress))
                                // 设置组的的频率根据频段选取，默认从所有频段选取，与 setGroupOperatingFrequency 互斥
                                setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_AUTO)
                                // 设置组的的频率（单位MHz），与 setGroupOperatingBand 互斥
//                                setGroupOperatingFrequency(1)
//                                enablePersistentMode(true) // 默认false，不保存组配置
                            }.build()
                        } else {
                            WifiP2pConfig().apply {
                                deviceAddress = deviceList[index].deviceAddress
                            }
                        }
                        manager.connect(mChannel!!, config, object : WifiP2pManager.ActionListener {
                            override fun onSuccess() {
                                showSnack("请求连接操作成功（不代表连接成功）")
                            }

                            override fun onFailure(reason: Int) {
                                showSnack("请求连接操作失败：$reason")
                            }
                        })
                    }
                }
            }

            val tool = WifiP2PTool()
            addBtn("启用消息监听") {
                tool.sendMessage()
            }
            addBtn("发送消息") {
//                if (receiver == null) return@addBtn showSnack("需先启用监听")
//                manager.requestConnectionInfo(mChannel) {
//
//                }
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
            KLog.i("$intent")

            intent?.extras?.keySet()?.forEach {
                KLog.i("$it ${intent.extras?.get(it)}")
            }


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
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    activity.showSnack("连接新设备结束，可获取连接信息了")
                }
            }
        }
    }
}