package com.kiven.sample.autoService;

import android.accessibilityservice.AccessibilityService;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.github.mikephil.charting.utils.Utils;
import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.util.ArrayMap;
import com.kiven.sample.util.UtilsKt;

public class WXShareTask implements AutoInstallService.AccessibilityTask {
    public static int logType = 0;//控制打印日志, 微信工具用

    private final String LauncherUI = "com.tencent.mm.ui.LauncherUI";//微信界面
    private final String SnsTimeLineUI = "com.tencent.mm.plugin.sns.ui.SnsTimeLineUI";//朋友圈界面

    private final ArrayMap<String, AccessibilityStep> steps = new ArrayMap<>();

    WXShareTask() {
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
    public void onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
//        KLog.i("onAccessibilityEvent: " + (event == null ? "null" : event.getPackageName().toString()));
        if (event == null) return;


        AccessibilityNodeInfo eventNode = event.getSource();
        if (eventNode == null) return;

        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow(); //当前窗口根节点
        if (rootNode == null) return;

        deal(event, rootNode);


        eventNode.recycle();
    }

    private String curWXUI;
    private void deal(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {

        switch (logType %3){
            case 0:
                KLog.i(String.format("%s %x %x", event.getClassName().toString(), event.getEventType(), event.getAction()));
                break;
            case 1:
                AccessibilityUtil.printTree(rootNode);
                break;
        }


        // step 1 :
        if (TextUtils.equals(curWXUI, LauncherUI)){


            return;
        }



        switch (event.getEventType()) {
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
        }

    }

    public interface AccessibilityStep {
        boolean isThis(AccessibilityNodeInfo rootNode);

        void deal(AccessibilityNodeInfo rootNode);
    }
}
