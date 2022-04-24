package com.kiven.pushlibrary.vivo

import android.content.Context
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.Web
import com.vivo.push.model.UPSNotificationMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver

/**
 * Created by oukobayashi on 2020-01-06.
 *
 * https://dev.vivo.com.cn/documentCenter/doc/364
 */
class VivoPushReceiver : OpenClientPushMessageReceiver() {
    // TODO: 2021/1/7   vivo推送api3.0.0.0废弃了该回调，3.0.0.2又恢复了该方法
//    override fun onNotificationMessageClicked(p0: Context?, p1: UPSNotificationMessage?) {
//        if (p0 == null || p1 == null) return
//        KLog.i("vivo 点击了通知")
//        KLog.printClassField(p1, null, true)
//
//        val skipType = p1.skipType//1：打开APP首页 2：打开链接 3：自定义 4：打开app内指定页面
//        // 跳转内容 跳转类型为2时，跳转内容最大1000个字符，跳转类型为3或4时，跳转内容最大1024个字符，
//        // skipType传3需要在onNotificationMessageClicked回调函数中自己写处理逻辑。
//        val skipContext = p1.skipContent
//        /*try {
//            val skipContent = p1.skipContent
//            p0.startActivity(Intent.parseUri(skipContent, Intent.URI_INTENT_SCHEME))
//        }catch (e:Exception) {
//            KLog.e(e)
//            KAppTool.startApp(p0, p0.packageName)
//        }*/
//
//        /*p0.startActivity(Intent(p0, ClickNotiActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        })*/
//        /*val arguUrl = p1.skipContent
//        val handler = Handler(Handler.Callback {
//            if (!arguUrl.isNullOrBlank()) {
//                try {
//                    val uri = Uri.parse(arguUrl)
//
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.data = uri
//
//                    val list = p0.packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)
//
//                    //URL Scheme是否有效
//                    if (list.isNotEmpty()) {
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
//                        p0.startActivity(intent)
//                        return@Callback true
//                    }
//                }catch (e:Throwable) {
//                    KLog.e(e)
//                }
//
//                KAppTool.startApp(p0, p0.packageName)
//            }
//            return@Callback true
//        })
//        handler.sendEmptyMessageDelayed(0, 1000 * 4)*/
//    }

    override fun onReceiveRegId(p0: Context?, p1: String?) {
        KLog.i("vivo 注册获得id: $p1")

        p0 ?: return
        p1 ?: return

        Web.register(p0, p1, 3)//设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
    }
}