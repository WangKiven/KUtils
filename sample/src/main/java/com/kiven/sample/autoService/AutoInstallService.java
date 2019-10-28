package com.kiven.sample.autoService;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.kiven.kutils.logHelper.KLog;
import com.kiven.sample.R;

import java.util.List;

/**
 * https://www.jianshu.com/p/04ebe2641290
 * https://blog.csdn.net/weimingjue/article/details/82744146
 */
public class AutoInstallService extends AccessibilityService {
    private static AutoInstallService mInstance;
    public static boolean isStarted() {
        return mInstance != null;
    }

    private static final int DELAY_PAGE = 320; // 页面切换时间
    private final Handler mHandler = new Handler();



    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || !event.getPackageName().toString()
                .contains(getString(R.string.auto_access_service_dist_package)))//不写完整包名，是因为某些手机(如小米)安装器包名是自定义的
            return;
        /*
        某些手机安装页事件返回节点有可能为null，无法获取安装按钮
        例如华为mate10安装页就会出现event.getSource()为null，所以取巧改变当前页面状态，重新获取节点。
        该方法在华为mate10上生效，但其它手机没有验证...(目前小米手机没有出现这个问题)
        */
        KLog.i("onAccessibilityEvent: " + event);
        AccessibilityNodeInfo eventNode = event.getSource();
        if (eventNode == null) {
            KLog.i("eventNode: null, 重新获取eventNode...");
            performGlobalAction(GLOBAL_ACTION_RECENTS); // 打开最近页面
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    performGlobalAction(GLOBAL_ACTION_BACK); // 返回安装页面
                }
            }, DELAY_PAGE);
            return;
        }

        /*
        模拟点击->自动安装，只验证了小米5s plus(MIUI 9.8.4.26)、小米Redmi 5A(MIUI 9.2)、华为mate 10
        其它品牌手机可能还要适配，适配最可恶的就是出现安装广告按钮，误点安装其它垃圾APP（典型就是小米安装后广告推荐按钮，华为安装开始官方安装）
        */
        AccessibilityNodeInfo rootNode = getRootInActiveWindow(); //当前窗口根节点
        if (rootNode == null)
            return;
        KLog.i( "rootNode: " + rootNode);
        if (isNotAD(rootNode))
            findTxtClick(rootNode, "安装"); //一起执行：安装->下一步->打开,以防意外漏掉节点
        findTxtClick(rootNode, "继续安装");
        findTxtClick(rootNode, "下一步");
        findTxtClick(rootNode, "打开");
        // 回收节点实例来重用
        eventNode.recycle();
        rootNode.recycle();
    }
    // 查找安装,并模拟点击(findAccessibilityNodeInfosByText判断逻辑是contains而非equals)
    private void findTxtClick(AccessibilityNodeInfo nodeInfo, String txt) {
        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(txt);
        if (nodes == null || nodes.isEmpty())
            return;
        KLog.i("findTxtClick: " + txt + ", " + nodes.size() + ", " + nodes);
        for (AccessibilityNodeInfo node : nodes) {
            if (node.isEnabled() && node.isClickable() && (node.getClassName().equals("android.widget.Button")
                    || node.getClassName().equals("android.widget.CheckBox") // 兼容华为安装界面的复选框
            )) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    // 排除广告[安装]按钮
    private boolean isNotAD(AccessibilityNodeInfo rootNode) {
        return isNotFind(rootNode, "还喜欢") //小米
                && isNotFind(rootNode, "官方安装"); //华为
    }

    private boolean isNotFind(AccessibilityNodeInfo rootNode, String txt) {
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(txt);
        return nodes == null || nodes.isEmpty();
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        KLog.i("onServiceConnected: ");

        mInstance = this;
    }

    @Override
    public void onInterrupt() {
        KLog.i("onInterrupt: ");

        performGlobalAction(GLOBAL_ACTION_BACK);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        }, DELAY_PAGE);

        mInstance = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.i("onDestroy: ");

        // 服务停止，重新进入系统设置界面
        AccessibilityUtil.jumpToSetting(this);

        mInstance = null;
    }
}
