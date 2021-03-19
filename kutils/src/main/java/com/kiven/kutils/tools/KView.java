package com.kiven.kutils.tools;

import android.annotation.SuppressLint;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.kiven.kutils.callBack.CallBack;

import java.util.ArrayList;

public class KView {
    /**
     * 设置单击事件，主要防止多次连续点击
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

    public static void setVisibility(boolean isShow, View... views) {
        if (views != null && views.length > 0)
            for (View view : views)
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
     */
    public static void setWeight(View view, float weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = weight;
    }


    /**
     * 滚动到指定位置
     */
    public static void scrollTo(@NonNull RecyclerView recyclerView, int position) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, 0);
        }
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
     * webview 基本配置
     */
    public static void initWebView(@NonNull WebView webView) {
        WebSettings webSetting = webView.getSettings();
        // 需要使用 file 协议
        webSetting.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSetting.setAllowFileAccessFromFileURLs(false);
            webSetting.setAllowUniversalAccessFromFileURLs(false);
        }

        // 禁止 file 协议加载 JavaScript
        webSetting.setJavaScriptEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 自适应屏幕
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        // 不支持缩放(如果要支持缩放，html页面本身也要支持缩放：不能加user-scalable=no)
        webSetting.setBuiltInZoomControls(true);
        webSetting.setSupportZoom(true);
        webSetting.setDisplayZoomControls(false);

        /*webView.addJavascriptInterface(WebJsCall(), "android")*/
        // 支持通过js打开新的窗口
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);

        webSetting.setBlockNetworkImage(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setDatabaseEnabled(true);
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            webSetting.databasePath = applicationContext.cacheDir.absolutePath*/
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 接受所有证书（如 http, https 等，默认不接受http）。建议具体使用时具体设置
        /*webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (handler != null) {
                    handler.proceed();
                }
            }
        });*/
    }

    /**
     * 在UI线程上运行。主要作用是防止activity已经finish，还有线程调用该activity的UI线程。
     * 最好再数据处理完成后，再用该方法进行UI的显示
     * 如果owner（activity或fragment）在前台，则运行callBack。
     * 如果owner（activity或fragment）在后台，则等待owner进入前台再运行callBack。
     * 如果owner（activity或fragment）已经退出，则不会运行callBack。
     */
    public static synchronized void runUI(@NonNull final LifecycleOwner owner, @NonNull final CallBack callBack) {
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

    /**
     * 获取View的截图
     *
     * @param width  指定宽
     * @param height 指定高
     */
    public static Bitmap getBitmap(@NonNull View v, int width, int height) {
        if (width > 0 && height > 0) {
            //测量使得view指定大小
            int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            v.measure(measuredWidth, measuredHeight);
            //调用layout方法布局后，可以得到view的尺寸大小
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }

    private static MutableLiveData<Integer> data = new MutableLiveData<Integer>();


    /**
     * 根据坐标获取相对应的子控件<br>
     * 在Activity使用
     *
     * @return 目标View
     */
    public static View getViewAtXY(Activity activity, int x, int y) {
        // 从Activity里获取容器
        View root = activity.getWindow().getDecorView();
        return getViewAtXY(root, x, y);
    }

    /**
     * 根据坐标获取相对应的子控件<br>
     * 在重写ViewGroup使用
     *
     * @return 目标View
     */
    public static View getViewAtXY(View view, int x, int y) {
        View targetView = null;
        if (view instanceof ViewGroup) {
            // 父容器,遍历子控件
            ViewGroup v = (ViewGroup) view;
            for (int i = 0; i < v.getChildCount(); i++) {
                targetView = getTouchViewAtXY(v.getChildAt(i), x, y);
                if (targetView != null) {
                    break;
                } else {
                    getViewAtXY(v.getChildAt(i), x, y);
                }
            }
        } else {
            targetView = getTouchViewAtXY(view, x, y);
        }
        return targetView;
    }

    public static View getTouchViewAtXY(View view, int x, int y) {
        View targetView = null;
        ArrayList<View> touchableViews = view.getTouchables();
        for (View child : touchableViews) {
            if (child.isClickable() && isPointInView(child, x, y)) {
                targetView = child;
                // break;
            }
        }
        return targetView;
    }

    public static boolean isPointInView(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }
}
