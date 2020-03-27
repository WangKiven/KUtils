package com.kiven.pushlibrary

import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import com.kiven.kutils.tools.KUtil

/**
 * Created by oukobayashi on 2019-12-11.
 * https://blog.csdn.net/xiayiye5/article/details/83688396
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class PushNotificationListenerService : NotificationListenerService() {

    companion object {
        val linsteners = mutableListOf<OnNotificationListener>()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val appPackageName = packageName

        sbn?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (packageName == appPackageName && notification.channelId == PushUtil.channelId) {
                    KUtil.putSharedPreferencesLongValue(PushUtil.channelId, System.currentTimeMillis())
                }
            }
        }

        linsteners.forEach { it.onReceive(sbn) }
    }

    interface OnNotificationListener {
        fun onReceive(sbn: StatusBarNotification?)
    }
}