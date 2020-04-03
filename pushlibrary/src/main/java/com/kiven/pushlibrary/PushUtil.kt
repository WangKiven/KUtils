package com.kiven.pushlibrary

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object PushUtil {


    /**
     * 这个不要动，华为推送，服务器用了这个channelId
     */
//    val channelId = "sxbChannelId"
    fun getChannelId(context: Context): String {
        when (Web.platform) {//设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
            2 -> {
                // 远程通道channelId, 可本地提前创建并命名。创建的通道都是默认开启的。
                return "com.huawei.android.pushagent.low"
            }
            3 -> {
                // TODO vivo 远程推送的channel,本地也可以使用。远程推送时创建的channel默认是开启的, 本地创建的默认是关闭的。
                //  所以等待远程推送并创建好channel后再，使用webSocket推送
                if (Build.VERSION.SDK_INT >= 26) return "vivo_push_channel"
            }
            4 -> {
            }
        }
        return "sxbChannelId"
    }

    private val channelName = "推送通知"

    fun initChannel(context: Context) {
        val notiManager = NotificationManagerCompat.from(context)
        val channelId = getChannelId(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = notiManager.notificationChannels
            if (channels.filter { it.id == channelId }.isNullOrEmpty()) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.enableLights(true)
                channel.lightColor = Color.GREEN
                /*if (p < groups.size) {
                    channel.group = groups[p].id
                }*/
//                        channel.setSound()
                channel.enableVibration(true) // 震动
                //channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC // 锁屏可见
                channel.setShowBadge(true)
                channel.importance =
                    NotificationManager.IMPORTANCE_HIGH//重要性，不重要的通知可能没声音，也可能被收纳起来导致用户看不到
                channel.description = "推送通知" // 描述

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        channel.setAllowBubbles(true) // 小红点显示。华为崩了，所以放try里面
                    } catch (e: Throwable) {
                    }
                }
                channel.setBypassDnd(true) // 免打扰模式下，允许响铃或震动

                notiManager.createNotificationChannel(channel)
            }
        }
    }

    fun notification(context: Context, title: String, subTitle: String, argument: String) {
        // 先初始通道
        initChannel(context)

        val notiManager = NotificationManagerCompat.from(context)

//        val ii = Intent(context, Class.forName("com.kiven.sample.noti.ClickNotiActivity"))
        val ii = Intent(context, Class.forName("com.kiven.pushlibrary.ClickNotiActivity"))
        ii.putExtra("argu", argument)
        val pendingIntent =
            PendingIntent.getActivity(context, 110, ii, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder = NotificationCompat.Builder(context, getChannelId(context))
            .setSmallIcon(context.applicationContext.applicationInfo.icon)
//                .setTicker(subTitle) // 通知响起时，状态栏显示的内容
            .setContentTitle(title)
            .setContentText(subTitle)
//                .setNumber(12)
//                .setContentInfo(subTitle)//在通知的右侧设置大文本。
            .setAutoCancel(true)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)//图标类型
            .setContentIntent(pendingIntent)

        // Notification#DEFAULT_SOUND Notification#DEFAULT_VIBRATE
        // Notification#DEFAULT_LIGHTS Notification#DEFAULT_ALL
        mBuilder.setDefaults(Notification.DEFAULT_ALL)

        val cid = System.currentTimeMillis() % (1000 * 60 * 60 * 24 * 365)
        notiManager.notify(cid.toInt(), mBuilder.build())
    }


}