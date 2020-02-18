package com.kiven.sample.push

import android.Manifest
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
import com.kiven.pushlibrary.Web
import okhttp3.*
import okio.ByteString
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier


class AHSxbPush : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }







        addTitle("封装库测试")
        addTitle("")
        addTitle("projectKey = ${Web.projectKey}")
        addTitle("")

        flexboxLayout.addView(EditText(activity).apply {
            val spKey = "ah_sxb_push_http_pre"
            Web.httpPre = KUtil.getSharedPreferencesStringValue(spKey, Web.httpPre)

            setText(Web.httpPre)
            hint = "请输入账号"
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    Web.httpPre = s?.toString() ?: ""

                    KUtil.putSharedPreferencesStringValue(spKey, Web.httpPre)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        })

        addView("注册设备", View.OnClickListener {
            // 文档说小米手机不需要申请权限， 但测试还是出问题了，所已小米还是要权限
            // 权限只是小米推送需要
            KGranting.requestPermissions(mActivity, 3344, arrayOf(
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), arrayOf("识别码", "存储")) {
                if (it) {
                    PushClient.initPush(mActivity)
                }
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
        var mSocket: WebSocket? = null
        addView("连接", View.OnClickListener {

            val mOkHttpClient = OkHttpClient.Builder()
                    .readTimeout(3, TimeUnit.SECONDS) //设置读取超时时间
                    .writeTimeout(3, TimeUnit.SECONDS) //设置写的超时时间
                    .connectTimeout(3, TimeUnit.SECONDS) //设置连接超时时间
                    .hostnameVerifier(HostnameVerifier { p0, p1 -> true }) // todo websocket使用wss时需要这个配置
                    .build()

            val request: Request = Request.Builder()
                    .url("ws://192.168.101.105:8080/socket?ie=xxy&wd=hhhh")
                    /*.post(FormBody.Builder().apply {
                        mapOf(
                                "ie" to "UTF-8",
                                "wd" to "美女"
                        ).forEach { add(it.key, it.value) }
                    }.build())*/
                    .build()
            val socketListener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    mSocket = webSocket
                    KLog.i("webSocket 连接成功")
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    KLog.i("webSocket 收到消息 $bytes")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    KLog.i("webSocket 收到消息 $text")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    mSocket = null
                    KLog.i("webSocket 已关闭")
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    mSocket = null
                    KLog.i("webSocket 正在关闭")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    KLog.e(t)
                    KLog.i("webSocket 连接异常")
                }
            }

            // 刚进入界面，就开启心跳检测
            // 刚进入界面，就开启心跳检测
            /*GlobalScope.launch {
                repeat(Int.MAX_VALUE) {
                    mSocket?.send("大大，我来啦。。。")
                    delay(1000 * 60)
                }
            }*/

            mOkHttpClient.newWebSocket(request, socketListener)
            mOkHttpClient.dispatcher.executorService.shutdown()
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
            mSocket?.send(message)
        })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
    }
}