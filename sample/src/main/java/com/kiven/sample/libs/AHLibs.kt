package com.kiven.sample.libs

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.flyco.animation.FlipEnter.FlipRightEnter
import com.flyco.dialog.listener.OnBtnClickL
import com.flyco.dialog.widget.ActionSheetDialog
import com.flyco.dialog.widget.MaterialDialog
import com.flyco.dialog.widget.NormalDialog
import com.flyco.dialog.widget.NormalListDialog
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KToast
import com.kiven.sample.util.snackbar
import com.kiven.sample.xutils.db.AHDbDemo
import com.kiven.sample.xutils.net.AHNetDemo
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.server.AsyncHttpServer
import org.jetbrains.anko.support.v4.nestedScrollView
import java.net.Inet4Address
import java.net.NetworkInterface
import java.text.DateFormat
import java.util.*

/**
 * Created by wangk on 2018/3/27.
 */
class AHLibs : KActivityHelper() {
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

        val addBtn = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }

        addTitle("AndroidAsync")// https://github.com/koush/AndroidAsync
        addBtn("AndroidAsync 接收", View.OnClickListener {
            val server = AsyncHttpServer()
            server.websocket("/live", "my-protocal") { webSocket, request ->
                webSocket.setClosedCallback {
                    if (it == null) {
                        KToast.ToastMessage("websocket close")
                    } else {
                        KToast.ToastMessage("websocket close ,cause of error")
                    }
                }

                webSocket.setStringCallback {
                    mActivity.snackbar("接收到推送：$it")
                }
            }
            server.listen(5001)
        })
        addBtn("AndroidAsync 发送", View.OnClickListener {
            AsyncHttpClient.getDefaultInstance().websocket("ws://" + getIPAddress() + ":5001/live", "my-protocal") { ex, webSocket ->
                if (ex == null) {
                    webSocket.send("Hello World")
                } else
                    KLog.e(ex)
            }
        })
        addTitle("xUtil")
        addBtn("Net FrameWork", View.OnClickListener { AHNetDemo().startActivity(mActivity) })
        addBtn("数据库", View.OnClickListener { AHDbDemo().startActivity(mActivity) })

        addTitle("FlycoDialog")// https://github.com/H07000223/FlycoDialog_Master
        addBtn("NormalDialog", View.OnClickListener {
            val dialog = NormalDialog(mActivity).style(NormalDialog.STYLE_TWO).title("温馨提示").content("打开的NormalDialog")
                    .btnText("确定1", "取消1")
            dialog.setOnDismissListener { mActivity.snackbar("Dismiss") }
            dialog.setOnCancelListener { mActivity.snackbar("Cancel") }
            dialog.setOnBtnClickL(OnBtnClickL {
                mActivity.snackbar("click1")
            }, OnBtnClickL {
                mActivity.snackbar("click2")
            })
            dialog.show()
        })
        addBtn("MaterialDialog", View.OnClickListener {
            val dialog = MaterialDialog(mActivity).title("温馨提示").content("打开的MaterialDialog")
                    .btnNum(3).btnText("忽略", "确定1", "取消1")
            dialog.setOnDismissListener { mActivity.snackbar("Dismiss") }
            dialog.setOnCancelListener { mActivity.snackbar("Cancel") }
            dialog.setOnBtnClickL(OnBtnClickL { mActivity.snackbar("click1") }
                    , OnBtnClickL { mActivity.snackbar("click2") }, OnBtnClickL { mActivity.snackbar("click3") })
            dialog.show()
        })
        addBtn("NormalListDialog", View.OnClickListener {
            val dialog = NormalListDialog(mActivity, arrayOf("收藏", "打包", "下载", "删除")).apply {
                setOnOperItemClickL { parent, view, position, id -> mActivity.snackbar("click$position");dismiss() }
                title("请选择")
            }
            dialog.show()
        })
        addBtn("ActionSheetDialog", View.OnClickListener {
            val dialog = ActionSheetDialog(mActivity, arrayOf("收藏", "打包", "下载", "删除"), it)
                    .cancelText("取消吗？？？").title("选吧！！！")
            dialog.setOnOperItemClickL { parent, view, position, id -> mActivity.snackbar("click$position");dialog.dismiss() }
            dialog.setOnCancelListener { mActivity.snackbar("Cancel") }
            dialog.show()

        })
        addBtn("BubblePopup", View.OnClickListener {
            val pop = SimpleCustomPop(mActivity)
            pop.anchorView(it)
            pop.gravity(Gravity.BOTTOM)
            pop.showAnim(FlipRightEnter())
            pop.show()
        })

        // https://developer.android.google.cn/training/volley/simple.html
        // https://github.com/google/volley
        addTitle("volley")
        addBtn("volley", View.OnClickListener {
            var queue: RequestQueue? = null
            val volley = fun (http: String) {
                if (queue == null) {
                    queue = Volley.newRequestQueue(mActivity)
                }
                val request = StringRequest(Request.Method.GET, http, Response.Listener { Log.i("ULog_default", http + DateFormat.getTimeInstance().format(Date())) }, Response.ErrorListener { Log.i("ULog_default", http + DateFormat.getTimeInstance().format(Date())) })
                queue!!.add(request)
            }

            volley("https://github.com/google/volley")
            volley("http://blog.csdn.net/linmiansheng/article/details/21646753")
        })
        addBtn("", View.OnClickListener { })
        addBtn("", View.OnClickListener { })
        addBtn("", View.OnClickListener { })
    }

    private fun getIPAddress(): String? {

        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    //这里需要注意：这里增加了一个限定条件( inetAddress instanceof Inet4Address ),主要是在Android4.0高版本中可能优先得到的是IPv6的地址
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: Exception) {
            KLog.e(ex)
        }

        return null
    }
}