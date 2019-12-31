package com.kiven.sample.service

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.kiven.kutils.logHelper.KLog

/**
 * Created by oukobayashi on 2019-12-31.
 */
class HuaWeiPushService:HmsMessageService() {
    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        KLog.i("onMessageReceived ${p0?.data}")
    }

    override fun onMessageSent(p0: String?) {
        super.onMessageSent(p0)
        KLog.i("onMessageSent $p0")
    }
}