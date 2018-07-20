package com.kiven.kutils.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiven.kutils.activityHelper.KActivityHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiven on 16/5/6.
 */
public class KContext extends Application {
    private static KContext mInstance;

    public static KContext getInstance() {
        return mInstance;
    }

    @Override
    public final void onCreate() {
        super.onCreate();

        mInstance = this;

        init();
    }

    protected void init() {
//        x.Ext.init(this);
//        x.Ext.setDebug(KLog.isDebug());
    }


    // TODO-------------------------------


    /**
     * 记录当前activity的状态
     */
    public enum ActivityStatus {
        UNKNOWN, CREATED, STARTED, RESUMED, PAUSED, FINISHING, STOPED, DESTORIED;
    }

    public class ActivityInfo {
        Activity activity;
        ActivityStatus status;

        public ActivityInfo(Activity activity) {
            this.activity = activity;
            status = ActivityStatus.UNKNOWN;
        }

        public Activity getActivity() {
            return activity;
        }

        public ActivityStatus getStatus() {
            return status;
        }
    }

    public interface ActivityOnChangeStatusListener {
        void onChange(ActivityInfo activityInfo);
    }

    public void onActivityCreate(Activity activity) {
        if (activity == null) {
            return;
        }

        changeStatus(checkOrAdd(activity, ActivityStatus.CREATED));
    }

    public void onActivityStart(Activity activity) {
        if (activity == null) {
            return;
        }

        changeStatus(checkOrAdd(activity, ActivityStatus.STARTED));
    }

    public void onActivityResume(Activity activity) {
        if (activity == null) {
            return;
        }

        changeStatus(checkOrAdd(activity, ActivityStatus.RESUMED));
    }

    public void onActivityFinish(Activity activity) {
        if (activity == null) {
            return;
        }

        changeStatus(checkOrAdd(activity, ActivityStatus.FINISHING));
    }

    public void onActivityPause(Activity activity) {
        if (activity == null) {
            return;
        }

        changeStatus(checkOrAdd(activity, ActivityStatus.PAUSED));
    }

    public void onActivityStop(Activity activity) {
        if (activity == null) {
            return;
        }

        changeStatus(checkOrAdd(activity, ActivityStatus.STOPED));
    }

    public void onActivityDestory(Activity activity) {
        if (activity == null) {
            return;
        }
        ActivityInfo a = remove(activity);
        if (a != null) {
            a.status = ActivityStatus.DESTORIED;
        }
        changeStatus(a);
    }

    /**
     *
     */
    public boolean isLocal() {
        for (ActivityInfo a : activities) {
            if (a.status == ActivityStatus.RESUMED) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param activity 是否是当前显示的activity
     */
    public boolean canShowDialog(Activity activity) {
        if (activity != null) {
            for (ActivityInfo a : activities) {
                if (a.activity == activity) {
                    return a.status == ActivityStatus.CREATED || a.status == ActivityStatus.STARTED || a.status == ActivityStatus.RESUMED;
                }
            }
        }
        return false;
    }

    /**
     * app是否在前台
     *
     * @return
     */
    public boolean isOnForeground() {
        System.out.println("=======获得当前正在运行的activity=========");
        Context context = this;

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());*/
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return false;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return true;
                }
            }
        }
        return isLocal();
    }
    // TODO---------------activity数组操作----------------


    private ActivityInfo checkOrAdd(@NonNull Activity activity, ActivityStatus status) {
        ActivityInfo a = checkOrAdd(activity);
        a.status = status;
        return a;
    }

    private ActivityInfo checkOrAdd(@NonNull Activity activity) {

        for (ActivityInfo a : activities) {
            if (a.activity == activity) {
                return a;
            }
        }

        ActivityInfo b = new ActivityInfo(activity);
        activities.add(b);
        return b;
    }

    private ActivityInfo remove(@NonNull Activity activity) {
        ActivityInfo b = null;
        for (ActivityInfo a : activities) {
            if (a.activity == activity) {
                b = a;
                break;
            }
        }

        if (b != null) {
            activities.remove(b);
        }

        return b;
    }


    // TODO------------ 监听activity状态改变 -------------------

    private List<ActivityInfo> activities = new ArrayList<>();
    private List<ActivityOnChangeStatusListener> activityOnChangeStatusListeners = new ArrayList<>();

    private void changeStatus(ActivityInfo activityInfo) {
        for (ActivityOnChangeStatusListener listener : activityOnChangeStatusListeners) {
            listener.onChange(activityInfo);
        }
    }

    /**
     * 添加监听
     */
    public void addActivityOnChangeStatusListener(ActivityOnChangeStatusListener listener) {
        if (listener != null && !activityOnChangeStatusListeners.contains(listener)) {
            activityOnChangeStatusListeners.add(listener);
        }
    }

    /**
     * 移除监听
     */
    public void removeActivityOnChangeStatusListener(ActivityOnChangeStatusListener listener) {
        if (listener != null && activityOnChangeStatusListeners.contains(listener)) {
            activityOnChangeStatusListeners.remove(listener);
        }
    }

    // TODO------------ 启动关闭activity -------------------

    /**
     * 关闭所有activity
     */
    public void closeAllActivity() {
        for (ActivityInfo a : activities) {
            a.activity.finish();
        }
    }

    /**
     * 下沉启动activity。关闭所有activity，启动新的activity
     */
    public void startSinkActivity(Class aClass) {
        closeAllActivity();
        startActivity(new Intent(this, aClass));
    }

    /**
     * 下沉启动activity。关闭所有activity，启动新的activity
     */
    public void startSinkActivity(KActivityHelper helper) {
        closeAllActivity();

        // 不加这句可能会蹦
        helper.getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        helper.startActivity(this);
    }
}
