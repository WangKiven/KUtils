package com.kiven.kutils.activityHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.kiven.kutils.activityHelper.activity.KActivity;
import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KContext;

import java.lang.reflect.Constructor;

public class KHelperActivity extends KActivity {

    private KActivityHelper helper;

    public KActivityHelper getHelper() {
        return helper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHelper(savedInstanceState);

        if (helper == null) {
            finish();
        } else {
            helper.onCreate(this, savedInstanceState);
        }

//        KContext.getInstance().onActivityCreate(this);
    }

    @SuppressWarnings({"rawtypes"})
    protected KActivityHelper getHelper(Bundle savedInstanceState) {
        // 使用ViewModel 确保Helper不会多个同时存在
//        KHelperModel model = ViewModelProviders.of(this).get(KHelperModel.class);
//        helper = model.helper;
        if (helper == null) {//此判断是为了防止子类已设置helper的值
            if (savedInstanceState == null) {
                helper = (KActivityHelper) KActivityHelper.getStackValue(getIntent().getStringExtra("BaseActivityHelper"));
            } else {
                try {
                    Constructor[] constructors = Class.forName(savedInstanceState.getString("helper_name")).getConstructors();
                    if (constructors != null && constructors.length > 0) {
                        Constructor constructor = constructors[0];
                        Class[] types = constructor.getParameterTypes();
                        if (types != null && types.length > 0) {
                            Object[] parameters = new Object[types.length];
                            for (int i = 0; i < types.length; i++) {
                                parameters[i] = getDefaultValue(types[i]);
                            }
                            helper = (KActivityHelper) constructor.newInstance(parameters);
                        } else {
                            helper = (KActivityHelper) constructor.newInstance();
                        }
                    }

                } catch (Exception e) {
                    KLog.e(e);
                }
            }

//            model.helper = helper;
        }
        return helper;
    }

    /**
     * 根据参数类型获取默认值
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object getDefaultValue(Class class1) {
        if (class1.isAssignableFrom(int.class) || class1.isAssignableFrom(Integer.class)
                || class1.isAssignableFrom(float.class) || class1.isAssignableFrom(Float.class)
                || class1.isAssignableFrom(double.class) || class1.isAssignableFrom(Double.class)) {
            return 0;
        } else if (class1.isAssignableFrom(boolean.class) || class1.isAssignableFrom(Boolean.class)) {
            return false;
        }

        return null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (helper != null) {
            helper.onNewIntent(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (helper != null) {
            outState.putString("helper_name", helper.getClass().getName());
            helper.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        if (helper != null) {
            helper.onRestoreInstanceState(savedInstanceState);
        }
    }

    public void onClick(View view) {
        if (helper != null) {
            helper.onClick(view);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        KContext.getInstance().onActivityStart(this);
        if (helper != null) {
            helper.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        KContext.getInstance().onActivityResume(this);
        if (helper != null) {
            helper.onResume();
        }
    }

    @Override
    protected void onPause() {
//        KContext.getInstance().onActivityPause(this);
        if (helper != null) {
            helper.onPause();
        }
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
//        KContext.getInstance().onActivityFinish(this);
    }

    @Override
    protected void onStop() {
        if (helper != null) {
            helper.onStop();
        }
        super.onStop();

//        KContext.getInstance().onActivityStop(this);
    }

    @Override
    protected void onDestroy() {
        if (helper != null) {
            helper.onDestroy();
            helper.mActivity = null;
            helper = null;
        }
        KActivityHelper.removeStack(getIntent().getStringExtra("BaseActivityHelper"));//将helper移除
        super.onDestroy();
//        KContext.getInstance().onActivityDestory(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (helper != null) {
            helper.onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (helper == null) {
            return onKeyDown(keyCode, event);
        } else {
            boolean b = super.onKeyDown(keyCode, event);
            return b && helper.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        if (helper == null) {
            super.onBackPressed();
        } else {
            if (helper.onBackPressed()) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (helper == null) {
            return false;
        } else {
            return helper.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (helper == null) {
            return false;
        } else {
            return helper.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (helper != null) {
            helper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (helper != null) {
            helper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // for fix https://stackoverflow.com/questions/41025200/android-view-inflateexception-error-inflating-class-android-webkit-webview
    /*@Override
    public AssetManager getAssets() {
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            return getResources().getAssets();
        } else
            return super.getAssets();
    }*/
}
