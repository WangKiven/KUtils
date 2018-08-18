package com.kiven.kutils.activityHelper.activity;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.kiven.kutils.callBack.Consumer;
import com.kiven.kutils.tools.KString;

class DebugEntity {
    private boolean isIcon = true;
    @DrawableRes
    private int resId;
    private String text;

    private Consumer<Activity> callBack;

    public DebugEntity(@DrawableRes int resId, Consumer<Activity> callBack) {
        this.resId = resId;
        this.callBack = callBack;
    }

    public DebugEntity(@NonNull String text, Consumer<Activity> callBack) {
        this.isIcon = false;
        this.text = text;
        this.callBack = callBack;
    }

    public void onClick(Activity context) {
        if (callBack != null) {
            callBack.callBack(context);
        }
    }

    public boolean isIcon() {
        return isIcon;
    }

    public int getResId() {
        return resId;
    }

    public String getText() {
        if (KString.isBlank(text)) {
            return "";
        }
        return text.substring(0, 1);
    }
}
