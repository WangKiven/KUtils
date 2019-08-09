package com.kiven.sample.dock

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.sample.R
import com.kiven.sample.util.showDialog
import com.kiven.sample.util.showSnack

/**
 * Created by oukobayashi on 2019-08-09.
 */
class ActivityDock : KActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dock)
    }

    /**
     *
     * 参考文档：
     *
     * 桌面检测与设置：https://blog.csdn.net/weixin_41549915/article/details/81633354
     *
     *
     */
    fun onClick(view: View) {

        when (view.id) {
            R.id.btn_set_dock -> {
                val paramIntent = Intent("android.intent.action.MAIN")
                paramIntent.component = ComponentName("android", "com.android.internal.app.ResolverActivity")
                paramIntent.addCategory("android.intent.category.DEFAULT")
                paramIntent.addCategory("android.intent.category.HOME")
                startActivity(paramIntent)
            }
            R.id.btn_dock_info -> {
                // 判断桌面是否是当前app的
                val dockIntent = Intent(Intent.ACTION_MAIN)
                dockIntent.addCategory(Intent.CATEGORY_HOME)
                val res = packageManager.resolveActivity(dockIntent, 0)

                val sb = StringBuilder()
                sb.appendln("桌面Activity类名：${res.activityInfo.name}")
                sb.appendln("桌面应用包名：${res.activityInfo.packageName}")

                showDialog(sb.toString())
            }
        }
    }
}