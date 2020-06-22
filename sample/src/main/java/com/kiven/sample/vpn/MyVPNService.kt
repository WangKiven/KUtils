package com.kiven.sample.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kiven.sample.R
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramSocket
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel


/**
 * Created by oukobayashi on 2020/6/19.
 *
 * https://developer.android.google.cn/guide/topics/connectivity/vpn
 * https://github.com/pencil-box/NetKnight
 * https://github.com/mightofcode/android-vpnservice
 */
class MyVPNService: VpnService() {
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        open()
        return Service.START_STICKY
    }

    private fun open() {
        /*val tunnel = DatagramChannel.open()
        if (!protect(tunnel.socket())) {
            KLog.e("不能保持socket")
            return
        }

        tunnel.connect(InetSocketAddress("148.70.34.25", 9000))
        tunnel.configureBlocking(false)*/




//        val ds = DatagramSocket(8097)
//        val protect = protect(ds)
        val tunnel = DatagramChannel.open()
        val protect = protect(tunnel.socket())

        if (!protect) return

        val vpnFileDescriptor = Builder().apply {
            addAddress("192.168.0.105", 16)
//                addRoute("0.0.0.0", 0)
//                addDnsServer("192.168.0.23")
        }.establish() ?: return

        val vpnInput = FileInputStream(vpnFileDescriptor.fileDescriptor)
        val vpnOutput = FileOutputStream(vpnFileDescriptor.fileDescriptor)




        // Allocate the buffer for a single packet.

        // Allocate the buffer for a single packet.
        val packet = ByteBuffer.allocate(Short.MAX_VALUE.toInt())

        // Timeouts:
        //   - when data has not been sent in a while, send empty keepalive messages.
        //   - when data has not been received in a while, assume the connection is broken.

        // Timeouts:
        //   - when data has not been sent in a while, send empty keepalive messages.
        //   - when data has not been received in a while, assume the connection is broken.
        var lastSendTime = System.currentTimeMillis()
        var lastReceiveTime = System.currentTimeMillis()

        // We keep forwarding packets till something goes wrong.

        // We keep forwarding packets till something goes wrong.
        while (true) {
            // Assume that we did not make any progress in this iteration.
            var idle = true

            // Read the outgoing packet from the input stream.
            var length: Int = vpnInput.read(packet.array())
            if (length > 0) {
                // Write the outgoing packet to the tunnel.
                packet.limit(length)
                tunnel.write(packet)
                packet.clear()

                // There might be more outgoing packets.
                idle = false
                lastReceiveTime = System.currentTimeMillis()
            }

            // Read the incoming packet from the tunnel.
            length = tunnel.read(packet)
            if (length > 0) {
                // Ignore control messages, which start with zero.
                if (packet[0].toInt() != 0) {
                    // Write the incoming packet to the output stream.
                    vpnOutput.write(packet.array(), 0, length)
                }
                packet.clear()

                // There might be more incoming packets.
                idle = false
                lastSendTime = System.currentTimeMillis()
            }

            // If we are idle or waiting for the network, sleep for a
            // fraction of time to avoid busy looping.
            if (idle) {
                Thread.sleep(100 * 1000)
                val timeNow = System.currentTimeMillis()
                if (lastSendTime + 15 * 1000 <= timeNow) {
                    // We are receiving for a long time but not sending.
                    // Send empty control messages.
                    packet.put(0.toByte()).limit(1)
                    for (i in 0..2) {
                        packet.position(0)
                        tunnel.write(packet)
                    }
                    packet.clear()
                    lastSendTime = timeNow
                } else check(lastReceiveTime + 20 * 1000 > timeNow) {
                    // We are sending for a long time but not receiving.
                    "Timed out"
                }
            }
        }
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