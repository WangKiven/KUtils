package com.kiven.pushlibrary

import android.content.Context
import android.os.Build
import com.heytap.mcssdk.PushManager
import com.kiven.pushlibrary.hw.HuaWeiPushHelper
import com.kiven.pushlibrary.mi.MiPushHelper
import com.kiven.pushlibrary.oppo.OPPOPushHelper
import com.kiven.pushlibrary.vivo.VivoPushHelper
import com.vivo.push.PushClient

object PushClient {
    private var pushHelper: PushHelper? = null

    val isInit: Boolean
        get() = pushHelper != null

    var projectKey: String
        get() = Web.projectKey
        set(value) {
            Web.projectKey = value
        }

    var host: String
        get() = Web.host
        set(value) {
            Web.host = value
        }

    var ishttps: Boolean
        get() = Web.ishttps
        set(value) {
            Web.ishttps = value
        }

    fun initPush(context: Context) {
        Web.context = context.applicationContext

        when (Build.BRAND.toLowerCase()) {
            "huawei", "honor" -> {
                pushHelper = HuaWeiPushHelper()
            }
            "oppo" -> {
                if (PushManager.isSupportPush(context)) {
                    pushHelper = OPPOPushHelper()
                }
            }
            "vivo" -> {
                if (PushClient.getInstance(context).isSupport) {
                    pushHelper = VivoPushHelper()
                }
            }
        }

        if (pushHelper == null) pushHelper = MiPushHelper()

        pushHelper?.initPush(context)
    }

    fun setTags(context: Context, tags: Set<String>) {
        pushHelper?.setTags(context, tags)
    }

    fun setAccount(context: Context, account: String) {
//        pushHelper?.setAccount(context, account)
        Web.bindAccount(account)
    }

}