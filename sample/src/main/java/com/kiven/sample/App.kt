package com.kiven.sample

import android.content.Context
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
//import com.alibaba.android.arouter.launcher.ARouter
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KContext
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.noti.NotificationClickReceiver
import com.kiven.sample.util.Const
import com.tencent.smtt.sdk.QbSdk
import org.xutils.x
import com.tencent.smtt.export.external.TbsCoreSettings





/**
 *
 * Created by kiven on 2017/2/16.
 */

class App : KContext() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        KLog.i("AppContext attachBaseContext")
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
    override fun initKUtil() {
        KUtil.init(this, KUtil.Config().apply {
            isDebug = BuildConfig.DEBUG
            fileprovider = "com.kiven.sample.fileprovider"
        })
    }

    override fun onCreate(isMain: Boolean) {
        super.onCreate(isMain)
        if (isMain) {
//        DoKit.Builder(this)
//            .productId("需要使用平台功能的话，需要到dokit.cn平台申请id")
//            .build()

            KLog.printDeviceInfo()
        }


        x.Ext.init(this)

//        KGranting.useFragmentRequest = true
//        KUtil.setApp(this)

        if (Const.nightMode != AppCompatDelegate.getDefaultNightMode())
            AppCompatDelegate.setDefaultNightMode(Const.nightMode)

        registerReceiver(NotificationClickReceiver(), IntentFilter())

//        ARouter.openLog();     // 打印日志
//        ARouter.openDebug();   // // TODO: 2019-09-16  开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//        ARouter.init(this)


        // 在调用TBS初始化、创建WebView之前进行如下配置
        // 在调用TBS初始化、创建WebView之前进行如下配置
        val map = mutableMapOf<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {

            }

            override fun onViewInitFinished(p0: Boolean) {
                KLog.i("QbSdk.initX5Environment -> onViewInitFinished : $p0")
            }
        })
    }
}
