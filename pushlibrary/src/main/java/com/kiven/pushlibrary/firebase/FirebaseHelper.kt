package com.kiven.pushlibrary.firebase

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.PushHelper
import com.kiven.pushlibrary.Web

/**
 * Created by oukobayashi on 2020/7/8.
 * https://firebase.google.com/docs/cloud-messaging/android/send-multiple?authuser=0
 * https://console.firebase.google.com
 */
class FirebaseHelper : PushHelper {
    override var hasInitSuccess: Boolean = false

    override fun initPush(context: Context) {
        // 此值一经设置便会持久保存，不受应用重启的影响
//        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        KLog.e(task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token ?: ""

                    Web.register(context, token, 6)
                    KLog.i("firebaseToken = $token")
                })
    }

    // 没有找到获取已订阅的接口
    override fun setTags(context: Context, tags: Set<String>) {
//        FirebaseMessaging.getInstance().subscribeToTopic()

    }
}