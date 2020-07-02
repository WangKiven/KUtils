package com.kiven.pushlibrary

import android.app.Service
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KContext
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * context.startService(Intent(context, PushService::class.java))
 * https://blog.csdn.net/weixin_39460667/article/details/82770164
 * http://blog.sina.com.cn/s/blog_3fe961ae0100xhsl.html
 */
class PushService : Service() {
    val notiManager:NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        KLog.i("PushService-onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        KLog.i("PushService-onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        KLog.i("PushService-onStartCommand")

        intent?.apply {
            val u = getStringExtra("url") ?: ""
            // PushService 有可能不在主线程，所以一些需要用到的参数，需要传过来。
            PushUtil.platform = getIntExtra("platform", 0)
//            KLog.i("xxxxxxxxxxxxx ${PushUtil.platform} ${KContext.getInstance().processName_}")

            if (u != url) {
                url = u
                Thread {
                    try {
                        mSocket?.close(1000, null)//Code must be in range [1000,5000)
                    } catch (e: Throwable) {
                        KLog.e(e)
                    }
                    mSocket = null
                    Thread.sleep(3000)
                    try {
                        connWebSocket()
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }.start()
            }
        }

        return START_REDELIVER_INTENT
    }

    private var mSocket: WebSocket? = null

    private var url: String = ""

    @Synchronized
    private fun connWebSocket() {

        val curUrl = url
        if (curUrl.isBlank()) {
            stopSelf()
            return
        }

        // TODO vivo 远程推送的channel,本地也可以使用。远程推送时创建的channel默认是开启的, 本地创建的默认是关闭的。
        //  所以等待远程推送并创建好channel后再，使用webSocket推送
        if (PushUtil.platform == 3 && Build.VERSION.SDK_INT >= 26) {
            val notiChannel = notiManager.getNotificationChannel(PushUtil.getChannelId(this))
            if (notiChannel == null) {
                KLog.i("notiChannel == null")
                Thread {
                    Thread.sleep(1000 * 60)
                    if (curUrl == url)
                        connWebSocket()
                }.start()
                return
            }
        }

        val mOkHttpClient = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS) //设置读取超时时间
            .writeTimeout(3, TimeUnit.SECONDS) //设置写的超时时间
            .connectTimeout(3, TimeUnit.SECONDS) //设置连接超时时间
            .hostnameVerifier(HostnameVerifier { _, _ -> true }) // todo websocket使用wss时需要这个配置
            .build()


        val request: Request = Request.Builder()
            .url(curUrl)
            .build()

        val socketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                if (curUrl == url) mSocket = webSocket
                else webSocket.close(1000, null)
                KLog.i("webSocket 连接成功")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                KLog.i("webSocket 收到bytes消息 $bytes")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                KLog.i("webSocket 收到text消息 $text")

                try {
                    val jsonObject = JSONObject(text)
                    jsonObject.optString("path").also { path ->
                        if (path == "push/notification") {
                            jsonObject.optJSONObject("data")?.also { dataObj ->
                                val title = dataObj.optString("title")
                                val subTitle = dataObj.optString("subTitle")
                                val argument = dataObj.optString("argument")
                                val sArgument = dataObj.optString("sArgument")


                                val importance = PushUtil.notification(this@PushService, title, subTitle, argument, sArgument)
                                val isShow = notiManager.areNotificationsEnabled() && importance > 0 // 未知的情况就当做显示了

                                KLog.i("notiManager.areNotificationsEnabled() = ${notiManager.areNotificationsEnabled()}, importance = $importance")

                                val temporaryKey = jsonObject.optString("temporaryKey")
                                if (temporaryKey.isNotEmpty()) {
                                    webSocket.send("{\"path\":\"push/notification_received\",\"data\":\"$temporaryKey\",\"isShow\":$isShow}")
                                }
                            }

                        }
                    }
                } catch (e: Throwable) {
                    KLog.e(e)
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                mSocket = null
                KLog.i("webSocket 已关闭")

                reConnect()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                mSocket = null
                KLog.i("webSocket 正在关闭")

                reConnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                mSocket = null
                KLog.e(t)
                KLog.i("webSocket 连接异常")

                reConnect()
            }

            fun reConnect() {
                if (curUrl == url)
                    Thread {
                        Thread.sleep(1000 * 60)
                        connWebSocket()
                    }.start()
            }
        }

        mOkHttpClient.newWebSocket(request, socketListener)
        mOkHttpClient.dispatcher.executorService.shutdown()
    }

    override fun onLowMemory() {
        KLog.i("PushService-onLowMemory")
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        KLog.i("PushService-onTrimMemory-$level")
        ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
        super.onTrimMemory(level)
    }

    override fun onRebind(intent: Intent?) {
        KLog.i("PushService-onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        KLog.i("PushService-onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        KLog.i("PushService-onDestroy")
        super.onDestroy()
    }
}