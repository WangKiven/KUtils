package com.kiven.sample.push

import android.content.Context
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.kiven.kutils.logHelper.KLog


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
}