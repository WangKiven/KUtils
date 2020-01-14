package com.kiven.pushlibrary.hw

import android.content.Context
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.PushHelper

/**
 * 集成SDK：https://developer.huawei.com/consumer/cn/doc/development/HMS-Library/push-sdk-integrate
 * api：https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/push-basic-client
 *
 * 主题问题：
 *      华为客户端SDK进提供了添加主题、取消主题两个api，所以无法取消之前设置的主题，因为根本不知道之前设置了什么主题
 *      解决方案：调用我们自己的服务器设置主题，服务器可以通过华为提供的接口查询到之前设置过的主题，从而判断添加什么主题，取消什么主题
 */
class HuaWeiPushHelper:PushHelper {

    companion object {
        var token:String? = null
    }

    /**
     * 可能耗时，需异步
     *
     * 出现异常的可能情况：
     * 1 下载的 agconnect-services.json 文件名称不对，有可能下载下来文件名称是"agconnect-services.json.txt"，需要去掉".txt"
     *
     *
     * 华为测试机token: 0865265045829291300005487100CN01
     */
    override fun initPush(context: Context) {
        Thread{
            try {
                val appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id")
                KLog.i("华为appId: $appId")

                // TODO Token发生变化时或者EMUI版本低于10.0以 onNewToken 方法返回
                token = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
                KLog.i("HmsInstanceId获取华为token: $token")

            } catch (e: Exception) {
                KLog.e(e)
            }
        }.start()
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

    override fun setTags(context: Context, tags: Set<String>) {
        HmsMessaging.getInstance(context).apply {
            tags.forEach {tag ->
                subscribe(tag).addOnCompleteListener {
                    if (it.isSuccessful) {
                        KLog.i("华为推送注册主题 $tag 成功")
                    }else {
                        KLog.i("华为推送注册主题 $tag 失败，${it.exception.message}")
                    }
                }
            }
        }
    }

    override fun clearTags(context: Context) {
        /*HmsMessaging.getInstance(context).apply {

        }*/
    }

    override fun setAccount(context: Context, account: String) {
    }

    override fun removeAccount(context: Context) {
    }
}