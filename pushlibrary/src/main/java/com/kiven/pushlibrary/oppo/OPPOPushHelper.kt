package com.kiven.pushlibrary.oppo

import android.content.Context
import android.content.pm.PackageManager
import com.heytap.mcssdk.PushManager
import com.heytap.mcssdk.callback.PushAdapter
import com.heytap.mcssdk.mode.ErrorCode
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.PushHelper
import com.kiven.pushlibrary.Web

/**
 * 标签 别名 账号都不能用了，已标记过时
 */
class OPPOPushHelper : PushHelper {
    override var hasInitSuccess: Boolean = false

    override fun initPush(context: Context) {
        val manifest = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val bundleData = manifest.metaData ?: throw Throwable("manifest中oppo配置信息为空")

        val appKey = bundleData.getString("oppo_app_key") ?: throw Throwable("oppo APPKey为空")
        val appSecret = bundleData.getString("oppo_app_secret")
                ?: throw Throwable("oppo APPSecret为空")

        PushManager.getInstance().register(context, appKey,
                appSecret, object : PushAdapter() {
            override fun onRegister(responseCode: Int, registerID: String?) {
                if (responseCode == ErrorCode.SUCCESS) {
                    KLog.i("OPPO推送注册成功, registerID = $registerID")

                    Web.register(context, registerID!!, 4)//设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
                } else {
                    KLog.i("OPPO推送注册失败，responseCode = $responseCode")
                }
            }

            override fun onUnRegister(responseCode: Int) {
                if (responseCode == ErrorCode.SUCCESS) {
                    KLog.i("OPPO推送注销成功")
                } else {
                    KLog.i("OPPO推送注销失败，responseCode = $responseCode")
                }
            }
            // // TODO: 2020-01-06 标签 别名 账号都不能用了，已标记过时
        })
        hasInitSuccess = true
    }

    override fun setTags(context: Context, tags: Set<String>) {
    }
}