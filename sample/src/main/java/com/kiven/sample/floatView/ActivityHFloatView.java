package com.kiven.sample.floatView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.sample.R;

/**
 * 悬浮框
 * <p>
 * 学习文档：TODO http://blog.csdn.net/stevenhu_223/article/details/8504058
 * <p>
 * Created by kiven on 2016/10/31.
 */

public class ActivityHFloatView extends KActivityHelper {
    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        setContentView(R.layout.activity_h_float_view);
    }

    FloatView activityFloatView;

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.item_activity_float:
                if (activityFloatView == null) {
                    activityFloatView = new FloatView(mActivity, mActivity.getWindowManager());
                }
                if (activityFloatView.isShow) {
                    activityFloatView.hideFloat();
                } else {
                    activityFloatView.showFloat();
                }
                break;
            case R.id.item_application_float:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(mActivity)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        mActivity.startActivity(intent);
                    } else {
                        startAppOutFloat();
                    }
                } else {
                    startAppOutFloat();
                }
                break;
        }
    }

    boolean isShow = false;

    public void startAppOutFloat() {
        Intent intent = new Intent(mActivity, ServiceFloat.class);
        if (isShow) {
            mActivity.stopService(intent);
        } else {
            mActivity.startService(intent);
        }
        isShow = !isShow;
    }

    @Override
    public void onPause() {
        if (activityFloatView != null && activityFloatView.isShow) {
            activityFloatView.hideFloat();
        }
        super.onPause();
    }
}
