package com.kiven.sample.floatView;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

/**
 *
 * Created by kiven on 2016/10/31.
 */

public class ServiceFloat extends Service {

    FloatView floatView;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        floatView = new FloatView(getApplication(), (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE));
        floatView.showFloat();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (floatView != null) {
            floatView.hideFloat();
        }
    }
}
