package com.kiven.kutils.activityHelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.kiven.kutils.callBack.CallBack;
import com.kiven.kutils.callBack.Consumer;
import com.kiven.kutils.tools.KImage;
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
            if (mActivity == null)
                mIntent = new Intent();
            else
                mIntent = mActivity.getIntent();
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
        return KHelperActivity.class;
    }

    public KActivityHelper addFlags(int flags) {
        getIntent().addFlags(flags);
        return this;
    }

    public KActivityHelper putExtra(String key, Object value) {
        if (value != null) {
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
        }
        return this;
    }

    public KActivityHelper putExtra(String key, byte[] value) {
        getIntent().putExtra(key, value);
        return this;
    }
    //--------------------------------------------

    /**
     * 在UI线程上运行。
     * 最好再数据处理完成后，再用该方法进行UI的显示
     * 如果owner（activity或fragment）在前台，则运行callBack。
     * 如果owner（activity或fragment）在后台，则等待owner进入前台再运行callBack。
     * 如果owner（activity或fragment）已经退出，则不会运行callBack。
     */
    protected void runUI(CallBack callBack) {
        KView.runUI(mActivity, callBack);
    }

    @CallSuper
    public void onCreate(@NonNull KHelperActivity activity, Bundle savedInstanceState) {
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
        initBackToolbar(toolBar, false);
    }

    protected void initBackToolbar(@IdRes int toolBarId, boolean showTitle) {
        Toolbar toolBar = findViewById(toolBarId);
        initBackToolbar(toolBar, showTitle);
    }

    protected void initBackToolbar(Toolbar toolBar) {
        initBackToolbar(toolBar, false);
    }
    protected void initBackToolbar(Toolbar toolBar, boolean showTitle) {
        if (toolBar == null) {
            return;
        }
        mActivity.setSupportActionBar(toolBar);
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            KView.initBackActionBar(actionBar, showTitle);

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

    private CallBack nextResumeAction;

    public void onResume() {
        if (nextResumeAction != null) {
            nextResumeAction.callBack();
            nextResumeAction = null;
        }
    }

    // todo 注册该方法后第一次调用onResume需要运行的操作, 传null取消操作。建议用在，打开新界面后返回该界面时刷新（列表中某条）数据
    public void registerNextResumeAction(CallBack callBack) {
        nextResumeAction = callBack;
    }

    public void onPause() {

    }

    public void onStop() {
    }

    public void onDestroy() {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

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

    private Consumer<ActivityResultInfo> activityResultAction;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (activityResultAction != null) {
            activityResultAction.callBack(new ActivityResultInfo(requestCode, resultCode, data));
        }
        activityResultAction = null;
    }

    // todo 注册该方法后第一次调用onActivityResult需要运行的操作, 传null取消操作。建议启动新界面后立刻调用该方法。
    public void registerNextActivityResultAction(Consumer<ActivityResultInfo> action) {
        activityResultAction = action;
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
        return mActivity.getDelegate().findViewById(rId);
    }

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

    protected Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(mActivity, resId);
    }

    protected int getColor(int resId) {
        return ContextCompat.getColor(mActivity, resId);
    }

    private static Map<String, Object> stuts = null;
    private static int statusCount = 0;

    /**
     * 存放Activity间跳转时传递的参数
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
     */
    public static void removeStack(String key) {
        if (key == null)
            return;
        if (stuts != null)
            stuts.remove(key);
    }
}
