package com.kiven.kutils.activityHelper.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import androidx.annotation.NonNull;

import com.kiven.kutils.callBack.CallBack;

public class KShakingListener implements SensorEventListener {
    private final CallBack onShaking;
    private long showLogTime = 0L;

    public KShakingListener(@NonNull CallBack onShaking) {
        this.onShaking = onShaking;
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
            float zz = Math.abs(z);

            double sens = Math.sqrt(xx * xx + yy * yy + zz * zz);

            if (sens > 25) {
                onShaking.callBack();

                showLogTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
