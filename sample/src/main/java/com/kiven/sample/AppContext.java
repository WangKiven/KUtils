package com.kiven.sample;

import android.content.Context;

import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KContext;
import com.kiven.kutils.tools.KUtil;

/**
 *
 * Created by kiven on 2017/2/16.
 */

public class AppContext extends KContext {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        KLog.i("AppContext attachBaseContext");
    }

    @Override
    protected void init() {
        super.init();
        KLog.i("AppContext init");
        KUtil.setApp(this);
    }
}
