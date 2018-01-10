package com.kiven.kutils.tools;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.kiven.kutils.callBack.CallBack;

public class KView {
    /**
     * 设置单击事件，主要防止多次连续点击
     *
     * @param view
     * @param listener
     */
    public static void setOnClickListener(View view, final OnClickListener listener) {
        if (view == null || listener == null) {
            return;
        }

        view.setOnClickListener(new OnClickListener() {
            private long lastClickTime;

            private synchronized boolean isFastClick() {
                long time = System.currentTimeMillis();
                if (time - lastClickTime < 1000) {
                    return true;
                }
                lastClickTime = time;
                return false;
            }

            @Override
            public void onClick(View v) {
                if (isFastClick()) {
                    return;
                }
                listener.onClick(v);
            }
        });
    }

    /**
     * 设置背景
     */
    @SuppressLint("NewApi")
    public static void setBackground(View v, Drawable background) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(background);
        } else {
            v.setBackground(background);
        }
    }

    /**
     * 显示，隐藏控件
     */
    public static void setVisibility(View view, boolean isShow) {
        view.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 旋转，需api>=11才能实现旋转
     */
    public static void setRotation(View view, float rotate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setRotation(rotate);
        }
    }

    /**
     * 设置View在LinearLayout中的weight
     *
     * @param view
     * @param weight
     */
    public static void setWeight(View view, float weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = weight;
    }

    /**
     * actionbar添加默认返回按钮
     */
    public static void initBackActionBar(@NonNull ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    /**
     * 在UI线程上运行。
     * 最好再数据处理完成后，再用该方法进行UI的显示
     * 如果owner（activity或fragment）在前台，则运行callBack。
     * 如果owner（activity或fragment）在后台，则等待owner进入前台再运行callBack。
     * 如果owner（activity或fragment）已经退出，则不会运行callBack。
     */
    public static synchronized void runUI(@NonNull final LifecycleOwner owner, final CallBack callBack) {
        data.observe(owner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer o) {
                callBack.callBack();
                data.removeObserver(this);
            }
        });
        Integer v = data.getValue();
        data.setValue(v == null ? 0 : (v + 1));
    }

    private static MutableLiveData<Integer> data = new MutableLiveData<Integer>();
}
