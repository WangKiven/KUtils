package com.kiven.kutils.tools;

import android.app.Application;

import androidx.annotation.CallSuper;

import com.kiven.kutils.BuildConfig;

/**
 * Created by kiven on 16/5/6.
 */
public class KContext extends Application {
    protected KAppHelper helper;

    @Override
    public final void onCreate() {
        super.onCreate();

        initKUtil();
        if (KUtil.getApp() == null) KUtil.setApp(this);// 防止 initKUtil 被覆盖，但没设置KUtil

        // 必须先调用KUtil.setApp(this);否则 helper=null
        helper = KAppHelper.getInstance();

        helper.startAppCreate();

        // TODO: 2021-04-06 -------------------------------------
        onCreate(helper.isMainProcess());
        // TODO: 2021-04-06 -------------------------------------

        helper.endAppCreate();
    }

    protected void initKUtil() {
        KUtil.Config config = new KUtil.Config();
        config.setDebug(isDebug());
        KUtil.init(this, config);
    }

    /**
     * 初始
     * @param isMain 是否是在主线程
     */
    @CallSuper
    public void onCreate(boolean isMain) {

    }

    /**
     * 是否是debug状态
     */
    public Boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
