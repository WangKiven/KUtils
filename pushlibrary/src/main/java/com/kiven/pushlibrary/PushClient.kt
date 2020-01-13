package com.kiven.pushlibrary

import android.content.Context
import android.os.Build
import com.kiven.pushlibrary.mi.MiPushHelper

object PushClient {
    private var pushHelper: PushHelper? = null
    fun initPush(context: Context) {
        val deviceName = Build.BRAND.toLowerCase()
        when(deviceName) {
            "huawei","honor" -> {

            }
            "oppo" ->{}
            "vivo" -> {}
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