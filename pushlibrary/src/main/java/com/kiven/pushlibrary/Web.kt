package com.kiven.pushlibrary

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kiven.kutils.logHelper.KLog
import com.sxb.kutils_ktx.util.KWeb
import org.json.JSONObject

internal object Web {
    /*const val httpPre = "http://192.168.101.105:8080/api/"
    private const val registerUrl = "${httpPre}open/push/register"
    private const val bindAccountUrl = "${httpPre}open/push/bindAccount"
    private const val setTagsUrl = "${httpPre}open/push/setTags"*/

    var context: Context? = null
    var shouldWebSocket: Boolean = false

    var projectKey = "projectKey_sample"

    var ishttps = false
    var host = "192.168.101.106:8080"
    var isDebug = true

    private val httpPre
        get() = "${if (ishttps) "https" else "http"}://$host/api/"
    private val registerUrl
        get() = "${httpPre}open/push/register"
    private val bindAccountUrl
        get() = "${httpPre}open/push/bindAccount"
    private val setTagsUrl
        get() = "${httpPre}open/push/setTags"
    private val msgCallBack
        get() = "${httpPre}open/push/msgCallBack"

    private val wsPre
        get() = "${if (ishttps) "wss" else "ws"}://$host/socket"

    // TODO: 2020/5/15  拿取这些值的时候需要注意，不是设置后马上就能拿到正确的值，需要等待与服务器同步完成后才能拿到正确的值
    var tokenOrId: String = ""
        private set
    var platform: Int = 5
        private set
    var account: String = ""
        private set
    var tagOrTopics: String = ""
        private set

    /**
     * @param platformN 设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
     */
    @Synchronized
    fun register(context: Context, tokenOrIdN: String, platformN: Int) {
        if (tokenOrId == tokenOrIdN) {
            return
        }

//        tokenOrId = tokenOrIdN
        platform = platformN

        when (platformN) {
            2 -> {
                // 提前生成channel，可防止系统生成默认channel的相关参数，如名称，说明，等级等。系统默认的chnnelId为"com.huawei.android.pushagent.low"
                // 测试后发现，当有系统推送时，会将"com.huawei.android.pushagent.low"的参数重置。但是之前是关闭的话，系统不会重置。
                PushUtil.initChannel(context)
            }
            3 -> {
            }
            4 -> {
            }
        }

        registerTaskId = System.currentTimeMillis()
        register(registerTaskId, tokenOrIdN, platformN)
    }

    private var registerTaskId = 0L
    private fun register(taskId: Long, tokenOrIdN: String, platformN: Int) {
        if (registerTaskId != taskId) return

        Thread {
            try {
                val result = KWeb.request(
                    registerUrl, mapOf(
                        "projectKey" to projectKey,
                        "tokenOrId" to tokenOrIdN,
                        "platform" to platformN,
                        "isDebug" to isDebug
                    ), hostnameVerifier = !host.startsWith("192.168.")// 本地电脑不验证
                )

                val json = JSONObject(result)
                if (json.getInt("status") != 200) {
                    Thread.sleep(1000 * 30)
                    register(taskId, tokenOrIdN, platformN)
                } else {

                    try {
                        json.optJSONObject("data")?.apply {
                            account = optString("account")

                            var tags = optString("tagOrTopics")
                            if (tags.startsWith(",")) {
                                tags = tags.substring(1)
                            }
                            if (tags.endsWith(",")) {
                                tags = tags.substring(0, tags.length - 1)
                            }

                            tagOrTopics = tags
                        }
                    } catch (t: Throwable) {
                        KLog.e(t)
                    }
                    // 在标签和账号设置完成后，再设置token和平台，防止bindAccount()和setTags()两个方法判断失误
                    tokenOrId = tokenOrIdN
                    platform = platformN


                    if (shouldWebSocket) {
                        context?.apply {
                            startService(Intent(this, PushService::class.java).apply {
                                putExtra(
                                    "url",
                                    "${wsPre}?projectKey=${Uri.encode(projectKey)}&tokenOrId=${Uri.encode(
                                        tokenOrId
                                    )}"
                                )
                            })
                        }
                    }
                }

            } catch (e: Throwable) {
                KLog.e(e)
                Thread.sleep(1000 * 30)
                register(taskId, tokenOrIdN, platformN)
            }
        }.start()
    }

    @Synchronized
    fun bindAccount(accountN: String) {
        if (account.isNotEmpty() && account == accountN) {
            return
        }
//        account = accountN

        accountTaskId = System.currentTimeMillis()
        bindAccount(accountTaskId, accountN)
    }

    private var accountTaskId = 0L
    private fun bindAccount(taskId: Long, accountN: String) {
        if (accountTaskId != taskId) return

        Thread {
            while (tokenOrId.isBlank()) {
                Thread.sleep(10000)
            }
            // 设备注册完成后，得知已绑定该账号
            if (account == accountN) return@Thread

            try {
                val result = KWeb.request(
                    bindAccountUrl, mapOf(
                        "projectKey" to projectKey,
                        "tokenOrId" to tokenOrId,
                        "account" to accountN
                    ), hostnameVerifier = !host.startsWith("192.168.")// 本地电脑不验证
                )

                val json = JSONObject(result)
                if (json.getInt("status") != 200) {
                    Thread.sleep(1000 * 30)
                    bindAccount(taskId, accountN)
                } else {
                    account = accountN
                }

            } catch (e: Throwable) {
                KLog.e(e)
                Thread.sleep(1000 * 30)
                bindAccount(taskId, accountN)
            }
        }.start()
    }

    @Synchronized
    fun setTags(tagOrTopicsN: String) {
        if (tagOrTopics.isNotEmpty() && tagOrTopics == tagOrTopicsN) return
//        tagOrTopics = tagOrTopicsN

        tagsTaskId = System.currentTimeMillis()
        setTags(tagsTaskId, tagOrTopicsN)
    }

    private var tagsTaskId = 0L
    private fun setTags(taskId: Long, tagOrTopicsN: String) {
        if (tagsTaskId != taskId) return

        Thread {
            while (tokenOrId.isBlank()) {
                Thread.sleep(10000)
            }
            // 设备注册完成后，得知已绑定该标签
            if (tagOrTopics == tagOrTopicsN) return@Thread

            try {
                val result = KWeb.request(
                    setTagsUrl, mapOf(
                        "projectKey" to projectKey,
                        "tokenOrId" to tokenOrId,
                        "tagOrTopics" to tagOrTopicsN
                    ), hostnameVerifier = !host.startsWith("192.168.")// 本地电脑不验证
                )

                val json = JSONObject(result)
                if (json.getInt("status") != 200) {
                    Thread.sleep(1000 * 30)
                    setTags(taskId, tagOrTopicsN)
                } else {
                    tagOrTopics = tagOrTopicsN
                }

            } catch (e: Throwable) {
                KLog.e(e)
                Thread.sleep(1000 * 30)
                setTags(taskId, tagOrTopicsN)
            }
        }.start()
    }

    fun onClick(msgUnicode: String, isDebug: Boolean, serverKey: String) {
        Thread {
            while (tokenOrId.isBlank()) {
                Thread.sleep(5000)
            }

            if (platform == 5) {
                // 小米系统有点击回调，这里就不上传了
                return@Thread
            }

            try {
                val result = KWeb.request(
                    msgCallBack, mapOf(
                        "projectKey" to projectKey,
                        "tokenOrId" to tokenOrId,
                        "platform" to platform,
                        "msgUnicode" to msgUnicode,
                        "isDebug" to isDebug,
                        "type" to 3,
                        "serverKey" to serverKey
                    ), hostnameVerifier = !host.startsWith("192.168.")// 本地电脑不验证
                )

                val json = JSONObject(result)
                if (json.getInt("status") != 200) {
                    Thread.sleep(1000 * 30)
                    onClick(msgUnicode, isDebug, serverKey)
                }

            } catch (e: Throwable) {
                KLog.e(e)
                Thread.sleep(1000 * 10)
                onClick(msgUnicode, isDebug, serverKey)
            }
        }.start()
    }
}