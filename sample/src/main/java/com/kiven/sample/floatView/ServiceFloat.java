package com.kiven.sample.floatView;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by kiven on 2016/10/31.
 */

public class ServiceFloat extends Service {

    FloatView floatView;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        floatView = new FloatView(getBaseContext(), (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE), true);// y由于是后台服务，必须是应用外悬浮
        floatView.showFloat();
        printLog("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        printLog("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        printLog("onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        printLog("onDestroy");
        if (floatView != null) {
            floatView.hideFloat();
        }
    }

    public void printLog(String log) {
        Log.i("ULog_default", log);
    }
}
