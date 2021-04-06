package com.kiven.kutils.tools;

import android.app.Application;

/**
 * Created by kiven on 16/5/6.
 */
public class KContext extends Application {
    protected KAppHelper helper;

    @Override
    public final void onCreate() {
        super.onCreate();
        KUtil.setApp(this);

        // 必须先调用KUtil.setApp(this);否则 helper=null
        helper = KAppHelper.getInstance();

        helper.startAppCreate();

        // TODO: 2021-04-06 -------------------------------------
        if (helper.isMainProcess()) {
            initOnlyMainProcess();
        }
        init();
        // TODO: 2021-04-06 -------------------------------------

        helper.endAppCreate();
    }

    /**
     * 主进程下
     */
    protected void initOnlyMainProcess() {
    }

    /**
     * 所有进程下
     */
    protected void init() {
    }
}
