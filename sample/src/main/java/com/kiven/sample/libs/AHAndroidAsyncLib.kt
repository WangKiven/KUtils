package com.kiven.sample.libs

import android.os.Bundle
import android.view.View
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KNetwork
import com.kiven.kutils.tools.KToast
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.util.showSnack
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.server.AsyncHttpServer

/**
 * Created by wangk on 2020/12/4.
 *
 * https://github.com/koush/AndroidAsync
 */
class AHAndroidAsyncLib:BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        addBtn("AndroidAsync 接收", {
            val server = AsyncHttpServer()
            server.websocket("/live", "my-protocal") { webSocket, request ->
                webSocket.setClosedCallback {
                    if (it == null) {
                        KToast.ToastMessage("websocket close")
                    } else {
                        KToast.ToastMessage("websocket close ,cause of error")
                    }
                }

                webSocket.setStringCallback {
                    mActivity.showSnack("接收到推送：$it")
                }
            }
            server.listen(5001)
        })
        addBtn("AndroidAsync 发送", {
            AsyncHttpClient.getDefaultInstance().websocket("ws://" + KNetwork.getIPAddress() + ":5001/live", "my-protocal") { ex, webSocket ->
                if (ex == null) {
                    webSocket.send("Hello World")
                } else
                    KLog.e(ex)
            }
        })
    }
}