package com.kiven.pushlibrary

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.mi.MiPushHelper
import com.kiven.pushlibrary.vivo.VivoPushHelper
import com.vivo.push.PushClient

object PushClient {
    private var pushHelper: PushHelper? = null
    fun initPush(context: Context) {
        when (Build.BRAND.toLowerCase()) {
            "huawei", "honor" -> {

            }
            "oppo" -> {
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

    fun clearTags(context: Context) {
        pushHelper?.clearTags(context)
    }

    fun setAccount(context: Context, account: String) {
        pushHelper?.setAccount(context, account)
    }

    fun removeAccount(context: Context) {
        pushHelper?.removeAccount(context)
    }

}