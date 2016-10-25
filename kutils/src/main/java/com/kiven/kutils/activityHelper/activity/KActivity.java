package com.kiven.kutils.activityHelper.activity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.logHelper.KShowLog;
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

        if (showLog())
            sensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KContext.getInstance().onActivityResume(this);

        if (showLog()) {
            showLogTime = System.currentTimeMillis();
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        KContext.getInstance().onActivityPause(this);

        if (showLog())
            sensorManager.unregisterListener(this);
    }

    protected boolean showLog() {
        return KLog.isDebug();
    }

    private long showLogTime;

    /**
     * Called when sensor values have changed.
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
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
                new KShowLog().startActivity(this);
                showLogTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
