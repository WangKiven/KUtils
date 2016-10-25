package com.kiven.kutils.tools;

import android.app.Activity;
import android.app.Application;

import com.kiven.kutils.logHelper.KLog;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 *
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
        x.Ext.init(this);
        x.Ext.setDebug(KLog.isDebug());
    }


    //-----------------------------TODO-------------------------------
    /**
     * 记录当前activity
     */
    private List<Activity> showActivities = new ArrayList<>();
    public void onActivityResume(Activity activity) {
        if (activity == null) {
            return;
        }

        if (isContainActivity(showActivities, activity)) {
            showActivities.remove(activity);
        }
        showActivities.add(activity);
    }

    public void onActivityPause(Activity activity) {
        if (activity == null) {
            return;
        }

        if (isContainActivity(showActivities, activity)) {
            showActivities.remove(activity);
        }
    }

    /**
     * app是否在前台
     * @return
     */
    public boolean isOnForeground() {
        return showActivities.size() == 0;
    }
    //-----------------------------TODO-------------------------------


    private boolean isContainActivity(List<Activity> activities, Activity activity) {
        for (Activity a : activities) {
            if (a == activity) {
                return  true;
            }
        }
        return false;
    }
}
