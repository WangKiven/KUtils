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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.nestedScrollView
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


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

        addView("DatagramSocket.send", View.OnClickListener {
            GlobalScope.launch {
                //广播的实现 :由客户端发出广播，服务器端接收
                //广播的实现 :由客户端发出广播，服务器端接收
                val host = "255.255.255.255" //广播地址

                val port = 9999 //广播的目的端口

                val message = "test" //用于发送的字符串

                try {
                    val adds: InetAddress = InetAddress.getByName(host)
                    val ds = DatagramSocket()
                    val dp = DatagramPacket(message.toByteArray(), message.length, adds, port)
                    ds.send(dp)
                    ds.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}