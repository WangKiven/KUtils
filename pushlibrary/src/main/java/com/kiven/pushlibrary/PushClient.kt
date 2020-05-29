package com.kiven.pushlibrary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.heytap.mcssdk.PushManager
import com.kiven.pushlibrary.hw.HuaWeiPushHelper
import com.kiven.pushlibrary.mi.MiPushHelper
import com.kiven.pushlibrary.oppo.OPPOPushHelper
import com.kiven.pushlibrary.vivo.VivoPushHelper
import com.vivo.push.PushClient
import org.json.JSONObject

object PushClient {
    private var pushHelper: PushHelper? = null

    val hasInit: Boolean
        get() = pushHelper != null && pushHelper?.hasInitSuccess == true

    fun shouldRequestPermission(context: Context): Boolean {
        return when (Build.BRAND.toLowerCase()) {
            "huawei", "honor", "oppo", "vivo", "xiaomi", "redmi" -> false
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    false
                }
            }
        }
    }

    fun initPush(
        context: Context,
        projectKey: String,
        host: String,
        ishttps: Boolean,
        isDebug: Boolean
    ) {
        Web.context = context.applicationContext
        Web.projectKey = projectKey
        Web.host = host
        Web.ishttps = ishttps
        Web.isDebug = isDebug

        when (Build.BRAND.toLowerCase()) {
            "huawei", "honor" -> {
                Web.shouldWebSocket = false
                pushHelper = HuaWeiPushHelper()
            }
            "oppo" -> {
                if (PushManager.isSupportPush(context)) {
                    Web.shouldWebSocket = true
                    pushHelper = OPPOPushHelper()
                }
            }
            "vivo" -> {
                if (PushClient.getInstance(context).isSupport) {
                    Web.shouldWebSocket = true
                    pushHelper = VivoPushHelper()
                }
            }
            "xiaomi", "redmi" -> {
                Web.shouldWebSocket = false
                pushHelper = MiPushHelper()
            }
        }

        if (pushHelper == null) {
            Web.shouldWebSocket = true
            pushHelper = MiPushHelper()
        }

        pushHelper?.initPush(context)
    }

    fun findData(intent: Intent?): String? {
        if (intent == null) return null

        // todo 所有平台推送支持intent.data，部分平台支持intent.extras，PushService又仅支持intent.extras。所以需要两个都拿, sArgu和argu都是这样


        val sArgu = intent.data?.getQueryParameter("sArgu") ?: intent.extras?.getString("sArgu")
        sArgu?.also {
            try {
                val jsonObject = JSONObject(it)
                Web.onClick(jsonObject.getString("msgUnicode"), jsonObject.getBoolean("isDebug"))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        return intent.data?.getQueryParameter("argu") ?: intent.extras?.getString("argu")
    }

    fun setTags(context: Context, tags: Set<String>) {
        pushHelper?.setTags(context, tags)

        Web.setTags(tags.joinToString(","))
    }

    fun setAccount(context: Context, account: String) {
//        pushHelper?.setAccount(context, account)
        Web.bindAccount(account)
    }

    fun setOnClickNotiListener(listener: (Activity, String?) -> Unit) {
        ClickNotiActivity.onClickNotiListener = listener
    }
}