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
    fun notification(context: Context, title: String, subTitle: String, argument: String) {
        val notiManager = NotificationManagerCompat.from(context)
        val channelId = "sxbChannelId"
        val channelName = "重要通知"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = notiManager.notificationChannels
            if (channels.filter { it.id == channelId }.isNullOrEmpty()) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                channel.enableLights(true)
                channel.lightColor = Color.GREEN
                /*if (p < groups.size) {
                    channel.group = groups[p].id
                }*/
//                        channel.setSound()
                channel.enableVibration(true) // 震动
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC // 锁屏可见
                channel.setShowBadge(true)
                channel.description = "显示重要通知消息" // 描述
                try {
                    channel.setAllowBubbles(true) // 小红点显示。华为崩了，所以放try里面
                } catch (e: NoSuchMethodError) {
                }
                channel.setBypassDnd(true) // 免打扰模式下，允许响铃或震动

                notiManager.createNotificationChannel(channel)
            }
        }

        val ii = Intent(context, Class.forName("com.kiven.sample.noti.ClickNotiActivity"))
        ii.putExtra("argu", argument)
        val pendingIntent = PendingIntent.getActivity(context, 110, ii, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(context.applicationContext.applicationInfo.icon)
                .setTicker(subTitle) // 通知响起时，状态栏显示的内容
                .setContentTitle(title)
                .setContentText(subTitle)
                .setNumber(12)
                .setContentInfo(subTitle)//在通知的右侧设置大文本。
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)//图标类型
                .setContentIntent(pendingIntent)


        val cid = System.currentTimeMillis() % (1000 * 60 * 60 * 24 * 365)
        notiManager.notify(cid.toInt(), mBuilder.build())
    }
}