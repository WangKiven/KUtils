package com.kiven.sample.autoService

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.floatView.FloatView

/**
 * https://www.jianshu.com/p/04ebe2641290
 * 建议阅读：https://blog.csdn.net/weimingjue/article/details/82744146
 * xml配置详解：https://www.jianshu.com/p/ef01ce654302
 */
class AutoInstallService : AccessibilityService() {
    private val mHandler = Handler()

    private var floatView: FloatView? = null

    override fun onCreate() {
        super.onCreate()
        // y由于是后台服务，必须是应用外悬浮
        floatView = FloatView(baseContext, application.getSystemService(Context.WINDOW_SERVICE) as WindowManager, true)
        floatView!!.showFloat()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        /*if (event == null || !event.getPackageName().toString()
                .contains(getString(R.string.auto_access_service_dist_package)))//不写完整包名，是因为某些手机(如小米)安装器包名是自定义的
            return;*/

        KLog.i(String.format("触发事件：：：：：%s %x %x %x", event.className.toString(), event.eventType, event.action,
                event.contentChangeTypes))

        if (task != null) task!!.onAccessibilityEvent(this, event)
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        KLog.i("onServiceConnected: ")

        mInstance = this

        // 退出设置界面，后面再优化
//        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
//        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        val ah = AHAutoService()
        ah.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        ah.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ah.startActivity(this)

        mHandler.postDelayed({
            // 任务处理
            if (task != null) task!!.onServiceConnected(this@AutoInstallService)
        }, DELAY_PAGE)
    }

    override fun onInterrupt() {
        KLog.i("onInterrupt: ")

//        performGlobalAction(GLOBAL_ACTION_BACK)
//        mHandler.postDelayed({ performGlobalAction(GLOBAL_ACTION_BACK) }, DELAY_PAGE)

        mInstance = null
    }

    override fun onDestroy() {
        super.onDestroy()
        KLog.i("onDestroy: ")

        // 服务停止，重新进入系统设置界面
        AccessibilityUtil.jumpToSetting(this)

        mInstance = null

        if (floatView != null) {
            floatView!!.hideFloat()
        }
    }

    interface AccessibilityTask {
        fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent?)

        fun onServiceConnected(service: AccessibilityService)
    }

    companion object {
        private var mInstance: AutoInstallService? = null

        val isStarted: Boolean
            get() = mInstance != null

        private val DELAY_PAGE = 320L // 页面切换时间

        var task: AccessibilityTask? = null
    }
}
