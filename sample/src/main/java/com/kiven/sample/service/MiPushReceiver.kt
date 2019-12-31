package com.kiven.sample.service

import android.content.Context
import com.kiven.kutils.logHelper.KLog
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.MiPushMessage
import com.xiaomi.mipush.sdk.PushMessageReceiver

/**
 * Created by oukobayashi on 2019-12-27.
 */
class MiPushReceiver : PushMessageReceiver() {
    // 用来接收服务器发送的透传消息
    override fun onReceivePassThroughMessage(p0: Context?, p1: MiPushMessage?) {
        KLog.i("onReceivePassThroughMessage 收到消息")
        KLog.printClassField(p1, null, true)
    }

    // 用来接收服务器发来的通知栏消息（用户点击通知栏时触发）
    override fun onNotificationMessageClicked(p0: Context?, p1: MiPushMessage?) {
        KLog.i("onNotificationMessageClicked 点击消息")
        KLog.printClassField(p1, null, true)
    }

    // 用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
    override fun onNotificationMessageArrived(p0: Context?, p1: MiPushMessage?) {
        KLog.i("onNotificationMessageArrived 收到消息")
        KLog.printClassField(p1, null, true)
    }

    // 用来接收客户端向服务器发送命令消息后返回的响应
    override fun onCommandResult(p0: Context?, p1: MiPushCommandMessage?) {
        KLog.i("onCommandResult 收到消息")
        KLog.printClassField(p1, null, true)
    }

    // 用来接受客户端向服务器发送注册命令消息后返回的响应
    override fun onReceiveRegisterResult(p0: Context?, p1: MiPushCommandMessage?) {
        KLog.i("onReceiveRegisterResult 收到消息")
        KLog.printClassField(p1, null, true)
    }
}