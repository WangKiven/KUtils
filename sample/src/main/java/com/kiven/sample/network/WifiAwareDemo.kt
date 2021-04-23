package com.kiven.sample.network

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.aware.*
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KGranting
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.util.showDialogClose
import com.kiven.sample.util.showSnack

/**
 * https://developer.android.google.cn/guide/topics/connectivity/wifi-aware
 * 所需权限：
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 */
@RequiresApi(Build.VERSION_CODES.O)
class WifiAwareDemo:BaseFlexActivityHelper() {

    private var wifiAwareChangeLisener: BroadcastReceiver? = null

    override fun onDestroy() {
        wifiAwareChangeLisener?.apply { mActivity.unregisterReceiver(this) }
        publishDiscoverySession?.close()
        subscribeDiscoverySession?.close()
        super.onDestroy()
    }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        activity.apply {

            KGranting.requestPermissions(this, 899,
                    arrayOf(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION),
                    arrayOf("ACCESS_WIFI_STATE", "CHANGE_WIFI_STATE", "ACCESS_FINE_LOCATION")) {
                if (it) {
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)) {
                        showDialogClose("设备没 WiFi感知 功能")
                        return@requestPermissions
                    }
                    initView()
                } else {
                    showDialogClose("没有权限")
                }
            }
        }
    }

    private fun initView() {
        mActivity?.apply {

            val wifiAware = getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager

            addBtn("监听WiFi感知") {
                if (wifiAwareChangeLisener == null) {
                    val filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)
                    wifiAwareChangeLisener = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            if (wifiAware.isAvailable) {
                                showSnack("功能可用")
                            } else {
                                showSnack("功能不可用")
                            }
                        }
                    }
                    mActivity.registerReceiver(wifiAwareChangeLisener, filter)
                }
                mActivity.showSnack("已开启 WiFi感知 监听功能")
            }

            var wifiAwareSesson: WifiAwareSession? = null
            wifiAware.attach(object : AttachCallback() {
                override fun onAttached(session: WifiAwareSession?) {
                    super.onAttached(session)
                    session?.apply {
                        wifiAwareSesson = this
                    } ?: showSnack("WifiAwareSession = null")
                }

                override fun onAttachFailed() {
                    super.onAttachFailed()
                    showSnack("onAttachFailed 可能系统拒绝服务吧")
                }
            }, null)

            addBtn("订阅服务") {
                wifiAwareSesson?.apply { subscribe(this) } ?: showSnack("wifiAwareSesson = null")
            }

            addBtn("发布服务") {
                wifiAwareSesson?.apply { publish(this) } ?: showSnack("wifiAwareSesson = null")
            }
        }
    }

    private var publishDiscoverySession:PublishDiscoverySession? = null
    private fun publish(wifiAware: WifiAwareSession) {
        val config = PublishConfig.Builder()
                .setServiceName("Aware_File_Share_Service_Name")
                /*.apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        // 启用范围设置，在订阅端设置范围
                        setRangingEnabled(true)
                    }
                }*/
                .build()

        wifiAware.publish(config, object : DiscoverySessionCallback() {
            override fun onPublishStarted(session: PublishDiscoverySession) {
                super.onPublishStarted(session)
                publishDiscoverySession = session
            }

            override fun onMessageReceived(peerHandle: PeerHandle?, message: ByteArray?) {
                super.onMessageReceived(peerHandle, message)
                if (message != null) mActivity.showSnack("收到消息：${String(message)}")
            }
        }, null)
    }

    private var subscribeDiscoverySession: SubscribeDiscoverySession? = null
    private fun subscribe(wifiAware: WifiAwareSession) {
        val config = SubscribeConfig.Builder()
                .setServiceName("Aware_File_Share_Service_Name")
                /*.apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        // 范围设置，需要在发布端设置是否启用
                        setMinDistanceMm(1000 * 1)
                        setMaxDistanceMm(1000 * 15)
                    }
                }*/
                .build()

        wifiAware.subscribe(config, object :DiscoverySessionCallback() {
            var count = 0
            override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                super.onSubscribeStarted(session)
                subscribeDiscoverySession = session
            }

            override fun onServiceDiscovered(peerHandle: PeerHandle?, serviceSpecificInfo: ByteArray?, matchFilter: MutableList<ByteArray>?) {
                super.onServiceDiscovered(peerHandle, serviceSpecificInfo, matchFilter)
                peerHandle?.apply {
                    subscribeDiscoverySession?.sendMessage(this, count++, "你好坏啊".toByteArray())
                }
            }

            override fun onMessageSendFailed(messageId: Int) {
                super.onMessageSendFailed(messageId)
                mActivity.showSnack("消息发送失败：$messageId")
            }

            override fun onMessageSendSucceeded(messageId: Int) {
                super.onMessageSendSucceeded(messageId)
                mActivity.showSnack("消息发送成功：$messageId")
            }
        }, null)
    }
}