package com.kiven.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.custom.BaseHelper;

/**
 * Created by kiven on 16/5/6.
 */
public class ActivityHTestBase extends BaseHelper {

    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        setContentView(R.layout.activity_lauch);

        Button button = findViewById(R.id.button1);
        button.setText("RoboHelper Test");
    }

    boolean visible = true;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:

                /*AppContext.getInstance().startSinkActivity(new ActivityHFloatView());*/
                break;
            case R.id.button2:
//                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.root), new Slide(Gravity.LEFT));
                visible = !visible;
                findViewById(R.id.button1).setVisibility(visible ? View.VISIBLE : View.GONE);
                break;
        }
    }
}
