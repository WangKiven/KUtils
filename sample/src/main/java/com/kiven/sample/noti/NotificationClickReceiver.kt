package com.kiven.sample.noti

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by wangk on 2019/5/14.
 */
class NotificationClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startActivity(Intent(context, ClickNotiActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}