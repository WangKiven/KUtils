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
import com.kiven.kutils.logHelper.KLog;

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

        KUtil.setApp(this);
        // 拦截崩溃异常。
        KUncaughtExceptionHandler.getInstance().register();

        if (isMainProcess()) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
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
                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {
                    KLog.d(activity.toString() + " - onActivityPaused");
                    changeStatus(checkOrAdd(activity, ActivityStatus.PAUSED));
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
                }
            });

            initOnlyMainProcess();
        }

        init();

        // 再次拦截，防止被替换。
        // 注册两次的原因是，1 拦截所有异常 2 防止被替换
        KUncaughtExceptionHandler.getInstance().register();
    }

    protected void initOnlyMainProcess() {
    }

    protected void init() {
//        x.Ext.init(this);
//        x.Ext.setDebug(KLog.isDebug());
    }

    /**
     * 判断当前进程是否是主进程， 可用于防止多次调用 onCreate()
     */
    public boolean isMainProcess() {
        String packageName = getPackageName();

        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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
            return getProcessName();
        }

        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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

    /*public void onActivityCreate(Activity activity) {
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
    }*/

    public void onActivityFinish(Activity activity) {
        if (activity == null) {
            return;
        }

        changeStatus(checkOrAdd(activity, ActivityStatus.FINISHING));
    }

    /*public void onActivityPause(Activity activity) {
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
    }*/

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
