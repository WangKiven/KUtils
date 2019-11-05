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

//        KLog.i(String.format("点击或滚动：：：：：%s %08x %x", event.className.toString(), event.eventType, event.action))

        if (task != null) task!!.onAccessibilityEvent(this, event)

        /*KLog.i("onAccessibilityEvent: " + (event == null ? "null" : event.getPackageName().toString()));
        if (event == null) return;


        AccessibilityNodeInfo eventNode = event.getSource();
        if (eventNode == null) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow(); //当前窗口根节点
        if (rootNode == null) return;



        AccessibilityUtil.printTree(rootNode);


        AccessibilityUtil.findTxtClick(rootNode, "我", "com.tencent.mm:id/djv");


        eventNode.recycle();*/


        /*
        某些手机安装页事件返回节点有可能为null，无法获取安装按钮
        例如华为mate10安装页就会出现event.getSource()为null，所以取巧改变当前页面状态，重新获取节点。
        该方法在华为mate10上生效，但其它手机没有验证...(目前小米手机没有出现这个问题)
        */
        /*KLog.i("onAccessibilityEvent: " + event);
        AccessibilityNodeInfo eventNode = event.getSource();
        if (eventNode == null) {
            KLog.i("eventNode: null, 重新获取eventNode...");
            performGlobalAction(GLOBAL_ACTION_RECENTS); // 打开最近页面
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    performGlobalAction(GLOBAL_ACTION_BACK); // 返回安装页面
                }
            }, DELAY_PAGE);
            return;
        }*/

        /*
        模拟点击->自动安装，只验证了小米5s plus(MIUI 9.8.4.26)、小米Redmi 5A(MIUI 9.2)、华为mate 10
        其它品牌手机可能还要适配，适配最可恶的就是出现安装广告按钮，误点安装其它垃圾APP（典型就是小米安装后广告推荐按钮，华为安装开始官方安装）
        */
        /*AccessibilityNodeInfo rootNode = getRootInActiveWindow(); //当前窗口根节点
        if (rootNode == null)
            return;
        KLog.i( "rootNode: " + rootNode);
        if (isNotAD(rootNode))
            findTxtClick(rootNode, "安装"); //一起执行：安装->下一步->打开,以防意外漏掉节点
        findTxtClick(rootNode, "继续安装");
        findTxtClick(rootNode, "下一步");
        findTxtClick(rootNode, "打开");
        // 回收节点实例来重用
        eventNode.recycle();
        rootNode.recycle();*/
    }

    // 排除广告[安装]按钮
    private fun isNotAD(rootNode: AccessibilityNodeInfo): Boolean {
        return (isNotFind(rootNode, "还喜欢") //小米
                && isNotFind(rootNode, "官方安装")) //华为
    }

    private fun isNotFind(rootNode: AccessibilityNodeInfo, txt: String): Boolean {
        val nodes = rootNode.findAccessibilityNodeInfosByText(txt)
        return nodes == null || nodes.isEmpty()
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
