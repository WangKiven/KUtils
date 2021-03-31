package com.kiven.kutils.activityHelper.activity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

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

    private long showLogTime;
    private DebugView floatView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (showLog()) {
            sensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            floatView = new DebugView(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (showLog()) {
            showLogTime = System.currentTimeMillis();
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            floatView.showDropDownBar();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (showLog()) {
            sensorManager.unregisterListener(this);
            floatView.hideFloat();
        }
    }

    @Override
    public void finish() {
        super.finish();
        KContext.getInstance().onActivityFinish(this);
    }

    protected boolean showLog() {
        return KLog.isDebug();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() - showLogTime < 1000) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float xx = Math.abs(x);
            float yy = Math.abs(y);
            float zz = Math.abs(z - 9.8f);

            double sens = Math.sqrt(xx * xx + yy * yy + zz * zz);

            if (sens > 15) {
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
