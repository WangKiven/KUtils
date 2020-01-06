package com.kiven.sample.push

import android.content.Context
import com.kiven.kutils.logHelper.KLog
import com.vivo.push.PushClient

/**
 *
 * 集成说明：https://dev.vivo.com.cn/documentCenter/doc/233
 * api接口文档：https://dev.vivo.com.cn/documentCenter/doc/232
 */
object VivoPushHelper {
    // 是否初始化
    private var isInit = false

    fun turnOnPush(context: Context) {
        PushClient.getInstance(context).apply {
            if (isSupport) {
                if (!isInit){
                    initialize()
                    isInit = true
                }
                turnOnPush {
                    if (it == 0 || it == 1) {
                        KLog.i("操作成功，id = $regId")
                    } else KLog.i("操作失败")
                }
            } else KLog.i("不支持vivo推送")
        }
    }

    fun turnOffPush(context: Context) {
        PushClient.getInstance(context).turnOffPush {
            if (it == 0 || it == 1) {
                KLog.i("操作成功")
            } else KLog.i("操作失败")
        }

        /*PushClient.getInstance(context).bindAlias("") {}
        PushClient.getInstance(context).unBindAlias("") {}

        PushClient.getInstance(context).setTopic(""){}
        PushClient.getInstance(context).delTopic("") {}

        PushClient.getInstance(context).topics*/
    }

}