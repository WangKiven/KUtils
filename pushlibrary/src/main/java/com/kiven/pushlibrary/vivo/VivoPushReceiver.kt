package com.kiven.pushlibrary.vivo

import android.content.Context
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.Web
import com.vivo.push.model.UPSNotificationMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver

/**
 * Created by oukobayashi on 2020-01-06.
 */
class VivoPushReceiver : OpenClientPushMessageReceiver() {
    override fun onNotificationMessageClicked(p0: Context?, p1: UPSNotificationMessage?) {

    }

    override fun onReceiveRegId(p0: Context?, p1: String?) {
        KLog.i("vivo 注册获得id: $p1")

        if (p1 != null)
            Web.register(p1, 3)//设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
    }
}