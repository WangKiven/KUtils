package com.kiven.sample.autoService.wechat

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.*
import com.kiven.kutils.tools.KAppTool
import com.kiven.kutils.tools.KString
import com.kiven.sample.R
import com.kiven.sample.autoService.AccessibilityUtil
import com.kiven.sample.autoService.AutoInstallService
import com.kiven.sample.autoService.wechat.WXConst.Page.*
import com.kiven.sample.util.showToast
import kotlinx.coroutines.*

class WXShareTask(
        // 收信人信息
        val isSendAll: Boolean = false,// 是否发送给所有好友
        val isSendTags: Boolean = false,// 是否发送给标签好友，true: 发送给标签好友，false: 发送给不是这些标签的好友
        val tagForFriends: List<String>? = null, // 对应的标签，null: 不根据标签发送
        val msgForSend: String = "今天是个好日子，✌️",// 要发送的文案

        val mediaCount: Int = 0 // 要发送的图片数量
) : AutoInstallService.AccessibilityTask {

    // 用作记录用户选择的标签包含哪些好友
    private val tagAndFriends = mutableMapOf<String, MutableList<String>>()

//    var sendFrends: ArrayList<String>? = null //

    // 已发送图片数量
    private var hasSendMediaCount = 0

    // 当前所在的界面
    private var curWXUI: String? = null


    // 当前步骤
    // 1 开始进入发送进程（群发历史及之后的界面）
    // 2 发送文案完成
    // 3 所有操作完成（因为先发送文案，所以发送图片完成就完成所有操作了）
    private var curState = 0


    private var service: AccessibilityService? = null
    //    var events = mutableListOf<Pair<String, Int>>()
    private var lashChangeTime = 0L

    init {
        GlobalScope.launch {

            while (true) {
                delay(500)

                runBlocking(Dispatchers.Main) {
                    if (System.currentTimeMillis() - lashChangeTime > 500) {
                        service?.rootInActiveWindow?.apply {

                            deal(this)

                            recycle()
                        }
                    }
                }
            }
        }
    }

    override fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent?) {
        if (event == null) return

        val eventNode = event.source ?: return

        this.service = service


        lashChangeTime = event.eventTime

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            curWXUI = event.className.toString()
        }
        // 有点系统的微信略过了 MassSendMsgUI 的通知，这里需要手动检测
        service.rootInActiveWindow?.apply {
            if (curWXUI == MassSendSelectContactUI || curWXUI == SelectLabelContactUI) {
                val titile = AccessibilityUtil.findNodeById(this, "android:id/text1")
                if (titile != null && TextUtils.equals(titile.text, "群发")) {
                    val tts = findAccessibilityNodeInfosByText("你将发消息")
                    if (tts != null && tts.isNotEmpty()) {
                        curWXUI = MassSendMsgUI
                    }
                }
            }
        }

        eventNode.recycle()
    }

    private suspend fun deal(/*event: AccessibilityEvent, */rootNode: AccessibilityNodeInfo) {
        // 拦截滚动和点击
        /*if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED || event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
//            KLog.i("点击或滚动：：：：：：：：：：：：：" + event.source.className)
            return
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            curWXUI = event.className.toString()
        }

        // 有点系统的微信略过了 MassSendMsgUI 的通知，这里需要手动检测
        if (curWXUI == MassSendSelectContactUI || curWXUI == SelectLabelContactUI) {
            val titile = AccessibilityUtil.findNodeById(rootNode, "android:id/text1")
            if (titile != null && TextUtils.equals(titile.text, "群发")) {
                val tts = rootNode.findAccessibilityNodeInfosByText("你将发消息")
                if (tts != null && tts.isNotEmpty()) {
                    curWXUI = MassSendMsgUI
                }
            }
        }*/

        if (curWXUI == null) return

        if (curState >= 3) {

            // 分享完成，清空缓存图片, 需要退出发送界面后再清空
            if (curWXUI != MassSendMsgUI)
                WXConst.clearShareWXTempDir(service!!)
            return
        }

        // step 1 : 微信主界面
        if (TextUtils.equals(curWXUI, LauncherUI)) {

            // ID变动过一次（7.0.8 变成了 com.tencent.mm:id/dkb），
            // 怕以后也变动，就用麻烦一点的方法验证一次. 验证控件是否靠近底部
//            val myNode = AccessibilityUtil.findTxtNode(rootNode, "我", "com.tencent.mm:id/djv")

            var myNode: AccessibilityNodeInfo? = null

            val wxpp = AccessibilityUtil.findNodeByClass(
                    rootNode,
                    "com.tencent.mm.ui.mogic.WxViewPager"
            )
                    ?: return
            val rootBound = Rect()
            wxpp.getBoundsInScreen(rootBound)

            val mns = rootNode.findAccessibilityNodeInfosByText("我")
            if (mns != null) {
                val cr = Rect()
                for (ni in mns) {
                    if (ni.text == "我") {
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
                    if (n == 4) {
                        myNode = cd
                        break
                    }
                }
            }*/


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
            // 新建群发按钮，id 有变动，不能用ID来获取。
//            AccessibilityUtil.findNodeClickById(rootNode, "com.tencent.mm:id/dhn")

            // android.widget.Button(新建群发)(clickable:true)(resourceId:com.tencent.mm:id/di5)(boundsInScreen:Rect(36, 677 - 684, 763))
            // 有记录和没有记录的情况下，'新建群发' 的ID是不一样的，位置也不一样，并且随着版本更新，可能也会变动
            if (curState < 1 || curState == 2) {
                if (AccessibilityUtil.findTxtClick(rootNode, "新建群发")) {
                    if (curState < 1)
                        curState = 1
                }


                // 如果是发送给标签好友，需要清空一次，否则进不了标签选择界面
                if (isSendTags)
                    tagAndFriends.clear()
            }

            if (TextUtils.equals(curWXUI, MassSendHistoryUI)) curWXUI = null

            return
        }

        // 在这里做拦截，防止用户一打开微信就在下边的界面里面
        if (curState < 1) {
            showToast("请回到微信主界面，自动开始群发操作")
            return
        }

        // step 7: 设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面
        if (TextUtils.equals(curWXUI, MassSendSelectContactUI)) {
            // 两个按钮：下一步，和 全选（不选）
            val buttons = AccessibilityUtil.findNodesByClass(rootNode, Button::class.java)

            if (buttons.size < 2) {
//                showToast("界面变动，未找到按钮，请手动操作")
                return
            }

            // 全选
            if (isSendAll) {
                // 根据全选按钮判断，是否已经全选，全选时：按钮文字是'不选'，未全选时：按钮文字是'全选'
//                val selAllNode = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/di6")
//                if (selAllNode == null || selAllNode.size == 0) return
                val selAllNode = buttons[1]

                if (TextUtils.equals("全选", selAllNode.text)) {
                    AccessibilityUtil.clickNode(selAllNode)
                } else if (TextUtils.equals("不选", selAllNode.text)) {
                    // 说明已全选，未考虑用户点击了某一项的情况，可能取消了某个人
                }
            } else {
                // 选择标签
                if (tagForFriends != null && tagForFriends!!.isNotEmpty()) {
                    // 获取还没记录的好友标签
                    val recorded = tagAndFriends.keys
                    val nextTags = tagForFriends!!.filter { !recorded.contains(it) }

                    if (nextTags.isNotEmpty()) {
                        // 标签面板, 点击收索框才会出现
                        val tagPanlNode =
                                AccessibilityUtil.findNodeByClass(rootNode, ViewGroup::class.java)

                        if (tagPanlNode == null) {
                            // 搜索框
                            val searchNode =
                                    AccessibilityUtil.findNodeByClass(rootNode, EditText::class.java)
                            if (searchNode != null) {
                                AccessibilityUtil.clickNode(searchNode)
                            }
                            // 没找到就不处理
                            return
                        } else {
                            val childCount = tagPanlNode.childCount
                            if (childCount > 0) {

                                // 获取所有标签
                                val allTag = mutableListOf<String>()
                                for (i in 0 until childCount) {
                                    allTag.add(tagPanlNode.getChild(i).text.toString())
                                }
                                // 下一个需要记录的标签
                                val next = allTag.firstOrNull { nextTags.contains(it) }
                                if (next == null) {
                                    showToast("标签【${nextTags.joinToString()}】不存在")
                                    // 剩下的标签不存在，记录为空列表，下一步
                                    nextTags.forEach { tagAndFriends[it] = mutableListOf() }
                                } else {
                                    AccessibilityUtil.findTxtClick(tagPanlNode, next)
                                    return
                                }

                            } else {
                                showToast("标签【${tagForFriends!!.joinToString()}】不存在")

                                // 一个人都没选，没法下一步
                                return
                            }
                        }
                    } else {

                        if (!isSendTags) {// 如果是发送给没有这些标签的好友，需要在这里去勾选
                            val listViewNode =
                                    AccessibilityUtil.findNodeByClass(rootNode, ListView::class.java)
                            if (listViewNode != null) {

                                // 检测是否滚动到底部
                                if (!AccessibilityUtil.checkListViewByTextView(listViewNode)) {
                                    // 获取好友结点（把分组结点也获取到了，但是不影响，也就不单独处理了）
//                                    val itemNodes = AccessibilityUtil.findNodesByClass(listViewNode, TextView::class.java)
                                    val itemNodes = AccessibilityUtil.findNodesByClass(
                                            listViewNode,
                                            RelativeLayout::class.java
                                    )
                                    // 挨个检测是否不在标签好友里面并点击不在标签的好友
                                    itemNodes.forEach {
                                        if (it.childCount > 1) {
                                            var contain = false
                                            for (ll in tagAndFriends.values) {
                                                for (l in ll) {
                                                    if (TextUtils.equals(l, it.getChild(0).text)) {
                                                        contain = true
                                                        break
                                                    }
                                                }
                                                if (contain) break
                                            }

                                            val hasChecked = it.getChild(1).isChecked
                                            if (contain) {
                                                if (hasChecked) AccessibilityUtil.clickNode(it)
                                            } else {
                                                if (!hasChecked) AccessibilityUtil.clickNode(it)
                                            }
                                        }

                                    }

                                    delay(500)
                                    listViewNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                                    delay(500)

                                    return
                                }
                            }
                        }
                    }
                    // 已经选择完成，下一步
                }
            }

            // 选择完成 下一步, 走到这一步，说明已经选择完成，不考虑未选的情况，一个都未选择时，按钮是不能点击的
//            AccessibilityUtil.findNodeClickById(rootNode, "com.tencent.mm:id/lm")
            AccessibilityUtil.clickNode(buttons[0])

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
            val lvn = AccessibilityUtil.findNodeByClass(rootNode, ListView::class.java)
            if (lvn != null) {

                // 保存标签并判断是否遍历完成
                if (!AccessibilityUtil.checkListViewByTextView(lvn, tagAndFriends[tag]!!)) {

                    if (isSendTags) {// 如果是发送给有这些标签的好友，就在这里勾选上
                        AccessibilityUtil.findNodesByClass(lvn, CheckBox::class.java.name).forEach {
                            if (!it.isChecked) {
                                AccessibilityUtil.clickNode(it, true)
                            }
                        }

                        delay(500)
                    }
                    lvn.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                } else {
                    if (isSendTags) {
                        // 发送标签点击确定
                        val okBtn =
                                AccessibilityUtil.findNodeByClass(rootNode, Button::class.java.name)
                        if (okBtn != null) AccessibilityUtil.clickNode(okBtn)
                    } else {
                        // 发送非标签，点击返回
                        AccessibilityUtil.findNodeClickByClass(
                                rootNode,
                                LinearLayout::class.java.name
                        )
                    }
                    // // TODO: 2019-11-05 将当前界面置空，否则出现两次回退。原因是 curWXUI = event.className.toString() 并不能及时反映当前界面
                    // // TODO: 2019-11-05 目前没找到方法获取当前界面，现在的方法有延次。
                    if (TextUtils.equals(curWXUI, SelectLabelContactUI)) curWXUI = null
                }
            }
            return
        }

        // step 8: 设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面->群发消息输入界面
        // 注意：这个界面点击发送后，回到'MassSendHistoryUI'界面
        if (TextUtils.equals(curWXUI, MassSendMsgUI)) {

            val editNode = AccessibilityUtil.findNodeByClass(
                    rootNode,
                    EditText::class.java.name
            )

            // 发送文案
            if (!KString.isBlank(msgForSend) && curState < 2) {
                // 操作输入框
//                val editNode = AccessibilityUtil.findNodeById(rootNode, "com.tencent.mm:id/aqc")
                if (editNode == null) return

                if (!TextUtils.equals(editNode.text, msgForSend)) {
                    // 设置输入类容
                    val arguments = Bundle()
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, msgForSend)
                    editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

                    return
                } else {
                    // 发送按钮
                    AccessibilityUtil.findNodesByClass(rootNode, Button::class.java.name)
                            .forEach {
                                if (it.text == "发送") {
                                    AccessibilityUtil.clickNode(it)
                                    curState = if (mediaCount < 1) 3 else 2
                                }
                            }

                    return
                }
            }

            // 发送图片视频
            if (mediaCount > 0 && mediaCount > hasSendMediaCount) {
                // 有文案，先清空文案
                if (editNode != null && !KString.isBlank(editNode.text)) {
                    val arguments = Bundle()
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "")
                    editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    return
                }


                val gridView =
                        AccessibilityUtil.findNodeByClass(rootNode, GridView::class.java.name)
                if (gridView != null) {
                    AccessibilityUtil.findTxtClick(gridView, "相册")
                }
            }


            return
        }
        // step 8-1: 相册选择
        if (TextUtils.equals(curWXUI, AlbumPreviewUI)) {
            val gridView = AccessibilityUtil.findNodeByClass(rootNode, GridView::class.java.name)
            if (gridView != null) {
                val childCount = gridView.childCount

                val curIndex = hasSendMediaCount

                if (curIndex + 1 <= childCount) {
                    AccessibilityUtil.clickNode(gridView.getChild(curIndex))


                    // 视频，是直接发送，不会进入剪切界面，所有在这里计数
                    // 但是现在不考虑视频，以后处理
                }
            }
        }
        // step 8-2: 图片剪切界面
        if (TextUtils.equals(curWXUI, CropImageNewUI)) {
            if (AccessibilityUtil.findTxtClick(rootNode, "完成")) {
                // 与上边相同的问题，不然导致一张图片两次计数
                if (TextUtils.equals(curWXUI, CropImageNewUI)) curWXUI = null

                hasSendMediaCount++

                if (hasSendMediaCount >= mediaCount) {
                    curState = 3 // 发送完毕
                }
            }
        }

        // step 9: 应该是回到了'MassSendHistoryUI'界面，该怎么处理呢
//        if (TextUtils.equals(curWXUI, "")) {
//        }
    }

    /*interface AccessibilityStep {
        fun isThis(rootNode: AccessibilityNodeInfo): Boolean

        fun deal(rootNode: AccessibilityNodeInfo)
    }*/

    override fun onServiceConnected(service: AccessibilityService) {
        KAppTool.startApp(service, service.getString(R.string.auto_access_service_dist_package))
    }
}
