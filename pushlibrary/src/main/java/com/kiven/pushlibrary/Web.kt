package com.kiven.pushlibrary

object Web {
    private const val httpUrl = "http://192.168.0.108:8080/api/open/push/register"
    /*fun request(method:String, param: Map<String, Any?>) {

        // 请求参数
        val requestBody = FormBody.Builder()

        for ((key, value) in param) {
            requestBody.add(key, value.toString())
        }

        // 请求配置
        val uri = "$httpUrl${method}"
        val request = Request.Builder()
                .url(uri)
                .post(requestBody.build())
        if (isLogin) {
            request.addHeader("Client-User-Id", appUser.value!!.id!!)
            request.addHeader("Client-Token", appSecret!!.token)
        }

        // 请求
        try {
            OkHttpClient().newCall(request.build()).execute().body?.string()
        }catch (e:Exception) {

        }
    }*/
}