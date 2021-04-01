package com.kiven.kutils.activityHelper.activity;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KUtil;

import java.util.ArrayList;
import java.util.List;

public class DebugConst {
    private final static String actionStartTypeKey = "kUtil_actionStartTypeKey";

    /**
     * 功能菜单呼出方式
     * @return  0：呼吸按钮和传感器，1：传感器，2：呼吸按钮
     */
    public static int getActionStartType() {
        return KUtil.getSharedPreferencesIntValue(actionStartTypeKey, 0);
    }

    public static void setActionStartType(int actionStartType) {
        KUtil.putSharedPreferencesIntValue(actionStartTypeKey, actionStartType);
    }

    final static String quickActionsKey = "kUtil_quickActionsKey";
    final static String shareDivStr = "\n";
    final static int maxQuickShow = 10;
    @NonNull
    static List<DebugEntity> getQuickActions() {
        String keyStr = KUtil.getSharedPreferencesStringValue(quickActionsKey, null);
        List<DebugEntity> quickActions = new ArrayList<>();
        if (!TextUtils.isEmpty(keyStr)) {
            String[] ss = keyStr.split(shareDivStr);
            if (ss != null && ss.length > 0) {
                for (String s: ss) {
                    if (s != null && !TextUtils.isEmpty(s)) {
                        for (DebugEntity entity: DebugView.customAction) {
                            if (s.equals(entity.getOnlyKey())) {
                                quickActions.add(entity);
                            }
                        }
                    }
                }
            }
        }
        return quickActions;
    }

    static void saveQuickActions(@NonNull List<DebugEntity> actions) {
        StringBuilder builder = new StringBuilder();
        if (actions.size() > 0) {
            int i = 0;
            for (DebugEntity action: actions) {
                if (i != 0) {
                    builder.append(shareDivStr);
                }
                builder.append(action.getOnlyKey());
                i++;
            }
        }
        KLog.i(builder.toString());
        KUtil.putSharedPreferencesStringValue(quickActionsKey, builder.toString());
    }
}
