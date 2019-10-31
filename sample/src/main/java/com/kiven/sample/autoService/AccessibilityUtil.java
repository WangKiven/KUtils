package com.kiven.sample.autoService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KString;

import java.util.List;

public class AccessibilityUtil {
    private static final String TAG = "KUtils-sample";

    /**
     * 检查系统设置：是否开启辅助服务
     *
     * @param service 辅助服务
     */
    private static boolean isSettingOpen(Class service, Context cxt) {
        try {
            int enable = Settings.Secure.getInt(cxt.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
            if (enable != 1)
                return false;
            String services = Settings.Secure.getString(cxt.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (!TextUtils.isEmpty(services)) {
                TextUtils.SimpleStringSplitter split = new TextUtils.SimpleStringSplitter(':');
                split.setString(services);
                while (split.hasNext()) { // 遍历所有已开启的辅助服务名
                    if (split.next().equalsIgnoreCase(cxt.getPackageName() + "/" + service.getName()))
                        return true;
                }
            }
        } catch (Throwable e) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            Log.e(TAG, "isSettingOpen: " + e.getMessage());
        }
        return false;
    }

    /**
     * 跳转到系统设置：开启辅助服务
     */
    public static void jumpToSetting(final Context cxt) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        intent.setData(Uri.parse("package:" + cxt.getPackageName()));

        try {
            cxt.startActivity(intent);
        } catch (Throwable e) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cxt.startActivity(intent);
            } catch (Throwable e2) {
                Log.e(TAG, "jumpToSetting: " + e2.getMessage());
            }
        }
    }


    /**
     * 打印当前结构
     */
    public static void printTree(AccessibilityNodeInfo nodeInfo) {
        StringBuilder sb = new StringBuilder("windowId:").append(nodeInfo.getWindowId()).append("\n");
        getTree(nodeInfo, sb, 0);

        KLog.i(sb.toString());
    }

    private static void getTree(AccessibilityNodeInfo nodeInfo, StringBuilder tree, int deep) {
        if (nodeInfo == null) return;

        int childCount = nodeInfo.getChildCount();
        boolean visible = nodeInfo.isVisibleToUser();
        boolean clickable = nodeInfo.isClickable();

        tree.append(getDeepHeader(deep))
                .append(nodeInfo.getWindowId()).append(" ")
                .append(nodeInfo.getClassName());
        if (TextUtils.equals(nodeInfo.getClassName(), "android.widget.TextView") ||
                TextUtils.equals(nodeInfo.getClassName(), "android.widget.Button")
        ) {
            tree.append("(").append(nodeInfo.getText()).append(")");
        }
        if (TextUtils.equals(nodeInfo.getClassName(), "android.widget.EditText")) {
            tree.append("(text:").append(nodeInfo.getText()).append(", hint:");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tree.append(nodeInfo.getHintText());
            }
            tree.append(")");
        }

        if (childCount > 0) {
            tree.append("(childCount:").append(childCount).append(")");
        }
        if (!visible) {
            tree.append("(visible:").append(false).append(")");
        }
        if (clickable) {
            tree.append("(clickable:").append(true).append(")");
        }

        if (nodeInfo.isSelected()) {
            tree.append("(selected:").append(true).append(")");
        }

        if (nodeInfo.isChecked()) {
            tree.append("(checked:").append(true).append(")");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (nodeInfo.getViewIdResourceName() != null)
                tree.append("(resourceId:").append(nodeInfo.getViewIdResourceName()).append(")");
        }

        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        tree.append("(boundsInScreen:").append(rect.toString()).append(")");

        tree.append("\n");

        if (visible)
            for (int i = 0; i < childCount; i++) {
                getTree(nodeInfo.getChild(i), tree, deep + 1);
            }
    }

    private static String getDeepHeader(int deep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deep; i++) {
            sb.append("-");
        }
        sb.append(deep).append(" ");
        return sb.toString();
    }


    // TODO 查找安装,并模拟点击(findAccessibilityNodeInfosByText判断逻辑是contains而非equals)


    public static void findTxtClick(@NonNull AccessibilityNodeInfo nodeInfo, @NonNull String txt, @Nullable String souceId) {
        List<AccessibilityNodeInfo> nodes;
        if (KString.isBlank(souceId)) {
            nodes = nodeInfo.findAccessibilityNodeInfosByText(txt);
        } else {
            nodes = nodeInfo.findAccessibilityNodeInfosByViewId(souceId);
        }

        if (nodes == null || nodes.isEmpty())
            return;

        for (AccessibilityNodeInfo ni : nodes) {
            if (TextUtils.equals(ni.getText(), txt)) {
                KLog.i("click: " + ni);
                clickNode(ni, true);
            }
        }
    }

    public static void findTxtClick(AccessibilityNodeInfo nodeInfo, String txt) {
        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(txt);
        if (nodes == null || nodes.isEmpty())
            return;


        for (AccessibilityNodeInfo ni : nodes) {
            KLog.i("findTxtClick: " + txt + ", " + nodes.size() + ", " + ni);

            ni.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

//        KLog.i("findTxtClick: " + txt + ", " + nodes.size() + ", " + nodes);
        /*for (AccessibilityNodeInfo node : nodes) {
            if (node.isEnabled() && node.isClickable() && (node.getClassName().equals("android.widget.Button")
                    || node.getClassName().equals("android.widget.CheckBox") // 兼容华为安装界面的复选框
            )) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }*/
    }

    public static void findNodeClickById(@NonNull AccessibilityNodeInfo nodeInfo, @NonNull String souceId) {
        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId(souceId);
        if (nodes != null && nodes.size() > 0) {
            for (AccessibilityNodeInfo ni : nodes) {
                clickNode(ni, true);
            }
        }
    }

    /**
     * 点击组件
     *
     * @param checkParent 该按钮不可点击的话，向上还是向下检索并点击可点击控件，true:向上（即检测父控件） false:向下（即检测子控件, 没有的话就不检测）
     * @return true 点击成功，false: 没找到点击
     */
    public static boolean clickNode(AccessibilityNodeInfo nodeInfo, boolean checkParent) {

        if (checkParent) {
            AccessibilityNodeInfo checkNode = nodeInfo;

            while (checkNode != null) {
                if (checkNode.isClickable()) {
                    checkNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
                checkNode = checkNode.getParent();
            }
        } else {
            int length = nodeInfo.getChildCount();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    if (clickNode(nodeInfo.getChild(i), false)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void clickNode(AccessibilityNodeInfo nodeInfo) {
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    /**
     * 是否有文案是txt的组件，文案必须一样
     */
    public static boolean hasTxt(AccessibilityNodeInfo nodeInfo, @NonNull String txt) {
        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(txt);
        if (nodes == null || nodes.isEmpty())
            return false;

        for (AccessibilityNodeInfo ni : nodes) {
            if (TextUtils.equals(ni.getText(), txt)) {
                return true;
            }
        }

        return false;
    }


    public static AccessibilityNodeInfo findTxtNode(@NonNull AccessibilityNodeInfo nodeInfo, @NonNull String txt, @Nullable String souceId) {
        List<AccessibilityNodeInfo> nodes;
        if (KString.isBlank(souceId)) {
            nodes = nodeInfo.findAccessibilityNodeInfosByText(txt);
        } else {
            nodes = nodeInfo.findAccessibilityNodeInfosByViewId(souceId);
        }

        if (nodes == null || nodes.isEmpty())
            return null;

        for (AccessibilityNodeInfo ni : nodes) {
            if (TextUtils.equals(ni.getText(), txt)) {
                return ni;
            }
        }

        return null;
    }
}
