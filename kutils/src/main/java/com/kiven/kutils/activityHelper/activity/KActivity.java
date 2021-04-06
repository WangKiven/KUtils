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
import com.kiven.kutils.tools.KAppHelper;
import com.kiven.kutils.tools.KContext;

/**
 * 可显示日志父类Activit
 * Created by kiven on 16/5/6.
 */
public class KActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        KAppHelper.getInstance().onActivityFinish(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
