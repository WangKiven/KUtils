package com.kiven.sample.push

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KUtil
import com.kiven.pushlibrary.PushClient
import com.kiven.pushlibrary.PushUtil
import com.kiven.sample.noti.AHNotiTest
import okhttp3.*
import okio.ByteString
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * Created by oukobayashi on 2019-12-31.
 * 小米推送文档：https://dev.mi.com/console/doc/detail?pId=41
 *
 * 华为推送文档：
 * https://developer.huawei.com/consumer/cn/doc/development/HMS-Library/push-sdk-integrate
 * https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/push-Preparations
 *
 * 极光推送：https://docs.jiguang.cn/jpush/client/Android/android_guide/
 *
 * iOS推送：https://github.com/notnoop/java-apns
 *
 * OPPO推送：https://open.oppomobile.com/wiki/doc#id=10196
 *
 * vivo推送：https://dev.vivo.com.cn/documentCenter/doc/233
 */
class AHSxbPush : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

        val addTitle = fun(text: String): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
            return tv
        }

        val addView = fun(text: String, click: View.OnClickListener): Button {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
            return btn
        }







        addTitle("封装库测试")
        addTitle("")
        addTitle("projectKey = ${PushClient.projectKey}")
        addTitle("")

        flexboxLayout.addView(EditText(activity).apply {
            val spKey = "ah_sxb_push_host"
            PushClient.host = KUtil.getSharedPreferencesStringValue(spKey, PushClient.host)

            setText(PushClient.host)
            hint = "请输入主机地址及端口"
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    PushClient.host = s?.toString() ?: ""

                    KUtil.putSharedPreferencesStringValue(spKey, PushClient.host)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        })

        flexboxLayout.addView(Button(activity).apply {
            val spKey = "ah_sxb_push_is_https"
            PushClient.ishttps = KUtil.getSharedPreferencesBooleanValue(spKey, PushClient.ishttps)
            text = "当前使用http${if (PushClient.ishttps) "s" else ""}"
            setOnClickListener {
                PushClient.ishttps = !PushClient.ishttps
                KUtil.putSharedPreferencesBooleanValue(spKey, PushClient.ishttps)
                text = "当前使用http${if (PushClient.ishttps) "s" else ""}"
            }
        })

        addTitle("")

        addView("注册设备", View.OnClickListener {
            // 文档说小米手机不需要申请权限， 但测试还是出问题了，所已小米还是要权限
            // 权限只是小米推送需要
            if (PushClient.shouldRequestPermission(activity)) {
                KGranting.requestPermissions(mActivity, 3344, arrayOf(
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), arrayOf("识别码", "存储")) {
                    if (it) {
                        if (!PushClient.hasInit)
                            PushClient.initPush(mActivity)
                    }
                }
            } else {
                PushClient.initPush(mActivity)
            }
        })

        var account = "18780296428"
        flexboxLayout.addView(EditText(activity).apply {
            setText(account)
            hint = "请输入账号"
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    account = s?.toString() ?: ""
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        })
        addView("绑定账号", View.OnClickListener {
            PushClient.setAccount(mActivity, account)
        })


        var tag = "sea,dog"
        flexboxLayout.addView(EditText(activity).apply {
            setText(tag)
            hint = "请标签，英文逗号隔开多标签"
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    tag = s?.toString() ?: ""
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        })
        addView("设置标签", View.OnClickListener {
            PushClient.setTags(mActivity, tag.split(",").filter { it.isNotBlank() }.toSet())
        })


        addTitle("")
        addTitle("账号只能有一个，标签可以有多个。多个标签用','隔开，不要出现空格")


        addTitle("")
        addTitle("webSocket")

        val mSockets = mutableListOf<WebSocket>()
        var count = 0
        val newSocket = fun() {
            count++

            val tokenOrId = "xxxxxxid$count"

            val mOkHttpClient = OkHttpClient.Builder()
                    .readTimeout(3, TimeUnit.SECONDS) //设置读取超时时间
                    .writeTimeout(3, TimeUnit.SECONDS) //设置写的超时时间
                    .connectTimeout(3, TimeUnit.SECONDS) //设置连接超时时间
                    .hostnameVerifier(HostnameVerifier { p0, p1 -> true }) // todo websocket使用wss时需要这个配置
                    .build()

            val request: Request = Request.Builder()
                    .url("ws://192.168.101.107:8080/socket?projectKey=${Uri.encode("projectKey_sample")}&tokenOrId=${tokenOrId}")
                    /*.post(FormBody.Builder().apply {
                        mapOf(
                                "ie" to "UTF-8",
                                "wd" to "美女"
                        ).forEach { add(it.key, it.value) }
                    }.build())*/
                    .build()
            val socketListener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    optionList(mSockets, webSocket, true)
                    KLog.i("webSocket 连接成功 ${mSockets.size}")
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    KLog.i("webSocket 收到消息 $bytes")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    KLog.i("webSocket 收到消息 $text")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    optionList(mSockets, webSocket, false)
                    KLog.i("webSocket 已关闭 ${mSockets.size}")
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    optionList(mSockets, webSocket, false)
                    KLog.i("webSocket 正在关闭 ${mSockets.size}")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    optionList(mSockets, webSocket, false)
                    KLog.e(t)
                    KLog.i("webSocket 连接异常 ${mSockets.size}")
                }
            }

            mOkHttpClient.newWebSocket(request, socketListener)
            mOkHttpClient.dispatcher.executorService.shutdown()
        }
        addView("连接1个", View.OnClickListener {
            newSocket()
        })
        addView("连接10个", View.OnClickListener {
            repeat(10) { newSocket() }
        })
        addView("连接100个", View.OnClickListener {
            repeat(100) { newSocket() }
        })
        addView("连接1000个", View.OnClickListener {
            repeat(1000) { newSocket() }
        })

        var message = "Hello 大宝"
        flexboxLayout.addView(EditText(activity).apply {
            setText(message)
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    message = s?.toString() ?: ""
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        })
        addView("发送消息", View.OnClickListener {
            mSockets.forEach { it.send(message) }
        })
        addView("close", View.OnClickListener {
            //            mSockets.forEach { it.close(1000, null) }
            val nn = mutableListOf<WebSocket>()
            nn.addAll(mSockets)
            nn.forEach { it.close(1000, null) }
            mSockets.clear()
        })
        addView("cancel", View.OnClickListener {
            //            mSockets.forEach { it.cancel() }
            val nn = mutableListOf<WebSocket>()
            nn.addAll(mSockets)
            nn.forEach { it.cancel() }
            mSockets.clear()
        })
        addView("AHNotiTest", View.OnClickListener { AHNotiTest().startActivity(activity) })
        addView("notify", View.OnClickListener {
            PushUtil.notification(activity, "title", "subtitle", "argusss")
        })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
    }

    @Synchronized
    fun <T> optionList(list: MutableList<T>, i: T, isAdd: Boolean) {
        /*if (list.contains(i)) {
            list.remove(i)
        }*/
        if (isAdd) {
            list.add(i)
        } else {
            /*val it = list.iterator()
            if (it.hasNext() && it.next() == i) {
                it.remove()
            }*/
            list.remove(i)
        }
    }
}