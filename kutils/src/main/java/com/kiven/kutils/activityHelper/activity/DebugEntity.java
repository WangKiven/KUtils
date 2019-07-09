package com.kiven.kutils.activityHelper.activity;

import android.app.Activity;

import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.kiven.kutils.tools.KString;

class DebugEntity {
    private boolean isIcon = true;
    @DrawableRes
    private int resId;
    private String text;

    private DebugViewListener callBack;

    public DebugEntity(@DrawableRes int resId, DebugViewListener callBack) {
        this.resId = resId;
        this.callBack = callBack;
    }

    public DebugEntity(@NonNull String text, DebugViewListener callBack) {
        this.isIcon = false;
        this.text = text;
        this.callBack = callBack;
    }

    public void onClick(Activity context, View clickView) {
        if (callBack != null) {
            callBack.onClick(context, clickView);
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
