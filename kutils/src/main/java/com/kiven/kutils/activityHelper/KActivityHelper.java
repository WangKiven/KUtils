package com.kiven.kutils.activityHelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kiven.kutils.R;
import com.kiven.kutils.tools.KView;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * 父类helper,
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
        tIntent.setComponent(new ComponentName(context, getActivityClas()));
        tIntent.putExtra("BaseActivityHelper", putStack(this));
        return tIntent;
    }

    protected Class getActivityClas() {
        return KRoboHelperActivity.class;
    }

    public KActivityHelper addFlags(int flags) {
        getIntent().addFlags(flags);
        return this;
    }

    public KActivityHelper putExtra(String key, Object value) {
        if (value instanceof Boolean) {
            getIntent().putExtra(key, (boolean) value);
        } else if (value instanceof String) {
            getIntent().putExtra(key, (String) value);
        } else if (value instanceof Integer) {
            getIntent().putExtra(key, (int) value);
        } else if (value instanceof Float) {
            getIntent().putExtra(key, (float) value);
        } else if (value instanceof Double) {
            getIntent().putExtra(key, (double) value);
        } else if (value instanceof Parcelable) {
            getIntent().putExtra(key, (Parcelable) value);
        } else if (value instanceof Serializable) {
            getIntent().putExtra(key, (Serializable) value);
        }
        return this;
    }
    public KActivityHelper putExtra(String key, byte[] value) {
        getIntent().putExtra(key, value);
        return this;
    }
    //--------------------------------------------

    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        mActivity = activity;
        mIntent = activity.getIntent();
    }

    public void onNewIntent(Intent intent) {

    }

    /**
     * 给toobar添加返回按钮
     */
    protected void initBackToolbar(@IdRes int toolBarId) {
        Toolbar toolBar = findViewById(toolBarId);
        if (toolBar == null) {
            return;
        }
        mActivity.setSupportActionBar(toolBar);
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            KView.initBackActionBar(actionBar);

            toolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
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

    public boolean onCreateOptionsMenu(Menu menu){ return  false;}

    public boolean onOptionsItemSelected(MenuItem item){ return false;}

    public void onWindowFocusChanged(boolean hasFocus) {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    public boolean onBackPressed() {
        return true;
    }

    public void finish() {
        mActivity.finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        
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
    public <T extends View> T findViewById(@IdRes int rId) {
        return (T) mActivity.getDelegate().findViewById(rId);
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
