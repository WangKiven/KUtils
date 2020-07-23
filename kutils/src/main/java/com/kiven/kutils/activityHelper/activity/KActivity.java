package com.kiven.kutils.activityHelper.activity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KContext;

/**
 * 可显示日志父类Activit
 * Created by kiven on 16/5/6.
 */
public class KActivity extends AppCompatActivity implements SensorEventListener {

    protected SensorManager sensorManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KContext.getInstance().onActivityCreate(this);

        if (showLog()) {
            sensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            floatView = new DebugView(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        KContext.getInstance().onActivityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KContext.getInstance().onActivityResume(this);

        if (showLog()) {
            showLogTime = System.currentTimeMillis();
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            floatView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        KContext.getInstance().onActivityPause(this);

        if (showLog()) {
            sensorManager.unregisterListener(this);
            floatView.onPause();
        }
    }

    @Override
    public void finish() {
        super.finish();
        KContext.getInstance().onActivityFinish(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        KContext.getInstance().onActivityStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KContext.getInstance().onActivityDestory(this);
    }

    protected boolean showLog() {
        return KLog.isDebug();
    }

    private long showLogTime;
    private DebugView floatView;


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() - showLogTime < 1000) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // assign directions
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

//            KLog.i("(" + x + ", " + y + ", " + z + ")");

            float xx = Math.abs(x);
            float yy = Math.abs(y);
            float zz = Math.abs(z - 9.8f);

            double sens = Math.sqrt(xx * xx + yy * yy + zz * zz);

//            KLog.i("( " + sens + " )");

            if (sens > 15) {
//                KLog.i("showLog " + System.currentTimeMillis() + " " + event.timestamp);
                /*new KShowLog().startActivity(this);*/
                showDebugView();

                showLogTime = System.currentTimeMillis();
            }
        }
    }

    protected void showDebugView() {
        if (floatView != null) {
            floatView.showFloat();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // for fix https://stackoverflow.com/questions/41025200/android-view-inflateexception-error-inflating-class-android-webkit-webview
    @Override
    public AssetManager getAssets() {
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            return getResources().getAssets();
        } else
            return super.getAssets();
    }
}
