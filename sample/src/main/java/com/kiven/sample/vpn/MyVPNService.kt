package com.kiven.sample.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.R
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.channels.DatagramChannel


/**
 * Created by oukobayashi on 2020/6/19.
 *
 * https://developer.android.google.cn/guide/topics/connectivity/vpn
 */
class MyVPNService: VpnService() {
    companion object {
        private val TAG: String = com.kiven.sample.vpn.MyVPNService::class.java.getSimpleName()

        val ACTION_CONNECT = "com.example.android.toyvpn.START"
        val ACTION_DISCONNECT = "com.example.android.toyvpn.STOP"
    }


    private val channelId = "vpnChannel"
    private val channelName = "KUtils VPN通知服务"

    override fun onCreate() {
        super.onCreate()

        // 前台保活处理
        createChannel()

        val mBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("KUtils VPN已开启")
        startForeground(System.currentTimeMillis().toInt(), mBuilder.build())

        Thread {
            open()
        }.start()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    private fun open() {
        val tunnel = DatagramChannel.open()
        if (!protect(tunnel.socket())) {
            KLog.e("不能保持socket")
            return
        }

        tunnel.connect(InetSocketAddress("148.70.34.25", 9000))
        tunnel.configureBlocking(false)




        /*val ds = DatagramSocket(8097)

        val protect = protect(ds)
        if (protect) {
            val builder = Builder().apply {
                addAddress("192.168.0.10", 24)
                addRoute("0.0.0.0", 0)
                addDnsServer("192.168.0.23")
            }.establish()
        }*/
    }



    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notiManager = NotificationManagerCompat.from(this)

            notiManager.notificationChannels.forEach {
                if (it.id == channelId) {
                    return
                }
            }

            val channel = NotificationChannel(channelId, "$channelName", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(false)
//            channel.lightColor = Color.GREEN
//                        channel.setSound()
            channel.setSound(null, null)
            channel.enableVibration(false) // 震动
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC // 锁屏可见
            channel.setShowBadge(true)
            channel.description = "这是VPN分类" // 描述
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    channel.setAllowBubbles(true)// 小红点显示。华为崩了，所以放try里面
                }
            }catch (e:NoSuchMethodError){}
            channel.setBypassDnd(true) // 免打扰模式下，允许响铃或震动

            notiManager.createNotificationChannel(channel)
        }
    }
}