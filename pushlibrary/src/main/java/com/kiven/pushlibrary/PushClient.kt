package com.kiven.pushlibrary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.heytap.mcssdk.PushManager
import com.kiven.pushlibrary.hw.HuaWeiPushHelper
import com.kiven.pushlibrary.mi.MiPushHelper
import com.kiven.pushlibrary.oppo.OPPOPushHelper
import com.kiven.pushlibrary.vivo.VivoPushHelper
import com.vivo.push.PushClient

object PushClient {
    private var pushHelper: PushHelper? = null

    val hasInit: Boolean
        get() = pushHelper != null

    fun shouldRequestPermission(context: Context): Boolean {
        return when (Build.BRAND.toLowerCase()) {
            "huawei", "honor", "oppo", "vivo", "xiaomi", "redmi" -> false
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                } else {
                    false
                }
            }
        }
    }

    fun initPush(context: Context, projectKey: String, host: String, ishttps: Boolean) {
        Web.context = context.applicationContext
        Web.projectKey = projectKey
        Web.host = host
        Web.ishttps = ishttps

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

    fun setOnClickNotiListener(listener: (Activity, String?) -> Unit) {
        ClickNotiActivity.onClickNotiListener = listener
    }
}