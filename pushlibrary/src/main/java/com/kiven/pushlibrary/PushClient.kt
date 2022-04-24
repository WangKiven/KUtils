package com.kiven.pushlibrary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.heytap.msp.push.HeytapPushManager
import com.kiven.pushlibrary.firebase.FirebaseHelper
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

    private const val defaultPush = "mi"
    private const val defaultPushKey = "default_push_mi_or_firebase_or_none"
    private const val miPushEnableKey = "mi_push_enable"
    private const val firebaseEnableKey = "firebase_push_enable"

    fun shouldRequestPermission(context: Context): Boolean {

        return when (Build.BRAND.lowercase()) {
            "huawei", "honor", "oppo", "vivo", "xiaomi", "redmi" -> false
            else -> {
                val manifest = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val defaultPushIsMi = manifest.metaData?.getString(defaultPushKey, defaultPush) == "mi"

                // 非小米的不知名品牌，使用默认推送如果是小米推送，需要获取权限。
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && defaultPushIsMi) {
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

        val bundleData = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA).metaData
                ?: Bundle()

        when (Build.BRAND.lowercase()) {
            "huawei", "honor" -> {
                if (bundleData.getBoolean("hms_push_enable", true)) {
                    Web.shouldWebSocket = false
                    pushHelper = HuaWeiPushHelper()
                }
            }
            "oppo", "oneplus", "realme" -> {// 一加，oppo, realme
                if (bundleData.getBoolean("oppo_push_enable", true)) {
                    if (HeytapPushManager.isSupportPush(context)) {
                        Web.shouldWebSocket = true
                        pushHelper = OPPOPushHelper()
                    }
                }
            }
            "vivo" -> {
                if (bundleData.getBoolean("vivo_push_enable", true)) {
                    if (PushClient.getInstance(context).isSupport) {
                        Web.shouldWebSocket = true
                        pushHelper = VivoPushHelper()
                    }
                }
            }
            "xiaomi", "redmi" -> {
                if (bundleData.getBoolean(miPushEnableKey, true)) {
                    Web.shouldWebSocket = false
                    pushHelper = MiPushHelper()
                }
            }
        }

        // 不可知品牌，使用默认推送。
        if (pushHelper == null) {
            when (bundleData.getString(defaultPushKey, defaultPush)) {
                "mi" -> {
                    if (bundleData.getBoolean(miPushEnableKey, true)) {
                        Web.shouldWebSocket = true
                        pushHelper = MiPushHelper()
                    }
                }
                "firebase" -> {
                    if (bundleData.getBoolean(firebaseEnableKey, true)) {
                        Web.shouldWebSocket = true
                        pushHelper = FirebaseHelper()
                    }
                }
                "none" -> {
                    Web.shouldWebSocket = false
                }
            }
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
                Web.onClick(jsonObject.getString("msgUnicode"), jsonObject.getBoolean("isDebug"), jsonObject.getString("serverKey"))
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