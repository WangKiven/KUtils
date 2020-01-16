package com.sxb.kutils_ktx.util

import android.net.Uri
import com.kiven.kutils.logHelper.KLog
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL

object KWeb {

    /**
     * @param requestProperty 请求头
     */
    fun request(
            url: String,
            param: Map<String, Any?>? = null,
            requestProperty: Map<String, String?>? = null,
            requestMethod: String = "POST"
    ): String {
        // 请求参数
        var body:String? = null
                param?.apply {
            filter { it.value != null }.apply {
                if (size > 0) {
                    body = toList().joinToString("&") { "${Uri.encode(it.first)}=${Uri.encode(it.second!!.toString())}" }
                }
            }
        }

        return request(url, body, requestProperty, requestMethod)
    }
    /**
     * @param requestProperty 请求头
     */
    fun request(
            url: String,
            body:String? = null,
            requestProperty: Map<String, String?>? = null,
            requestMethod: String = "POST"
    ): String {
        try {
            val connect = URL(url)
                    .openConnection() as HttpURLConnection
            connect.requestMethod = requestMethod

            // 请求头
            requestProperty?.forEach {
                if (it.value != null) connect.addRequestProperty(it.key, it.value)
            }


            // 请求参数
            /*param?.apply {
                filter { it.value != null }.apply {
                    if (size > 0) {
                        connect.doOutput = true // 使用outputStream前，先确保 doOutput = true
                        val os = connect.outputStream
                        os.write(toList().joinToString("&") { "${it.first}=${it.second}" }.toByteArray())
                        os.close()
                    }
                }
            }*/
            // 请求体
            if (body != null) {
                connect.doOutput = true // 使用outputStream前，先确保 doOutput = true
                val os = connect.outputStream
                os.write(body.toByteArray())
                os.close()
            }

            val responseCode = connect.responseCode
            val isError = responseCode >= 400

            val inputStream = if (isError) connect.errorStream else connect.inputStream

            val datas = mutableListOf<Byte>()
            val buffer = ByteArray(512)
            var readLength: Int
            do {
                readLength = inputStream.read(buffer)
                if (readLength > 0) {
                    for (i in 0 until readLength) {
                        datas.add(buffer[i])
                    }
                }
            } while (readLength != -1)

            inputStream.close()

            val result = String(datas.toByteArray())

            KLog.i("网络请求：$url, 参数：$body, 结果：$responseCode $result")
            if (isError) {
                throw ConnectException("响应异常，异常码 = $responseCode, 错误信息：$result")
            } else return result
        } catch (e: Throwable) {
            throw e
        }
    }
}