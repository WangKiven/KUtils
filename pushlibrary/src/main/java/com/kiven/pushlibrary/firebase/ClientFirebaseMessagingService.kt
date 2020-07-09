package com.kiven.pushlibrary.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.Web

class ClientFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Web.register(this, p0, 6)
        KLog.i("firebaseToken = $p0")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        // app没启动，不回调用这个方法
        KLog.printClassField(p0, null, true)
        KLog.i(p0.data.toList().joinToString { it.toString() })
    }
}