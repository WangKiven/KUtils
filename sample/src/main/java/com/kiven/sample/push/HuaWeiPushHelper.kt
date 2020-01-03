package com.kiven.sample.push

import android.content.Context
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging
import com.kiven.kutils.logHelper.KLog

/**
 * https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/push-basic-client
 */
object HuaWeiPushHelper {

    var token:String? = null
    /**
     * 可能耗时，需异步
     *
     * 出现异常的可能情况：
     * 1 下载的 agconnect-services.json 文件名称不对，有可能下载下来文件名称是"agconnect-services.json.txt"，需要去掉".txt"
     *
     *
     * 华为测试机token: 0865265045829291300005487100CN01
     */
    fun initHuaWeiPush(context: Context):Boolean {
        return try {
            val appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id")
            KLog.i("华为appId: $appId")

            // TODO Token发生变化时或者EMUI版本低于10.0以 onNewToken 方法返回
            token = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
            KLog.i("HmsInstanceId获取华为token: $token")

            true
        } catch (e: Exception) {
            KLog.e(e)
            false
        }
    }

    fun unregisterPush(context: Context) {
        token?.apply {
            HmsInstanceId.getInstance(context).deleteToken(this, "HCM")
        }
    }

    /**
     * 限制：
     * 一个应用实例不可订阅超过2000个主题。
     * 该功能仅在EMUI版本不低于10.0的华为设备上支持。
     * 华为移动服务（APK）的版本不低于3.0.0。
     */
    fun subscribe(context: Context, topic:String) {
        HmsMessaging.getInstance(context).subscribe(topic)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        KLog.i("华为推送注册主题 $topic 成功")
                    }else {
                        KLog.i("华为推送注册主题 $topic 失败，${it.exception.message}")
                    }
                }
    }
    fun unsubscribe(context: Context, topic:String) {
        HmsMessaging.getInstance(context).unsubscribe(topic)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        KLog.i("华为推送注销主题 $topic 成功")
                    }else {
                        KLog.i("华为推送注销主题 $topic 失败，${it.exception.message}")
                    }
                }
    }
}