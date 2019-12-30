package com.kiven.sample

import android.app.ActivityManager
import android.content.Context
import android.content.IntentFilter
import android.os.Process
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KContext
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.noti.NotificationClickReceiver
import com.tencent.smtt.sdk.QbSdk
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.Logger
import com.xiaomi.mipush.sdk.MiPushClient
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

    override fun init() {
        super.init()
        KLog.i("AppContext init")
        x.Ext.init(this)
        KUtil.setApp(this)

        registerReceiver(NotificationClickReceiver(), IntentFilter())

//        ARouter.openLog();     // 打印日志
//        ARouter.openDebug();   // // TODO: 2019-09-16  开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(this)


        KGranting.useFragmentRequest = true

        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {

            }

            override fun onViewInitFinished(p0: Boolean) {
                KLog.i("QbSdk.initX5Environment -> onViewInitFinished : $p0")
            }
        })

        // mipush
        initMiPush()
    }

    /**
     * https://dev.mi.com/console/doc/detail?pId=41
     */
    private fun initMiPush(){
        if (!shouldInitMiPush()) return

        //初始化push推送服务
        MiPushClient.registerPush(this, "2882303761518292808", "5681829285808")

        //打开Log
        val newLogger: LoggerInterface = object : LoggerInterface {
            override fun setTag(tag: String) { // ignore
            }

            override fun log(content: String, t: Throwable) {
                KLog.e(Throwable("MiPush:$content", t))
            }

            override fun log(content: String) {
                KLog.i("MiPush:$content")
            }
        }
        Logger.setLogger(this, newLogger)
    }

    private fun shouldInitMiPush(): Boolean {
        val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses
        val mainProcessName = applicationInfo.processName
        val myPid: Int = Process.myPid()
        for (info in processInfos) {
            if (info.pid === myPid && mainProcessName == info.processName) {
                return true
            }
        }
        return false
    }
}
