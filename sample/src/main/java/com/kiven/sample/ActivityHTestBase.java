package com.kiven.sample;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.custom.BaseHelper;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;

import roboguice.inject.InjectView;

/**
 * Created by kiven on 16/5/6.
 */
public class ActivityHTestBase extends BaseHelper {
    @InjectView(R.id.button1) private Button button;

    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        setContentView(R.layout.activity_lauch);

        button.setText("RoboHelper Test");
    }

    boolean visible = true;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                break;
            case R.id.button2:
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.root), new Slide(Gravity.LEFT));
                visible = !visible;
                findViewById(R.id.button1).setVisibility(visible? View.VISIBLE: View.GONE);
                break;
        }
    }
}
