package com.kiven.sample.autoService;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KAppTool;
import com.kiven.kutils.tools.KString;
import com.kiven.kutils.util.ArrayMap;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.kiven.sample.autoService.WXConst.Page.ContactInfoUI;
import static com.kiven.sample.autoService.WXConst.Page.LauncherUI;
import static com.kiven.sample.autoService.WXConst.Page.MassSendHistoryUI;
import static com.kiven.sample.autoService.WXConst.Page.MassSendMsgUI;
import static com.kiven.sample.autoService.WXConst.Page.MassSendSelectContactUI;
import static com.kiven.sample.autoService.WXConst.Page.SettingsPluginsUI;
import static com.kiven.sample.autoService.WXConst.Page.settingUI;
import static com.kiven.sample.autoService.WXConst.Page.tongYongSettingUI;
import static com.kiven.sample.autoService.WXConst.logType;

public class WXShareTask implements AutoInstallService.AccessibilityTask {


    private final ArrayMap<String, AccessibilityStep> steps = new ArrayMap<>();

    // 收信人信息
    public boolean isSendAll = false;// 是否发送给所有好友

    public boolean isSendTags = false;// 是否发送给标签好友，true: 发送给标签好友，false: 发送给不是这些标签的好友
    public List<String> tagFriends; // 对应的标签，null: 不根据标签发送

    public ArrayList<String> sendFrends; //

    public String msgForSend = "买车就要省心宝，买车更要用省心宝，省心宝 你值得拥有，✌️";// 要发送的文案

    public WXShareTask() {
        steps.put("toMain", new AccessibilityStep() {
            @Override
            public boolean isThis(AccessibilityNodeInfo rootNode) {


                return false;
            }

            @Override
            public void deal(AccessibilityNodeInfo rootNode) {

            }
        });
    }

    @Override
    public void onAccessibilityEvent(@NotNull AccessibilityService service, AccessibilityEvent event) {
//        KLog.i("onAccessibilityEvent: " + (event == null ? "null" : event.getPackageName().toString()));
        if (event == null) return;


        AccessibilityNodeInfo eventNode = event.getSource();
        if (eventNode == null) return;

//        KLog.i("eventNode:" + eventNode);

        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow(); //当前窗口根节点
        if (rootNode == null) return;

        deal(event, rootNode);


        eventNode.recycle();
    }

    private String curWXUI;

    private void deal(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {


        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            KLog.i("点击：：：：：：：：：：：：：");
            AccessibilityUtil.printTree(event.getSource());
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            curWXUI = event.getClassName().toString();
        }

        if (curWXUI == null) return;

        // 放在 curWXUI 被记录之后
        switch (logType % 3) {
            case 0:
                KLog.i(String.format("%s %x %x", event.getClassName().toString(), event.getEventType(), event.getAction()));
                break;
            case 1:
                AccessibilityUtil.printTree(rootNode);
                break;
            case 2:
                return;
        }

        // step 1 : 微信主界面
        if (TextUtils.equals(curWXUI, LauncherUI)) {

            AccessibilityNodeInfo myNode = AccessibilityUtil.findTxtNode(rootNode, "我", "com.tencent.mm:id/djv");
            if (myNode != null) {

                AccessibilityNodeInfo settingNode = AccessibilityUtil.findTxtNode(rootNode, "设置", "android:id/title");

                if (settingNode == null) {
                    AccessibilityUtil.clickNode(myNode, true);
                } else {
                    AccessibilityUtil.clickNode(settingNode, true);
                }
            }

            return;
        }

        // step 2 : 设置界面
        if (TextUtils.equals(curWXUI, settingUI)) {
            /*AccessibilityNodeInfo tongyongNode = AccessibilityUtil.findTxtNode(rootNode, "通用", "android:id/title");
            if (tongyongNode == null){
                UtilsKt.showToast("未找到操作，请确认微信版本正确");
            }else {
                AccessibilityUtil.clickNode(tongyongNode, true);
            }*/

            AccessibilityUtil.findTxtClick(rootNode, "通用", "android:id/title");
            return;
        }

        // step 3 : 通用界面
        if (TextUtils.equals(curWXUI, tongYongSettingUI)) {
            AccessibilityUtil.findTxtClick(rootNode, "辅助功能", "android:id/title");
            return;
        }
        // step 4: 设置->通用->辅助功能 界面
        if (TextUtils.equals(curWXUI, SettingsPluginsUI)) {
            AccessibilityUtil.findTxtClick(rootNode, "群发助手", "android:id/title");
            return;
        }
        // step 5: 设置->通用->辅助功能->群发助手 界面
        if (TextUtils.equals(curWXUI, ContactInfoUI)) {
            AccessibilityNodeInfo startNode = AccessibilityUtil.findTxtNode(rootNode, "开始群发", "android:id/title");
            if (startNode == null) {
                // 如果没有开始群发按钮，那么就是没有开启，先开启功能
                AccessibilityUtil.findTxtClick(rootNode, "启用该功能", "android:id/title");
            } else {
                AccessibilityUtil.clickNode(startNode, true);
            }
            return;
        }
        // step 6: 设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面
        // 注意：发送信息完成之后会回到这个界面，这个界面就是群发的历史记录界面
        if (TextUtils.equals(curWXUI, MassSendHistoryUI)) {
            // 新建群发按钮
            AccessibilityUtil.findNodeClickById(rootNode, "com.tencent.mm:id/dhn");
            return;
        }
        // step 7: 设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面
        if (TextUtils.equals(curWXUI, MassSendSelectContactUI)) {
            // 全选
            if (isSendAll) {
                // 根据全选按钮判断，是否已经全选，全选时：按钮文字是'不选'，未全选时：按钮文字是'全选'
                List<AccessibilityNodeInfo> selAllNode = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/di6");
                if (selAllNode == null || selAllNode.size() == 0) return;

                boolean hasBtn = false;
                for (AccessibilityNodeInfo ni : selAllNode) {
                    if (TextUtils.equals("全选", ni.getText())) {
                        AccessibilityUtil.clickNode(ni);

                        hasBtn = true;
                        break;
                    } else if (TextUtils.equals("不选", ni.getText())) {
                        // 说明已全选，未考虑用户点击了某一项的情况，可能取消了某个人
                        hasBtn = true;
                        break;
                    }
                }
                // 未找到正确按钮，不操作
                if (!hasBtn) {
                    return;
                }
            } else {
                // 选择标签

                // 直接选择
            }

            // 选择完成 下一步, 走到这一步，说明已经选择完成，不考虑未选的情况，一个都未选择时，按钮是不能点击的
            AccessibilityUtil.findNodeClickById(rootNode, "com.tencent.mm:id/lm");

            return;
        }
        // step 8: 设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面->群发消息输入界面
        // 注意：这个界面点击发送后，回到'MassSendHistoryUI'界面
        if (TextUtils.equals(curWXUI, MassSendMsgUI)) {

            if (!KString.isBlank(msgForSend)){
                // 操作输入框
                AccessibilityNodeInfo editNode = AccessibilityUtil.findNodeById(rootNode,"com.tencent.mm:id/aqc");
                if (editNode == null || !TextUtils.equals(editNode.getClassName(), "android.widget.EditText")) return;

                if (!TextUtils.equals(editNode.getText(), msgForSend)){
                    // 设置输入类容
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, msgForSend);
                    editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                }
            }

            //

            // 操作图片视频

            return;
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

    public interface AccessibilityStep {
        boolean isThis(AccessibilityNodeInfo rootNode);

        void deal(AccessibilityNodeInfo rootNode);
    }

    @Override
    public void onServiceConnected(@NotNull AccessibilityService service) {
        KAppTool.startApp(service, "com.tencent.mm");
    }
}
