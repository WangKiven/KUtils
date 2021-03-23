package com.kiven.sample

import android.content.Context
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KContext
import com.kiven.sample.noti.NotificationClickReceiver
import com.kiven.sample.util.Const
import com.tencent.smtt.sdk.QbSdk
import org.xutils.x


/**
 *
 * Created by kiven on 2017/2/16.
 */

class AppContext : KContext() {


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        KLog.i("AppContext attachBaseContext")
    }

    override fun initOnlyMainProcess() {
        super.initOnlyMainProcess()

        KLog.printDeviceInfo()
    }

    override fun init() {
        super.init()
        KLog.i("AppContext init")
        x.Ext.init(this)

//        KGranting.useFragmentRequest = true
//        KUtil.setApp(this)

        registerReceiver(NotificationClickReceiver(), IntentFilter())

//        ARouter.openLog();     // 打印日志
//        ARouter.openDebug();   // // TODO: 2019-09-16  开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(this)

        if (Const.nightMode != AppCompatDelegate.getDefaultNightMode())
            AppCompatDelegate.setDefaultNightMode(Const.nightMode)


        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {

            }

            override fun onViewInitFinished(p0: Boolean) {
                KLog.i("QbSdk.initX5Environment -> onViewInitFinished : $p0")
            }
        })
    }
}
