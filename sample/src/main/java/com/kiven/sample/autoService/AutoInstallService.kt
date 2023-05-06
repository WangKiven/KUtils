package com.kiven.sample.autoService

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KAppHelper
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.autoService.wechat.WXConst

/**
 * https://www.jianshu.com/p/04ebe2641290
 * 建议阅读：https://blog.csdn.net/weimingjue/article/details/82744146
 * xml配置详解：https://www.jianshu.com/p/ef01ce654302
 */
class AutoInstallService : AccessibilityService() {
    private val mHandler = Handler(Looper.getMainLooper())

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        /*if (event == null || !event.getPackageName().toString()
                .contains(getString(R.string.auto_access_service_dist_package)))
            return;*/

        if (KLog.isDebug()) {
            when (WXConst.logType % 2) {
                0 -> {}
                1 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        KLog.i(
                                String.format(
                                        "触发事件：：：：：%s %x %x %x",
                                        event.className.toString(),
                                        event.eventType,
                                        event.action,
                                        event.contentChangeTypes
                                )
                        )
                    } else {
                        KLog.i(
                                String.format(
                                        "触发事件：：：：：%s %x %x",
                                        event.className.toString(),
                                        event.eventType,
                                        event.action
                                )
                        )
                    }
                    rootInActiveWindow?.let {
                        AccessibilityUtil.printTree(it)
                    }
                }
                /*2 -> {
                    rootInActiveWindow?.let {
                        AccessibilityUtil.printTree(it)
                    }
                }
                3 -> {
                    event.source?.let {
                        AccessibilityUtil.printTree(it)
                    }
                }*/
            }
        }

        if (task != null) task!!.onAccessibilityEvent(event)
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        KLog.i("onServiceConnected: ")

        mInstance = this

        closeSettingAndNext()
    }

    private fun closeSettingAndNext() {
        // 推一页
        performGlobalAction(GLOBAL_ACTION_BACK)

        mHandler.postDelayed({
            if (KAppHelper.getInstance().topActivity == null) {
                closeSettingAndNext()
            } else {
                // 任务处理
                task?.registerService(this@AutoInstallService)
            }

        }, DELAY_PAGE)
    }

    override fun onInterrupt() {
        KLog.i("onInterrupt: ")

//        performGlobalAction(GLOBAL_ACTION_BACK)
//        mHandler.postDelayed({ performGlobalAction(GLOBAL_ACTION_BACK) }, DELAY_PAGE)

        task?.close()
        mInstance = null
    }

    override fun onDestroy() {
        super.onDestroy()
        KLog.i("onDestroy: ")

        // 服务停止，重新进入系统设置界面
        AccessibilityUtil.jumpToSetting(this)

        task?.close()
        mInstance = null
    }

    companion object {
        var mInstance: AutoInstallService? = null
            private set

        private const val DELAY_PAGE = 500L // 页面切换时间

        private var task: AutoTaskInterface? = null

        fun startWXTask(
                mActivity: Activity,
                task: AutoTaskInterface
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!KUtil.canDrawOverlays()) {
                    KAlertDialogHelper.Show2BDialog(mActivity, "该功能需要【显示在其他应用上层】的权限，请点确定去开启。") {
                        KUtil.startOverlaySetting()
                    }
                    return
                }
            }

            AutoInstallService.task?.close()
            AutoInstallService.task = task

            if (mInstance == null) {
                // 请在手机【无障碍】设置中，开启省心宝提供的【微信分享助手】开始获取微信好友标签，点确定去开启。
                KAlertDialogHelper.Show2BDialog(
                        mActivity,
                        "请在手机【无障碍】设置中，开启由【KUSample】提供的【无障碍助手】，点确定去开启。"
                ) {
                    AccessibilityUtil.jumpToSetting(mActivity)
                }
            } else {
                /*KAppTool.startApp(
                        mActivity,
                        mActivity.getString(R.string.auto_access_service_dist_package)
                )*/
                task.registerService(mInstance!!)
            }
        }
    }
}
