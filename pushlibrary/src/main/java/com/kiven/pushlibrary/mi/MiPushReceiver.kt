package com.kiven.pushlibrary.mi

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAppTool
import com.kiven.pushlibrary.Web
import com.xiaomi.mipush.sdk.*


/**
 * Created by oukobayashi on 2019-12-27.
 */
class MiPushReceiver : PushMessageReceiver() {
    // 用来接收服务器发送的透传消息
    override fun onReceivePassThroughMessage(p0: Context?, p1: MiPushMessage?) {
        KLog.i("小米推送 onReceivePassThroughMessage 收到消息")
        KLog.printClassField(p1, null, true)
    }

    // 用来接收服务器发来的通知栏消息（用户点击通知栏时触发）
    override fun onNotificationMessageClicked(context: Context?, p1: MiPushMessage?) {
        KLog.i("小米推送 onNotificationMessageClicked 点击消息")
        KLog.printClassField(p1, null, true)

        context ?: return

        val arguUrl = p1?.extra?.get("arguUrl")

        if (!arguUrl.isNullOrBlank()) {
            try {
                val uri = Uri.parse(arguUrl)

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri

                val list = context.packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)

                //URL Scheme是否有效
                if (list.isNotEmpty()) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    return
                }
            }catch (e:Throwable) {
                KLog.e(e)
            }

            KAppTool.startApp(context, context.packageName)
        }
    }

    // 用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
    override fun onNotificationMessageArrived(p0: Context?, p1: MiPushMessage?) {
        KLog.i("小米推送 onNotificationMessageArrived 收到消息")
        KLog.printClassField(p1, null, true)
    }

    // 用来接收客户端向服务器发送命令消息后返回的响应
    override fun onCommandResult(p0: Context?, p1: MiPushCommandMessage?) {
        KLog.i("小米推送 onCommandResult 收到消息")
        KLog.printClassField(p1, null, true)
    }

    // 用来接受客户端向服务器发送注册命令消息后返回的响应
    override fun onReceiveRegisterResult(p0: Context?, p1: MiPushCommandMessage?) {
        KLog.i("小米推送 onReceiveRegisterResult 注册完成")
        p0 ?: return
        p1 ?: return
        KLog.printClassField(p1, null, true)

        val command = p1.command
        val arguments = p1.commandArguments
        val cmdArg1 = if (arguments != null && arguments.size > 0) arguments[0] else null
        val cmdArg2 = if (arguments != null && arguments.size > 1) arguments[1] else null
        if (MiPushClient.COMMAND_REGISTER == command) {
            if (p1.resultCode.toInt() == ErrorCode.SUCCESS) {
                KLog.i("小米推送注册成功 regId = $cmdArg1 , cmdArg2 = $cmdArg2")


                Web.register(p0, cmdArg1!!, 5)//设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
            }
        }
    }
}