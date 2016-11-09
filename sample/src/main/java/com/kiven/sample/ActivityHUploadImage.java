package com.kiven.sample;

import android.os.Bundle;

import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;

/**
 * 上传图片
 * Created by kiven on 2016/11/3.
 */

public class ActivityHUploadImage extends KActivityHelper {
    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        setContentView(R.layout.activity_h_float_view);
    }
}
