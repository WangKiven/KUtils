package com.kiven.sample.push

import android.content.Context
import com.heytap.mcssdk.PushManager
import com.heytap.mcssdk.callback.PushAdapter
import com.heytap.mcssdk.mode.ErrorCode
import com.kiven.kutils.logHelper.KLog

/**
 * 标签 别名 账号都不能用了，已标记过时
 */
object OPPOPushHelper {
    fun initOPPOPush(context: Context) {
        PushManager.getInstance().register(context, "09e71d4db52046768cf431af43f11579",
                "a1b2d2c0564d46e3b5319241bdeba7c1", object : PushAdapter() {
            override fun onRegister(responseCode: Int, registerID: String?) {
                if (responseCode == ErrorCode.SUCCESS) {
                    KLog.i("OPPO推送注册成功, registerID = $registerID")
                }else {
                    KLog.i("OPPO推送注册失败，responseCode = $responseCode")
                }
            }

            override fun onUnRegister(responseCode: Int) {
                if (responseCode == ErrorCode.SUCCESS) {
                    KLog.i("OPPO推送注销成功")
                }else {
                    KLog.i("OPPO推送注销失败，responseCode = $responseCode")
                }
            }
            // // TODO: 2020-01-06 标签 别名 账号都不能用了，已标记过时
        })
    }
}