package com.kiven.sample

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KNetwork
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.util.showSnack
import com.kiven.sample.util.showToast
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.support.v4.nestedScrollView
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * https://www.cnblogs.com/xujian2014/p/5072215.html
 */
class AHSocketTest:KActivityDebugHelper() {
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

        addTitle("- 广播分为：定向广播，本地广播" +
                "\n- 定向广播：将数据报包发送到本网络之外的特定网络的所有主机，然而，由于互联网上的大部分路由器都不转发定向广播消息，所以这里不深入介绍了" +
                "\n- 本地广播：将数据报包发送到本地网络的所有主机，IPv4的本地广播地址为“255.255.255.255”，路由器不会转发此广播" +
                "\n- 多播(组播): 一台主机向指定的一组主机发送数据报包. IP网络的组播一般通过组播IP地址来实现。组播IP地址就是D类IP地址，即224.0.0.0至239.255.255.255之间的IP地址" +
                "\n- https://www.cnblogs.com/xujian2014/p/5072215.html")

        addView("发送广播获取服务器IP", View.OnClickListener {
            GlobalScope.launch {
                val host = "255.255.255.255" //广播地址
                val port = 9999 //广播的目的端口


                try {
                    // port 是监听接口，接收数据时用到，发送数据时可以不放接口，如：val ds = DatagramSocket()
                    val ds = DatagramSocket(port)
                    // 发送部分
                    val adds: InetAddress = InetAddress.getByName(host)
                    val message = "test你好".toByteArray() //用于发送的字符串
                    val dp = DatagramPacket(message, message.size, adds, port)
                    ds.send(dp)

                    // 接收部分
                    val receiveBytes = ByteArray(512)
                    val receivePacket = DatagramPacket(receiveBytes, receiveBytes.size)
                    val localIp = KNetwork.getIPAddress()
                    // 需要排除接收到自己发送的数据
                    do {
                        ds.receive(receivePacket)
                    }while (receivePacket.address.hostAddress == localIp)

                    withContext(Main){
                        mActivity.showSnack(String(receivePacket.data, 0, receivePacket.length))
                    }


                    ds.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
    }
}