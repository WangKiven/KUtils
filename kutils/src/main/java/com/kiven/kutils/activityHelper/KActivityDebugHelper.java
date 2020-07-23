package com.kiven.kutils.activityHelper;

/**
 * helper, 可显示日志
 * Created by kiven on 16/7/22.
 */
public class KActivityDebugHelper extends KActivityHelper/* implements SensorEventListener*/ {

    /*protected SensorManager sensorManager;

    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        if (showLog()) {
            sensorManager = (SensorManager) mActivity.getSystemService(Activity.SENSOR_SERVICE);
            floatView = new DebugView(activity);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (showLog()) {
            showLogTime = System.currentTimeMillis();
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            floatView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (showLog()) {
            sensorManager.unregisterListener(this);
            floatView.onPause();
        }
    }

    protected boolean showLog() {
        return KLog.isDebug();
    }

    private DebugView floatView;
    private long showLogTime;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() - showLogTime < 2000) {
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
                showDebugView();
                showLogTime = System.currentTimeMillis();
            }
        }
    }

    public void showDebugView() {
        if (floatView != null) {
            floatView.showFloat();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }*/
}
