package com.kiven.pushlibrary.mi

import android.content.Context
import com.kiven.kutils.logHelper.KLog
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.Logger
import com.xiaomi.mipush.sdk.MiPushClient

/**
 * 接入指南：https://dev.mi.com/console/doc/detail?pId=100
 * SDK使用指南：https://dev.mi.com/console/doc/detail?pId=41
 */
object MiPushHelper {
    fun initMiPush(context: Context){
//        if (!shouldInitMiPush()) return

        //初始化push推送服务
        // TODO: 2019-12-31  在非MIUI平台下，如果targetSdkVersion>=23，需要动态申请电话和存储权限，请在申请权限后再调用注册接口，否则会注册失败。
        MiPushClient.registerPush(context, "2882303761518292808", "5681829285808")

        //打开Log
        val newLogger: LoggerInterface = object : LoggerInterface {
            override fun setTag(tag: String) { // ignore
                KLog.i("MiPush注册标签:$tag")
            }

            override fun log(content: String, t: Throwable) {
                KLog.e(Throwable("MiPush:$content", t))
            }

            override fun log(content: String) {
                KLog.i("MiPush:$content")
            }
        }
        Logger.setLogger(context, newLogger)
    }


}