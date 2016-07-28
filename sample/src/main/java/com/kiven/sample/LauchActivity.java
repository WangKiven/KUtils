package com.kiven.sample;

import android.os.Bundle;
import android.view.View;

import com.kiven.kutils.activityHelper.activity.KRoboActivity;
import com.kiven.kutils.logHelper.KLog;

import roboguice.RoboGuice;

public class LauchActivity extends KRoboActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch);
        RoboGuice.setUseAnnotationDatabases(false);
    }

    public void onClick(View view) {
        new ActivityHTestBase().startActivity(this);

        KLog.i("{\"name\":\"kiven\", \"hh\":[\"yy\", \"66\"]}");
    }
}
