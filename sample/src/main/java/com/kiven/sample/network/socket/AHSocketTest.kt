package com.kiven.sample.network.socket

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KNetwork
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.util.*
import com.sxb.kutils_ktx.util.RxBus
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.MulticastSocket
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * https://www.cnblogs.com/xujian2014/p/5072215.html
 */
class AHSocketTest : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START
        flexboxLayout.fitsSystemWindows = true

        setContentView(NestedScrollView(activity).apply {
            addView(flexboxLayout)
        })

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

        addTitle("- 广播分为：定向广播，本地广播" +
                "\n- 定向广播：将数据报包发送到本网络之外的特定网络的所有主机，然而，由于互联网上的大部分路由器都不转发定向广播消息，所以这里不深入介绍了" +
                "\n- 本地广播：将数据报包发送到本地网络的所有主机，IPv4的本地广播地址为“255.255.255.255”，路由器不会转发此广播" +
                "\n- 多播(组播): 一台主机向指定的一组主机发送数据报包. IP网络的组播一般通过组播IP地址来实现。组播IP地址就是D类IP地址，即224.0.0.0至239.255.255.255之间的IP地址" +
                "\n- https://www.cnblogs.com/xujian2014/p/5072215.html" +
                "\n- 本机ip：${KNetwork.getIPAddress()}")


        addView("发送广播获取服务器IP", View.OnClickListener {
            GlobalScope.launch(IO) {
                val host = "255.255.255.255" //广播地址
                val port = 9999 //广播的目的端口

                // port 是监听接口，接收数据时用到，发送数据时可以不放接口，如：val ds = DatagramSocket()
                val ds = DatagramSocket(port)
                try {
                    // 发送部分
                    val adds: InetAddress = InetAddress.getByName(host)
                    val message = Datagram(0, "KUtils请求服务").toByteArray() //用于发送的字符串
                    val dp = DatagramPacket(message, message.size, adds, port)

                    ds.send(dp)

                    // 接收部分
                    val receiveBytes = ByteArray(512)
                    val receivePacket = DatagramPacket(receiveBytes, receiveBytes.size)
                    ds.soTimeout = 3000 // 3秒超时

                    // 需要排除接收到自己发送的数据
                    var data: Datagram?
                    val curTime = System.currentTimeMillis()
                    do {
                        ds.receive(receivePacket)
                        data = Datagram.parse(receivePacket)
                    } while (data?.type != 1 && System.currentTimeMillis() - 3000 < curTime)

                    withContext(Main) {
                        mActivity.showSnack(data.toString())
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    ds.close()
                }
            }
        })
        addView("组播得IP", View.OnClickListener {
            GlobalScope.launch(IO) {
                val maxLength = 1024
                val port = 8888

                try {
                    val ms = MulticastSocket(port)
                    ms.loopbackMode = false // 设置本MulticastSocket发送的数据报会被回送到自身
                    val address = InetAddress.getByName("239.0.0.255")

                    val message = Datagram(0, "KUtils请求服务").toByteArray()
                    ms.send(DatagramPacket(message, message.size, address, port))


                    val dp = DatagramPacket(ByteArray(maxLength), maxLength)

                    ms.timeToLive = 32 //
                    ms.soTimeout = 3000 //等待时间
                    ms.joinGroup(address)// Mac上不行，似乎是ipv4、ipv6的问题, 需要用下面那个方法，不过在安卓上目前可用该方法
//                    ms.joinGroup(InetSocketAddress(address, port),
//                            NetworkInterface.getByInetAddress(address))

//                    ms.receive(dp)
                    // 需要排除接收到自己发送的数据
                    var data: Datagram?
                    val curTime = System.currentTimeMillis()
                    do {
                        ms.receive(dp)
                        data = Datagram.parse(dp)
                    } while (data?.type != 1 && System.currentTimeMillis() - 3000 < curTime)

                    withContext(Main) {
                        mActivity.showSnack("收到数据包（${dp.address?.hostAddress}:${dp.port}）：$data")
                    }

                    ms.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        })

        val serverSocketPortKey = "AHSocketTest_serverSocketPortKey"
        var serverSocketPort = KUtil.getSharedPreferencesIntValue(serverSocketPortKey, 9999)

        addView("ServerSocket", View.OnClickListener {
            activity.getInput("设置监听端口", serverSocketPort.toString(), EditorInfo.TYPE_CLASS_NUMBER) {
                if (it.isBlank()) return@getInput activity.showSnack("不能为空")
                serverSocketPort = it.toString().toInt()
                KUtil.putSharedPreferencesIntValue(serverSocketPortKey, serverSocketPort)

                SocketFactory.createServiceSocket(serverSocketPort)
            }
        })

        val socket2IpKey = "AHSocketTest_socket2IpKey"
        val socket2PortKey = "AHSocketTest_socket2PortKey"
        var socket2Ip = KUtil.getSharedPreferencesStringValue(socket2IpKey, "192.168.0.145")
        var socket2Port = KUtil.getSharedPreferencesIntValue(socket2PortKey, 9999)
        addView("Socket", View.OnClickListener {
            GlobalScope.launch(Dispatchers.Main){
                var ip = ""
                suspendCoroutine<Boolean> { continuation ->
                    activity.getInput("输入服务器IP", socket2Ip, onCancel = {
                        continuation.resume(false)
                    }) {
                        if (it.isBlank()) {
                            activity.showSnack("不能为空")
                        } else {
                            ip = it.toString()
                        }

                        continuation.resume(false)
                    }
                }

                if (ip.isBlank()) return@launch
                socket2Ip = ip
                KUtil.putSharedPreferencesStringValue(socket2IpKey, socket2Ip)

                var port = 0
                suspendCoroutine<Unit> {continuation ->
                    activity.getInput("输入服务器端口", socket2Port.toString(), EditorInfo.TYPE_CLASS_NUMBER, {
                        continuation.resume(Unit)
                    }) {
                        if (it.isBlank()) {
                            activity.showSnack("不能为空")
                        } else {
                            port = it.toString().toInt()
                        }
                        continuation.resume(Unit)
                    }
                }

                if (port == 0) return@launch
                socket2Port = port
                KUtil.putSharedPreferencesIntValue(socket2PortKey, socket2Port)

                var messageType = 0
                suspendCoroutine<Unit> {continuation ->
                    activity.showBottomSheetDialog(arrayOf("text", "image"), {continuation.resume(Unit)}) { index, _ ->
                        messageType = index + 1
                        continuation.resume(Unit)
                    }
                }

                when(messageType) {
                    1 -> {
                        var text = ""
                        suspendCoroutine<Unit> { continuation ->
                            activity.getInput("发送内容", "你若安好，便是晴天。", onCancel = {continuation.resume(Unit)}) {
                                if (it.isBlank()) activity.showSnack("不能为空")
                                text = it.toString()
                                continuation.resume(Unit)
                            }
                        }

                        if (text.isBlank()) return@launch

                        withContext(Dispatchers.IO) {
                            SocketFactory.sendSocketMessage(socket2Ip, socket2Port, SocketFactory.DataType.String, text)
                        }
                    }
                    2 -> {
                        var uri: Uri? = null
                        suspendCoroutine<Unit> { continuation ->
                            activity.randomPhoneImage({
                                activity.showSnack(it)
                                continuation.resume(Unit)
                            }) {
                                uri = it
                                continuation.resume(Unit)
                            }
                        }


                        withContext(Dispatchers.IO) {
                            uri?.apply {
                                SocketFactory.sendSocketMessage(socket2Ip, socket2Port, SocketFactory.DataType.Image, this)
                            }
                        }
                    }
                }
            }
        })

        RxBus.register<Message>(activity, SocketFactory.newAccept) {
            GlobalScope.launch(Dispatchers.Main) {
                when(it.dataType) {
                    SocketFactory.DataType.String -> activity.showDialog("收到来自${it.fromIp}的消息：${it.data}")
                    SocketFactory.DataType.Image -> activity.showImageDialog((it.data as File).absolutePath)
                    SocketFactory.DataType.File -> activity.showDialog("收到来自${it.fromIp}的文件消息，文件路径${it.data}")
                }
            }
        }
    }

    class Datagram(
            val type: Int = 0,// 0:发送的请求，1：返回的结果, -1：不明
            val text: String = ""// 请求内容
    ) {
        companion object {
            fun parse(dp: DatagramPacket): Datagram {
                return parse(dp.data, dp.length)
            }

            fun parse(data: ByteArray, length: Int): Datagram {
                val content = String(data, 0, length)
                var type: Int = -1
                var text: String = ""

                if (content.isNotEmpty()) {
                    type = content.substring(0, 1).toIntOrNull() ?: -1
                    if (type == -1) {
                        text = content
                    } else {
                        if (content.length > 1) {
                            text = content.substring(1)
                        }
                    }
                }
                text.toByteArray()
                return Datagram(type, text)
            }
        }


        override fun toString(): String {
            return "${type}${text}"
        }

        fun toByteArray(): ByteArray {
            return toString().toByteArray()
        }
    }
}