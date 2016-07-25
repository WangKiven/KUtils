package com.kiven.kutils.activityHelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Created by kiven on 15/11/5.
 */
public class KActivityHelper {

    protected KHelperActivity mActivity;

    public final void startActivity(Activity activity) {
        activity.startActivity(getIntent(activity));
    }

    public final void startActivity(Fragment fragment) {
        fragment.startActivity(getIntent(fragment.getActivity()));
    }

    public final void startActivity(Context context) {
        context.startActivity(getIntent(context));
    }

    public final void startActivityForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    public final void startActivityForResult(Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(fragment.getActivity()), requestCode);
    }
    //-------------------Intent-------------------
    /**
     * 不要直接调用
     */
    private Intent mIntent = null;

    public final Intent getIntent() {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        return mIntent;
    }

    private Intent getIntent(Context context) {
        Intent tIntent = getIntent();
        tIntent.setComponent(new ComponentName(context, KRoboHelperActivity.class));
        tIntent.putExtra("BaseActivityHelper", putStack(this));
        return tIntent;
    }

    public void addFlags(int flags) {
        getIntent().addFlags(flags);
    }
    //--------------------------------------------

    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        mActivity = activity;
        mIntent = activity.getIntent();
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onClick(View view) {
    }

    public void onStart() {
    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {
    }

    public void onDestroy() {

    }

    public void onWindowFocusChanged(boolean hasFocus) {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    public void finish() {
        mActivity.finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    protected final void setResult(int resultCode) {
        mActivity.setResult(resultCode);
    }

    protected final void setResult(int resultCode, Intent data) {
        mActivity.setResult(resultCode, data);
    }

    protected void setContentView(int layoutResID) {
        mActivity.setContentView(layoutResID);
    }

    protected void setContentView(View view) {
        mActivity.setContentView(view);
    }
    public View findViewById(int rId) {
        return mActivity.findViewById(rId);
    }

    /**
     * resources
     *
     * @return
     */
    protected Resources getResources() {
        return mActivity.getResources();
    }

    protected String getString(int resId) {
        return mActivity.getString(resId);
    }

    protected String getString(int resId, Object... formatArgs) {
        return mActivity.getString(resId, formatArgs);
    }

    protected String getArrayString(int rId, int count) {
        if (count >= 0) {
            String[] strings = getResources().getStringArray(rId);
            if (count < strings.length) {
                return strings[count];
            }
        }
        return "";
    }

    protected Drawable getDrawable(int resId) {
        return getResources().getDrawable(resId);
    }

    protected int getColor(int resId) {
        return getResources().getColor(resId);
    }

    private static Map<String, Object> stuts = null;
    private static int statusCount = 0;

    /**
     * 存放Activity间跳转时传递的参数
     *
     * @param obj
     * @return
     */
    public static String putStack(Object obj) {
        if (obj == null)
            return null;

        if (stuts == null)
            stuts = new TreeMap<String, Object>();

        String key = "strus_" + statusCount;
        statusCount++;

        stuts.put(key, obj);
        return key;
    }

    /**
     * 获取Activity间跳转时传递的参数
     *
     * @param key
     * @return
     */
    public static Object getStackValue(String key) {
        Object obj = null;
        if (key != null)
            if (stuts != null)
                obj = stuts.get(key);
        return obj;
    }

    /**
     * 移除Activity间跳转时传递的参数
     *
     * @param key
     */
    public static void removeStack(String key) {
        if (key == null)
            return;
        if (stuts != null)
            stuts.remove(key);
    }
}
