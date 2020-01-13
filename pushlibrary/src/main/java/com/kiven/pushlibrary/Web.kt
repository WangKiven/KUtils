package com.kiven.pushlibrary

import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KUtil
import com.sxb.kutils_ktx.util.KWeb
import org.json.JSONObject

object Web {
    private const val httpPre = "http://192.168.0.108:8080/api/"
    private const val registerUrl = "${httpPre}open/push/register"

    var tokenOrId: String
        private set(value) = KUtil.putSharedPreferencesStringValue("push_lib_tokenOrId", value)
        get() = KUtil.getSharedPreferencesStringValue("push_lib_tokenOrId", "")


    var platform: Int
        private set(value) = KUtil.putSharedPreferencesIntValue("push_lib_platform", value)
        get() = KUtil.getSharedPreferencesIntValue("push_lib_platform", 0)

    var account: String
        private set(value) = KUtil.putSharedPreferencesStringValue("push_lib_account", value)
        get() = KUtil.getSharedPreferencesStringValue("push_lib_account", "")

    var tagOrTopics: Set<String>
        private set(value) = KUtil.putSharedPreferencesStringSet("push_lib_tags_or_topics", value)
        get() = KUtil.getSharedPreferencesStringSet("push_lib_tags_or_topics", setOf())

    var isSync: Boolean
        private set(value) = KUtil.putSharedPreferencesBooleanValue("push_lib_is_sync", value)
        get() = KUtil.getSharedPreferencesBooleanValue("push_lib_is_sync", false)

    /**
     * @param platformN 设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
     */
    fun syncDeviceInfo(tokenOrIdN: String, platformN: Int, accountN: String? = null, tagOrTopicsN: Set<String> = setOf()) {
        tokenOrId = tokenOrIdN
        platform = platformN
        account = accountN ?: ""
        tagOrTopics = tagOrTopicsN
        isSync = false

        curTaskName = System.currentTimeMillis().toString()
        syncDeviceInfo(curTaskName)
    }

    private var curTaskName: String? = null
    private fun syncDeviceInfo(taskName:String?) {
        if (curTaskName != taskName) return

        Thread(Runnable {
            try {
                val result = KWeb.request(registerUrl, mapOf(
                        "tokenOrId" to tokenOrId,
                        "platform" to platform,
                        "account" to account,
                        "tagOrTopics" to tagOrTopics.joinToString(",")
                ))

                val json = JSONObject(result)
                if (json.getInt("status") != 200) {
                    Thread.sleep(1000 * 30)
                    syncDeviceInfo(taskName)
                }

            } catch (e: Throwable) {
                KLog.e(e)
                Thread.sleep(1000 * 30)
                syncDeviceInfo(taskName)
            }
        }).start()
    }
}