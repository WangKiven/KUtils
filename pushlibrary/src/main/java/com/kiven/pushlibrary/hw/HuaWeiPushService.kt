package com.kiven.pushlibrary.hw

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.kiven.kutils.logHelper.KLog

/**
 * Created by oukobayashi on 2019-12-31.
 */
class HuaWeiPushService:HmsMessageService() {
    /**
     * 目前测试结果：
     * 仅接收透传消息，并且在App已经启动的情况下才能收到
     */
    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        KLog.i("华为 onMessageReceived ${p0?.data}")
    }

    override fun onMessageSent(p0: String?) {
        super.onMessageSent(p0)
        KLog.i("华为 onMessageSent $p0")
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        HuaWeiPushHelper.token = p0
        KLog.i("华为 HmsMessageService接收的华为token: $p0")
    }
}