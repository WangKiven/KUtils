package com.kiven.kutils.activityHelper;

import android.content.Intent;

public class ActivityResultInfo {
    public final int requestCode;
    public final int resultCode;
    public final Intent data;

    public ActivityResultInfo(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }
}
