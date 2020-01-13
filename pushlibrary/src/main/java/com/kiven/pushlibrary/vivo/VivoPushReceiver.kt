package com.kiven.pushlibrary.vivo

import android.content.Context
import com.kiven.kutils.logHelper.KLog
import com.vivo.push.model.UPSNotificationMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver

/**
 * Created by oukobayashi on 2020-01-06.
 */
class VivoPushReceiver:OpenClientPushMessageReceiver() {
    override fun onNotificationMessageClicked(p0: Context?, p1: UPSNotificationMessage?) {

    }

    override fun onReceiveRegId(p0: Context?, p1: String?) {
        KLog.i("vivo 注册获得id: $p1")
    }
}