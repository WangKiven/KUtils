package com.kiven.kutils.activityHelper.activity;

import android.app.Activity;

import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.kiven.kutils.tools.KString;

public class DebugEntity {
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
        this.text = text.replace("\n", "");
        this.callBack = callBack;
    }

    public void onClick(Activity context, View clickView) {
        if (callBack != null) {
            callBack.onClick(context, clickView, this);
        }
    }

    public boolean isIcon() {
        return isIcon;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(@DrawableRes int resId) {
        this.resId = resId;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public String getText() {
        if (KString.isBlank(text)) {
            return "";
        }
        return text.substring(0, 1);
    }

    protected String getOnlyKey() {
        return "DebugEntity_" + resId + "_" + text;
    }
}
