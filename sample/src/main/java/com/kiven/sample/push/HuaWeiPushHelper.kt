package com.kiven.sample.push

import android.content.Context
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.kiven.kutils.logHelper.KLog


object HuaWeiPushHelper {

    var token:String? = null
    /**
     * 可能耗时，需异步
     */
    fun initHuaWeiPush(context: Context):Boolean {
        return try {
            val appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id")
            token = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
            KLog.i("华为token: $token")

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