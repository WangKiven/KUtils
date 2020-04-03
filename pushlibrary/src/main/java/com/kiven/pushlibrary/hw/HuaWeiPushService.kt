package com.kiven.pushlibrary.hw

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.Web

/**
 * Created by oukobayashi on 2019-12-31.
 */
class HuaWeiPushService : HmsMessageService() {
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
        if (p0 != null)
            Web.register(this, p0, 2)//设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
        KLog.i("华为 HmsMessageService接收的华为token: $p0")
    }
}