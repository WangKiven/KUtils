package com.kiven.kutils.tools;

import android.app.Application;

import com.kiven.kutils.logHelper.KLog;

import org.xutils.x;

/**
 *
 * Created by kiven on 16/5/6.
 */
public class KContext extends Application {


    private static KContext mInstance;

    public static KContext getInstance() {
        return mInstance;
    }

    @Override
    public final void onCreate() {
        super.onCreate();

        mInstance = this;

        init();
    }

    protected void init() {
        x.Ext.init(this);
        x.Ext.setDebug(KLog.isDebug());
    }
}
