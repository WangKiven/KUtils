package com.kiven.pushlibrary

import com.kiven.kutils.logHelper.KLog
import com.sxb.kutils_ktx.util.KWeb
import org.json.JSONObject

object Web {
    const val httpPre = "http://192.168.0.108:8080/api/"
    private const val registerUrl = "${httpPre}open/push/register"
    private const val bindAccountUrl = "${httpPre}open/push/bindAccount"
    private const val setTagsUrl = "${httpPre}open/push/setTags"

    var tokenOrId: String = ""
        private set
    var account: String = ""
        private set
    var tagOrTopics: String = ""
        private set

    /**
     * @param platformN 设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
     */
    fun register(tokenOrIdN: String, platformN: Int) {
        if (tokenOrId == tokenOrIdN) {
            return
        }

        tokenOrId = tokenOrIdN

        registerTaskId = System.currentTimeMillis()
        register(registerTaskId, platformN)
    }

    private var registerTaskId = 0L
    private fun register(taskId: Long, platformN: Int) {
        if (registerTaskId != taskId) return

        Thread {
            try {
                val result = KWeb.request(registerUrl, mapOf(
                        "tokenOrId" to tokenOrId,
                        "platform" to platformN
                ))

                val json = JSONObject(result)
                if (json.getInt("status") != 200) {
                    Thread.sleep(1000 * 30)
                    register(taskId, platformN)
                }

            } catch (e: Throwable) {
                KLog.e(e)
                Thread.sleep(1000 * 30)
                register(taskId, platformN)
            }
        }.start()
    }

    fun bindAccount(accountN: String) {
        if (account == accountN) {
            return
        }
        account = accountN

        accountTaskId = System.currentTimeMillis()
        bindAccount(accountTaskId)
    }

    private var accountTaskId = 0L
    private fun bindAccount(taskId: Long) {
        if (accountTaskId != taskId) return

        Thread {
            while (tokenOrId.isBlank()) {
                Thread.sleep(10000)
            }

            try {
                val result = KWeb.request(bindAccountUrl, mapOf(
                        "tokenOrId" to tokenOrId,
                        "account" to account
                ))

                val json = JSONObject(result)
                if (json.getInt("status") != 200) {
                    Thread.sleep(1000 * 30)
                    bindAccount(taskId)
                }

            } catch (e: Throwable) {
                KLog.e(e)
                Thread.sleep(1000 * 30)
                bindAccount(taskId)
            }
        }.start()
    }


    fun setTags(tagOrTopicsN: String) {
        if (tagOrTopics == tagOrTopicsN) return
        tagOrTopics = tagOrTopicsN

        tagsTaskId = System.currentTimeMillis()
        setTags(tagsTaskId)
    }

    private var tagsTaskId = 0L
    private fun setTags(taskId: Long) {
        if (tagsTaskId != taskId) return

        Thread {
            while (tokenOrId.isBlank()) {
                Thread.sleep(10000)
            }

            try {
                val result = KWeb.request(setTagsUrl, mapOf(
                        "tokenOrId" to tokenOrId,
                        "tagOrTopics" to tagOrTopics
                ))

                val json = JSONObject(result)
                if (json.getInt("status") != 200) {
                    Thread.sleep(1000 * 30)
                    setTags(taskId)
                }

            } catch (e: Throwable) {
                KLog.e(e)
                Thread.sleep(1000 * 30)
                setTags(taskId)
            }
        }.start()
    }
}