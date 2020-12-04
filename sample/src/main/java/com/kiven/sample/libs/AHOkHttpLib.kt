package com.kiven.sample.libs

import android.os.Bundle
import android.view.View
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.BaseFlexActivityHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import java.io.IOException

/**
 * Created by wangk on 2020/12/4.
 */
class AHOkHttpLib : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)


        addBtn("同步") {
            GlobalScope.launch {
                val httpUrl = "https://www.baidu.com"
                val method = "/s"
                val param = mapOf(
                        "ie" to "UTF-8",
                        "wd" to "美女"
                )

                // 请求参数
                val requestBody = FormBody.Builder()

                for ((key, value) in param) {
                    requestBody.add(key, value)
                }

                // 请求配置
                val uri = "$httpUrl${method}"

                val request = okhttp3.Request.Builder()
                        .url(uri)
                        .post(requestBody.build())
                /*if (isLogin) {
                    request.addHeader("Client-User-Id", appUser.value!!.id!!)
                    request.addHeader("Client-Token", appSecret!!.token)
                }*/

                // 请求
                try {
                    val response = OkHttpClient().newCall(request.build()).execute()
                    val result = response.body?.string()
                    KLog.i("OkHttp请求结果(${response.protocol}): $result")
                } catch (e: Exception) {
                    KLog.e(e)
                }
            }
        }

        addBtn("异步") {
            val client = OkHttpClient.Builder().build()

            val request = okhttp3.Request.Builder().url("https://www.yimizi.xyz:18080/api/open/push/register")
                    .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    KLog.e(e.message)
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    KLog.e("(${response.protocol.name})\n" + response.body?.string())
                }
            })
        }
    }
}