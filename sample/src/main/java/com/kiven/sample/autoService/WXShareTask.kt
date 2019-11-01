package com.kiven.sample.autoService

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAppTool
import com.kiven.kutils.tools.KString
import com.kiven.kutils.util.ArrayMap
import com.kiven.sample.autoService.WXConst.Page.*

import java.util.ArrayList

import com.kiven.sample.autoService.WXConst.logType
import com.kiven.sample.util.showToast

class WXShareTask : AutoInstallService.AccessibilityTask {


    private val steps = ArrayMap<String, AccessibilityStep>()

    // 收信人信息
    var isSendAll = false// 是否发送给所有好友

    var isSendTags = false// 是否发送给标签好友，true: 发送给标签好友，false: 发送给不是这些标签的好友
    var tagForFriends: List<String>? = null // 对应的标签，null: 不根据标签发送
    // 用作记录
    val tagAndFriends = mutableMapOf<String, MutableList<String>>()

    var sendFrends: ArrayList<String>? = null //

    var msgForSend = "买车就要省心宝，买车更要用省心宝，省心宝 你值得拥有，✌️"// 要发送的文案

    private var curWXUI: String? = null

    init {
        steps.put("toMain", object : AccessibilityStep {
            override fun isThis(rootNode: AccessibilityNodeInfo): Boolean {


                return false
            }

            override fun deal(rootNode: AccessibilityNodeInfo) {

            }
        })
    }

    override fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent?) {
        //        KLog.i("onAccessibilityEvent: " + (event == null ? "null" : event.getPackageName().toString()));
        if (event == null) return


        val eventNode = event.source ?: return

        //        KLog.i("eventNode:" + eventNode);

        val rootNode = service.rootInActiveWindow ?: return //当前窗口根节点

        deal(service, event, rootNode)

        rootNode.recycle()
        eventNode.recycle()
    }

    private fun deal(service: AccessibilityService, event: AccessibilityEvent, rootNode: AccessibilityNodeInfo) {

        // 拦截滚动和点击
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED || event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            KLog.i("点击或滚动：：：：：：：：：：：：：")
            AccessibilityUtil.printTree(event.source)
            return
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            curWXUI = event.className.toString()
        }

        if (curWXUI == null) return

        // 放在 curWXUI 被记录之后
        when (logType % 3) {
            0 -> KLog.i(String.format("%s %x %x", event.className.toString(), event.eventType, event.action))
            1 -> AccessibilityUtil.printTree(rootNode)
            2 -> return
        }

        // step 1 : 微信主界面
        if (TextUtils.equals(curWXUI, LauncherUI)) {

            val myNode = AccessibilityUtil.findTxtNode(rootNode, "我", "com.tencent.mm:id/djv")
            if (myNode != null) {

                val settingNode = AccessibilityUtil.findTxtNode(rootNode, "设置", "android:id/title")

                if (settingNode == null) {
                    AccessibilityUtil.clickNode(myNode, true)
                } else {
                    AccessibilityUtil.clickNode(settingNode, true)
                    settingNode.recycle()
                }

                myNode.recycle()
            }

            return
        }

        // step 2 : 设置界面
        if (TextUtils.equals(curWXUI, settingUI)) {
            /*AccessibilityNodeInfo tongyongNode = AccessibilityUtil.findTxtNode(rootNode, "通用", "android:id/title");
            if (tongyongNode == null){
                UtilsKt.showToast("未找到操作，请确认微信版本正确");
            }else {
                AccessibilityUtil.clickNode(tongyongNode, true);
            }*/

            AccessibilityUtil.findTxtClick(rootNode, "通用", "android:id/title")
            return
        }

        // step 3 : 通用界面
        if (TextUtils.equals(curWXUI, tongYongSettingUI)) {
            AccessibilityUtil.findTxtClick(rootNode, "辅助功能", "android:id/title")
            return
        }
        // step 4: 设置->通用->辅助功能 界面
        if (TextUtils.equals(curWXUI, SettingsPluginsUI)) {
            AccessibilityUtil.findTxtClick(rootNode, "群发助手", "android:id/title")
            return
        }
        // step 5: 设置->通用->辅助功能->群发助手 界面
        if (TextUtils.equals(curWXUI, ContactInfoUI)) {
            val startNode = AccessibilityUtil.findTxtNode(rootNode, "开始群发", "android:id/title")
            if (startNode == null) {
                // 如果没有开始群发按钮，那么就是没有开启，先开启功能
                AccessibilityUtil.findTxtClick(rootNode, "启用该功能", "android:id/title")
            } else {
                AccessibilityUtil.clickNode(startNode, true)
            }
            return
        }
        // step 6: 设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面
        // 注意：发送信息完成之后会回到这个界面，这个界面就是群发的历史记录界面
        if (TextUtils.equals(curWXUI, MassSendHistoryUI)) {
            // 新建群发按钮
            AccessibilityUtil.findNodeClickById(rootNode, "com.tencent.mm:id/dhn")
            return
        }
        // step 7: 设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面
        if (TextUtils.equals(curWXUI, MassSendSelectContactUI)) {
            // 全选
            if (isSendAll) {
                // 根据全选按钮判断，是否已经全选，全选时：按钮文字是'不选'，未全选时：按钮文字是'全选'
                val selAllNode = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/di6")
                if (selAllNode == null || selAllNode.size == 0) return

                var hasBtn = false
                for (ni in selAllNode) {
                    if (TextUtils.equals("全选", ni.text)) {
                        AccessibilityUtil.clickNode(ni)

                        hasBtn = true
                        break
                    } else if (TextUtils.equals("不选", ni.text)) {
                        // 说明已全选，未考虑用户点击了某一项的情况，可能取消了某个人
                        hasBtn = true
                        break
                    }
                }
                // 未找到正确按钮，不操作
                if (!hasBtn) {
                    return
                }
            } else {
                // 选择标签
                if (tagForFriends != null && tagForFriends!!.isNotEmpty()) {
                    // 标签面板, 点击收索框才会出现
                    val tagPanlNode = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/jx")
                    if (tagPanlNode == null || tagPanlNode.isEmpty()) {
                        // 搜索框
                        val searchNode = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bdl")
                        if (searchNode != null && searchNode.isNotEmpty()) {
                            AccessibilityUtil.clickNode(searchNode[0])
                        }
                        // 没找到就不处理
                        return
                    } else {
                        val childCount = tagPanlNode[0].childCount
                        if (childCount > 0) {

                            // 获取还没记录的好友标签
                            val recorded = tagAndFriends.keys
                            val nextTags = tagForFriends!!.filter { !recorded.contains(it) }
                            if (nextTags.isNotEmpty()) {
                                // 获取所有标签
                                val allTag = mutableListOf<String>()
                                for (i in 0 until childCount) {
                                    allTag.add(tagPanlNode[0].getChild(i).text.toString())
                                }
                                // 下一个需要记录的标签
                                val next = allTag.firstOrNull { nextTags.contains(it) }
                                if (next == null) {
                                    showToast("标签【${nextTags.joinToString()}】不存在")
                                    // 剩下的标签不存在，记录为空列表，下一步
                                    nextTags.forEach { tagAndFriends[it] = mutableListOf() }
                                } else {
                                    AccessibilityUtil.findTxtClick(tagPanlNode[0], next)
                                    return
                                }
                            }
                            // 已经选择完成，下一步
                        } else {
                            showToast("标签【${tagForFriends!!.joinToString()}】不存在")

                            // 一个人都没选，没法下一步
                            return
                        }
                    }
                }

                // 直接选择
            }

            // 选择完成 下一步, 走到这一步，说明已经选择完成，不考虑未选的情况，一个都未选择时，按钮是不能点击的
            AccessibilityUtil.findNodeClickById(rootNode, "com.tencent.mm:id/lm")

            return
        }

        // step 7-1:  设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面->点击搜索框弹出的标签进入的'按标签选择界面'
        // 在这个界面可以选择，但是如果之前已经选择了好友，在这里将不可取消选中，不过依然可以点击并有点击效果
        // 如果用户意图是不选这些标签，那么在这个界面只记录标签下有哪些好友
        if (TextUtils.equals(curWXUI, SelectLabelContactUI)) {
            // 获取当前标签
            val titleNode = rootNode.findAccessibilityNodeInfosByViewId("android:id/text1")
            if (titleNode == null || titleNode.isEmpty()) return
            val tag = titleNode[0].text.toString()

            if (!tagAndFriends.containsKey(tag)) {
                tagAndFriends[tag] = mutableListOf<String>()
            }
            // 获取标签下好友
            /*val listViewNode = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/s6")
            if (listViewNode == null || listViewNode.isEmpty()) return
            val tfs = listViewNode.map { it.text.toString() }//当前页的数据

            tagAndFriends[tag]!!.addAll(tfs)
            // 选中控件，目前不考虑指定标签不发送的情况
            listViewNode.forEach {
                AccessibilityUtil.clickNode(it, true)
            }
            // 滚动
            val lvn = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/js")
            if (lvn != null && lvn.isNotEmpty()) lvn.first().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)*/

            val lvn = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/js")
            if (lvn != null && lvn.isNotEmpty()) {

                val hasComplete = AccessibilityUtil.checkListView(lvn.first(), tagAndFriends[tag]!!, "com.tencent.mm:id/s6")
                lvn[0].findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a3h")?.forEach {
                    if (!it.isChecked) {
                        AccessibilityUtil.clickNode(it, true)
                    }
                }

                if (hasComplete) {
//                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    AccessibilityUtil.findNodeClickById(rootNode, "com.tencent.mm:id/lm")
                    KLog.i("AccessibilityService.GLOBAL_ACTION_BACK")
                } else {
                    lvn.first().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                }
            }
            return
        }

        // step 8: 设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面->群发消息输入界面
        // 注意：这个界面点击发送后，回到'MassSendHistoryUI'界面
        if (TextUtils.equals(curWXUI, MassSendMsgUI)) {

            if (!KString.isBlank(msgForSend)) {
                // 操作输入框
                val editNode = AccessibilityUtil.findNodeById(rootNode, "com.tencent.mm:id/aqc")
                if (editNode == null || !TextUtils.equals(editNode.className, "android.widget.EditText"))
                    return

                if (!TextUtils.equals(editNode.text, msgForSend)) {
                    // 设置输入类容
                    val arguments = Bundle()
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, msgForSend)
                    editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                }
            }

            //

            // 操作图片视频

            return
        }
        // step 9: 应该是回到了'MassSendHistoryUI'界面，该怎么处理呢
        if (TextUtils.equals(curWXUI, "")) {
        }

        /*switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                curWXUI = event.getClassName().toString();
                AccessibilityUtil.findTxtClick(rootNode, "我", "com.tencent.mm:id/djv");
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                break;
            default:
                break;
        }*/
    }

    interface AccessibilityStep {
        fun isThis(rootNode: AccessibilityNodeInfo): Boolean

        fun deal(rootNode: AccessibilityNodeInfo)
    }

    override fun onServiceConnected(service: AccessibilityService) {
        KAppTool.startApp(service, "com.tencent.mm")
    }
}
