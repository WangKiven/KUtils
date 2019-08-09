package com.kiven.sample.dock

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.sample.R
import com.kiven.sample.util.showDialog

/**
 * Created by oukobayashi on 2019-08-09.
 */
class ActivityDock:KActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dock)
    }
    fun onClick(view: View) {
        // 判断桌面是否是当前app的
        val dockIntent = Intent(Intent.ACTION_MAIN)
        dockIntent.addCategory(Intent.CATEGORY_HOME)
        val res = packageManager.resolveActivity(dockIntent, 0)
        showDialog(res.activityInfo.packageName)
    }
}