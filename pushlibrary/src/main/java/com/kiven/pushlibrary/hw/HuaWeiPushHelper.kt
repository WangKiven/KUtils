package com.kiven.pushlibrary.hw

import android.content.Context
import android.content.pm.PackageManager
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.PushHelper
import com.kiven.pushlibrary.PushUtil
import com.kiven.pushlibrary.Web

/**
 * https://developer.huawei.com/consumer/cn/hms/huawei-pushkit
 *
 * 主题问题：
 *      华为客户端SDK进提供了添加主题、取消主题两个api，所以无法取消之前设置的主题，因为根本不知道之前设置了什么主题
 *      解决方案：调用我们自己的服务器设置主题，服务器可以通过华为提供的接口查询到之前设置过的主题，从而判断添加什么主题，取消什么主题
 */
class HuaWeiPushHelper : PushHelper {
    override var hasInitSuccess: Boolean = false

    companion object {
        val token: String
            get() = Web.tokenOrId
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
    override fun initPush(context: Context, isAgreePrivacy: Boolean) {
        Thread {
            try {

                val manifest = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val bundleData = manifest.metaData ?: throw Throwable("manifest中配置信息为空")

                val appId = bundleData.getString("hms_app_id")?.replaceFirst("HMS_", "") ?: throw Throwable("华为AppID为空")

                // 新版本，不推荐使用agconnect-services.json获取appid了
//                val appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id")
                KLog.i("华为appId: $appId")

                // TODO Token发生变化时或者EMUI版本低于10.0以 onNewToken 方法返回。
                //  集成HMS SDK最新版本需要满足HMS Core（APK）的版本不低于3.0.0。
                //  如果低于3.0.0版本，当应用有前台界面时HMS SDK会提示用户升级HMS Core（APK）为最新版本，
                //  此时 HmsInstanceId.getInstance 的入参必须传入Activity类实例；当应用是后台应用时，
                //  如果开发者不想弹出引导升级的页面时，可以传入非Activity类型的Context类实例。
                val token = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
                if (!token.isNullOrBlank()) {
                    KLog.i("HmsInstanceId获取华为token: $token")
                    Web.register(context, token, 2)//设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
                }
                hasInitSuccess = true
            } catch (e: Exception) {
                KLog.e(e)
            }
        }.start()
    }

    fun unregisterPush(context: Context) {
        if (token.isNotBlank())
            token.apply {
                HmsInstanceId.getInstance(context).deleteToken(this, "HCM")
            }
    }

    /**
     * 限制：
     * 一个应用实例不可订阅超过2000个主题。
     * 该功能仅在EMUI版本不低于8.1的华为设备上支持。
     * HMS Core（APK）的版本不低于4.0.0。
     *
     * 测试机错误码：907122049 当前系统EMUI版本过低导致能力不可使用。
     */
    override fun setTags(context: Context, tags: Set<String>) {
        // 由于测试机设置华为主题，返回码是 907122049 当前系统EMUI版本过低导致能力不可使用。
        // 猜测很多华为设备都不行，所以只有自己来管理了。
        // 测试机是符合华为文档说的使用主题的限制的：https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/push-topic

        /*Thread {
            while (token.isNullOrBlank()) {
                Thread.sleep(10000)
            }

            try {
                val result = KWeb.request("${Web.httpPre}open/push/getDeviceInfo", mapOf("tokenOrId" to token))

                val json = JSONObject(result)

                if (json.getInt("status") != 200) {
                    Thread.sleep(20000)
                    return@Thread
                }

                val jsonTopics = json.optString("tagOrTopics")
                val oldTopics = jsonTopics.split(",").filter { !it.isBlank() }

                val addTags = mutableSetOf<String>()
                val delTags = mutableSetOf<String>()

                oldTopics.forEach {
                    if (!tags.contains(it)) delTags.add(it)
                }

                tags.forEach {
                    if (!oldTopics.contains(it)) addTags.add(it)
                }


                HmsMessaging.getInstance(context).apply {
                    addTags.forEach { tag ->
                        subscribe(tag).addOnCompleteListener {
                            if (it.isSuccessful) {
                                KLog.i("华为推送注册主题 $tag 成功")
                            } else {
                                KLog.i("华为推送注册主题 $tag 失败，${it.exception.message}")
                            }
                        }
                    }

                    delTags.forEach { tag ->
                        unsubscribe(tag).addOnCompleteListener {
                            if (it.isSuccessful) {
                                KLog.i("华为推送注销主题 $tag 成功")
                            } else {
                                KLog.i("华为推送注销主题 $tag 失败，${it.exception.message}")
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                KLog.e(e)
                setTags(context, tags)
            }
        }.start()*/

    }
}