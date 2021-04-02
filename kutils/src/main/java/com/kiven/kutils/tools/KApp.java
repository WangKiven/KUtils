package com.kiven.kutils.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.activity.DebugConst;
import com.kiven.kutils.activityHelper.activity.DebugView;
import com.kiven.kutils.logHelper.KLog;

import java.util.ArrayList;
import java.util.List;

public final class KApp {
    private static KApp instance;

    private final Application app;
    private KApp(@NonNull Application app) {
        this.app = app;
    }

    public static KApp getInstance(@NonNull Application app) {
        if (instance == null)
            instance = new KApp(app);
        return instance;
    }

    public Application getApp() {
        return app;
    }

    public void onCreate() {
        KUtil.setApp(app);

        if (isMainProcess()) {
            app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                    KLog.d(activity.toString() + " - onActivityCreated");
                    changeStatus(checkOrAdd(activity, ActivityStatus.CREATED));
                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                    KLog.d(activity.toString() + " - onActivityStarted");
                    changeStatus(checkOrAdd(activity, ActivityStatus.STARTED));
                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {
                    KLog.d(activity.toString() + " - onActivityResumed");
                    changeStatus(checkOrAdd(activity, ActivityStatus.RESUMED));

                    if (DebugConst.getActionStartType() != 1 && KLog.isDebug()) DebugView.addBreathBtn(activity);
                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {
                    KLog.d(activity.toString() + " - onActivityPaused");
                    changeStatus(checkOrAdd(activity, ActivityStatus.PAUSED));

                    DebugView.hideFloat(activity);
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                    KLog.d(activity.toString() + " - onActivityStopped");
                    changeStatus(checkOrAdd(activity, ActivityStatus.STOPED));
                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                    KLog.d(activity.toString() + " - onActivitySaveInstanceState");
                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {
                    KLog.d(activity.toString() + " - onActivityDestroyed");

                    ActivityInfo a = remove(activity);
                    if (a != null) {
                        a.status = ActivityStatus.DESTORIED;
                    }
                    changeStatus(a);

                    DebugView.onDestroy(activity);
                }
            });
        }
    }

    /**
     * 判断当前进程是否是主进程， 可用于防止多次调用 onCreate()
     */
    public boolean isMainProcess() {
        String packageName = app.getPackageName();

        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null)
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                    .getRunningAppProcesses()) {

                if (appProcess.pid == pid) {
                    return packageName.equals(appProcess.processName);
                }
            }

        return true;
    }

    public String getProcessName_() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return app.getProcessName();
        }

        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null)
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                    .getRunningAppProcesses()) {

                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        return null;
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

    public void onActivityFinish(Activity activity) {
        if (activity == null) {
            return;
        }

        changeStatus(checkOrAdd(activity, ActivityStatus.FINISHING));
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
        Context context = app;

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

    private synchronized void changeStatus(ActivityInfo activityInfo) {
        for (ActivityOnChangeStatusListener listener : activityOnChangeStatusListeners) {
            listener.onChange(activityInfo);
        }
    }

    /**
     * 添加监听
     */
    public synchronized void addActivityOnChangeStatusListener(ActivityOnChangeStatusListener listener) {
        if (listener != null && !activityOnChangeStatusListeners.contains(listener)) {
            activityOnChangeStatusListeners.add(listener);
        }
    }

    /**
     * 移除监听
     */
    public synchronized void removeActivityOnChangeStatusListener(ActivityOnChangeStatusListener listener) {
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
        app.startActivity(new Intent(app, aClass));
    }

    /**
     * 下沉启动activity。关闭所有activity，启动新的activity
     */
    public void startSinkActivity(KActivityHelper helper) {
        closeAllActivity();

        // 不加这句可能会蹦
        helper.getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        helper.startActivity(app);
    }

    @Nullable
    public Activity getTopActivity() {
        if (activities.size() > 0) {
            ActivityInfo activityInfo = activities.get(activities.size() - 1);
            if (activityInfo.status == ActivityStatus.RESUMED)
                return activityInfo.activity;
        }

        return null;
    }
}
