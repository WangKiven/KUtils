package com.kiven.pushlibrary.vivo

import android.content.Context
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.PushHelper
import com.kiven.pushlibrary.Web
import com.vivo.push.PushClient

/**
 *
 * 集成说明：https://dev.vivo.com.cn/documentCenter/doc/233
 * api接口文档：https://dev.vivo.com.cn/documentCenter/doc/232
 *
 * 错误码参考: https://dev.vivo.com.cn/documentCenter/doc/226
 *
 * vivo 不能绑定账号
 */
class VivoPushHelper : PushHelper {
    // 是否初始化
    private var isInit = false

    fun turnOffPush(context: Context) {
        PushClient.getInstance(context).turnOffPush {
            if (it == 0 || it == 1) {
                KLog.i("操作成功")
            } else KLog.i("操作失败")
        }

        /*PushClient.getInstance(context).bindAlias("") {}
        PushClient.getInstance(context).unBindAlias("") {}

        PushClient.getInstance(context).setTopic(""){}
        PushClient.getInstance(context).delTopic("") {}

        PushClient.getInstance(context).topics*/
    }

    override fun initPush(context: Context) {
        PushClient.getInstance(context).apply {

            if (!isInit) {
                initialize()
                isInit = true
            }
            turnOnPush {
                if (it == 0 || it == 1) {
                    if (regId.isNotBlank())
                        Web.register(regId, 3)//设备类型 0 不明，1 iOS, 2 华为, 3 vivo, 4 oppo, 5 小米
                } else KLog.i("操作失败")
            }
        }
    }

    override fun setTags(context: Context, tags: Set<String>) {
        if (tags.isEmpty()) {
            clearTags(context)
            return
        }

        PushClient.getInstance(context).apply {
            val addTags = mutableSetOf<String>()
            val delTags = mutableSetOf<String>()
            val topics = topics

            if (topics == null || topics.isEmpty()) {
                addTags.addAll(tags)
            } else {
                topics.forEach {
                    if (!tags.contains(it)) delTags.add(it)
                }

                tags.forEach {
                    if (!topics.contains(it)) addTags.add(it)
                }
            }

            addTags.forEach { tag ->
                addTagTaskName = System.currentTimeMillis().toString()
                addTag(context, tag, addTagTaskName)
            }

            delTags.forEach { tag ->
                delTagTaskName = System.currentTimeMillis().toString()
                delTag(context, tag, delTagTaskName)
            }
        }
    }

    private var addTagTaskName = ""
    private fun addTag(context: Context, tag:String, taskName: String) {
        if (addTagTaskName != taskName) return

        PushClient.getInstance(context).setTopic(tag) {
            if (it == 20004) {//订阅次数太频繁或已订阅数过多
                Thread {
                    Thread.sleep(15000)
                    addTag(context, tag, taskName)
                }.start()
            }
            KLog.i("vivo setTopic($tag) 操作结果码 = $it")
        }
    }

    private fun clearTags(context: Context) {
        PushClient.getInstance(context).apply {
            topics?.forEach { tag ->
                delTagTaskName = System.currentTimeMillis().toString()
                delTag(context, tag, delTagTaskName)
            }
        }
    }

    private var delTagTaskName = ""
    private fun delTag(context: Context, tag: String, taskName: String) {
        if (delTagTaskName != taskName) return

        PushClient.getInstance(context).delTopic(tag) {
            if (it == 20004) {//取消主题次数太频繁
                Thread {
                    Thread.sleep(15000)
                    delTag(context, tag, taskName)
                }.start()
            }
            KLog.i("vivo delTopic($tag) 操作结果码 = $it")
        }
    }

    override fun setAccount(context: Context, account: String) {

    }

}