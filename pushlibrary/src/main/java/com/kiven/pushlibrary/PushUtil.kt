package com.kiven.pushlibrary

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object PushUtil {


    val channelId = "sxbChannelId"
    val channelName = "重要通知"

    fun notification(context: Context, title: String, subTitle: String, argument: String) {
        val notiManager = NotificationManagerCompat.from(context)


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
                //channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC // 锁屏可见
                channel.setShowBadge(true)
                channel.importance = NotificationManager.IMPORTANCE_DEFAULT//重要性，不重要的通知可能没声音，也可能被收纳起来导致用户看不到
                channel.description = "显示重要通知消息" // 描述

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

//        val ii = Intent(context, Class.forName("com.kiven.sample.noti.ClickNotiActivity"))
        val ii = Intent(context, Class.forName("com.kiven.pushlibrary.ClickNotiActivity"))
        ii.putExtra("argu", argument)
        val pendingIntent = PendingIntent.getActivity(context, 110, ii, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder = NotificationCompat.Builder(context, channelId)
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