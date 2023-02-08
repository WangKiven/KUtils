package com.kiven.sample.autoService.wechat

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ListView
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAppTool
import com.kiven.sample.R
import com.kiven.sample.autoService.AccessibilityUtil
import com.kiven.sample.autoService.AutoInstallService
import com.kiven.sample.autoService.wechat.WXConst.Page.ContactLabelManagerUI
import com.kiven.sample.autoService.wechat.WXConst.Page.LauncherUI
import com.kiven.sample.util.showToast

/**
 * Created by oukobayashi on 2019-10-31.
 */
class WXLoadTagTask : AutoInstallService.AccessibilityTask {

    private var isCompleted = false

    override fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent?) {
        if (event == null || isCompleted) return


        val eventNode = event.source ?: return

//        KLog.i("eventNode:" + eventNode);

        val rootNode = service.rootInActiveWindow ?: return //当前窗口根节点

        deal(service, event, rootNode)

        rootNode.recycle()
        eventNode.recycle()
    }

    private var curWXUI: String? = null
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


            /*val tags = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b94")
            if (tags == null || tags.isEmpty()) {
                showToast("你还没有标签呢，快创建几个吧")
                return
            }
            val unselTags = tags.filter { WXConst.frindsTags[it.text.toString()] == null }
            if (unselTags.isNotEmpty()) {
                unselTags.forEach {
                    val countNodes = it.parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b95")
                    val count = if (countNodes == null || countNodes.isEmpty())
                        0
                    else {
                        val text = countNodes.first().text
                        text.substring(1, text.length - 1).toInt()
                    }

                    WXConst.frindsTags.put(it.text.toString(), count)
                }

                KLog.i("已记录标签：${WXConst.frindsTags.keys.joinToString()}")

                // 滚动判断是否需要滚动：根据最后一行底部与列表底部位置判断
                val listViewNodes = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b98")
                if (listViewNodes != null && listViewNodes.isNotEmpty()) {
                    val listViewNode = listViewNodes.first()
                    val lastNode = listViewNode.getChild(listViewNode.childCount - 1)

                    val listRect = Rect()
                    listViewNode.getBoundsInScreen(listRect)

                    val lastNodeRect = Rect()
                    lastNode.getBoundsInScreen(lastNodeRect)

                    if (lastNodeRect.bottom >= listRect.bottom) {
                        // 超出列表，需要滚动
                        listViewNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                        return
                    }
                }

            }*/

            isCompleted = true

            val activityManager =  service.getSystemService(ACTIVITY_SERVICE) as ActivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val tasks = activityManager.appTasks
                tasks[0].moveToFront()
                KLog.i("xxxxxxxxxxxxxx:::::${tasks.size}")
            }else {
                showToast("标签获取完成，请回到【KUSample】")
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)


//                val tasks = activityManager.getRunningTasks(5)
//                activityManager.moveTaskToFront(tasks[0].id, 0)
            }
        }

        if (curWXUI != null && !isCompleted) showToast("请回到微信主界面，自动获取标签")
    }

    override fun onServiceConnected(service: AccessibilityService) {
        KAppTool.startApp(service, service.getString(R.string.auto_access_service_dist_package))
    }
}