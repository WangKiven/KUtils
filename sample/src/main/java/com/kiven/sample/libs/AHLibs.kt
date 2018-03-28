package com.kiven.sample.libs

import android.os.Bundle
import android.view.View
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KToast
import com.kiven.sample.R
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.server.AsyncHttpServer
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * Created by wangk on 2018/3/27.
 */
class AHLibs : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_libs)

        val ip = getIPAddress()
        KLog.i(ip)
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.btn_aa_get -> {
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
                        KToast.ToastMessage("接收到推送：$it")
                    }
                }
                server.listen(5001)
            }
            R.id.btn_aa_send -> {
                AsyncHttpClient.getDefaultInstance().websocket(getIPAddress() + ":5001/live", "my-protocal") { ex, webSocket ->
                    if (ex == null) {
                        webSocket.send("Hello World")
                    } else
                        KLog.e(ex)
                }

            }
        }
    }

    fun getIPAddress(): String? {

        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    //这里需要注意：这里增加了一个限定条件( inetAddress instanceof Inet4Address ),主要是在Android4.0高版本中可能优先得到的是IPv6的地址
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: Exception) {
            KLog.e(ex)
        }

        return null
    }
}