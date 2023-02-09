package com.kiven.sample.autoService.wechat

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ListView
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAppTool
import com.kiven.sample.R
import com.kiven.sample.autoService.AccessibilityUtil
import com.kiven.sample.autoService.AutoInstallService
import com.kiven.sample.autoService.AutoTaskInterface
import com.kiven.sample.autoService.wechat.WXConst.Page.ContactLabelManagerUI
import com.kiven.sample.autoService.wechat.WXConst.Page.LauncherUI
import com.kiven.sample.floatView.FloatView
import com.kiven.sample.util.showToast

/**
 * Created by oukobayashi on 2019-10-31.
 */
class WXLoadTagTask : AutoTaskInterface {
    private var mService: AutoInstallService? = null

    private var floatView: FloatView? = null

    override var isClose: Boolean = false

    private var curWXUI: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (isClose) return
        val service = mService ?: return


        val eventNode = event.source ?: return

//        KLog.i("eventNode:" + eventNode);

        val rootNode = service.rootInActiveWindow ?: return //当前窗口根节点

        deal(service, event, rootNode)

        rootNode.recycle()
        eventNode.recycle()
    }

    override fun registerService(service: AutoInstallService) {
        mService = service

        floatView = FloatView(
            service,
            service.getSystemService(Context.WINDOW_SERVICE) as WindowManager,
            "回",
            true
        ) {
            KAppTool.startApp(service, service.packageName)
        }
        floatView?.showFloat()
    }

    override fun close() {
        if (isClose) return

        floatView?.hideFloat()
    }

    override fun pause() {
    }
    private fun deal(service: AccessibilityService, event: AccessibilityEvent, rootNode: AccessibilityNodeInfo) {

        // 拦截滚动和点击
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED || event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
//            KLog.i(String.format("点击或滚动：：：：：%s %x %x", event.className.toString(), event.eventType, event.action))
            return
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            curWXUI = event.className.toString()
        }

        if (curWXUI == null) return

        // step 1 : 微信主界面
        if (TextUtils.equals(curWXUI, LauncherUI)) {
            // 查找通讯录选项，id要变，不能用ID
//            val myNode = AccessibilityUtil.findTxtNode(rootNode, "通讯录", "com.tencent.mm:id/djv")

            var myNode: AccessibilityNodeInfo? = null

            val wxpp = AccessibilityUtil.findNodeByClass(rootNode, "com.tencent.mm.ui.mogic.WxViewPager")
                    ?: return
            val rootBound = Rect()
            wxpp.getBoundsInScreen(rootBound)

            val mns = rootNode.findAccessibilityNodeInfosByText("通讯录")
            if (mns != null) {
                val cr = Rect()
                for (ni in mns) {
                    if (ni.text == "通讯录") {
                        ni.parent.getBoundsInScreen(cr)
                        if (rootBound.bottom - cr.bottom < 2) {
                            myNode = ni
                            break
                        }
                    }
                }
            }

            /*if (rootNode.childCount < 1) return

            val childCount = rootNode.getChild(0).childCount
            var n = 0
            for (i in 0 until childCount) {
                val cd = rootNode.getChild(0).getChild(i)

                if (TextUtils.equals(cd.className, "android.widget.RelativeLayout")) {
                    n++

                    if (n == 2) {
                        myNode = cd
                        break
                    }
                }
            }*/

            if (myNode == null) return


            val settingNode = AccessibilityUtil.findTxtNode(rootNode, "标签")

            if (settingNode == null) {
                AccessibilityUtil.clickNode(myNode, true)
            } else {
                AccessibilityUtil.clickNode(settingNode, true)

                settingNode.recycle()
            }
            myNode.recycle()

            return
        }

        // step 2 : 标签列表界面
        if (TextUtils.equals(curWXUI, ContactLabelManagerUI)) {
            val listView = AccessibilityUtil.findNodeByClass(rootNode, ListView::class.java.name)

            if (listView == null || listView.childCount < 1) {
                showToast("你还没有标签呢，快创建几个吧")
                return
            }


            val childCount = listView.childCount
            for ( i in 0 until childCount) {
                val tag = listView.getChild(i).getChild(0).text

                val countTxt = listView.getChild(i).getChild(1).text
                val count = countTxt.substring(1, countTxt.length - 1).toInt()

                WXConst.frindsTags[tag.toString()] = count
            }


            if (!AccessibilityUtil.checkListViewByTextView(listView)){
                listView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                return
            }

            isClose = true

            val activityManager =  service.getSystemService(ACTIVITY_SERVICE) as ActivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val tasks = activityManager.appTasks
                tasks[0].moveToFront()
            }else {
                showToast("标签获取完成，请回到【KUSample】")
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
            }
        }

        if (curWXUI != null && !isClose) showToast("请回到微信主界面，自动获取标签")
    }

    /*override fun onServiceConnected(service: AccessibilityService) {
        KAppTool.startApp(service, service.getString(R.string.auto_access_service_dist_package))
    }*/
}