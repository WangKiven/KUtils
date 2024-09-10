package com.kiven.sample

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.service.PersistentService

/**
 * Created by kiven on 2019-12-23.
 */
class AHAutoStartAndLiving : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        addBtn("加入白名单", View.OnClickListener {
            // 需配置权限<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
            val powerManager = mActivity.getSystemService(Context.POWER_SERVICE) as PowerManager?
            powerManager ?: return@OnClickListener

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                powerManager.isIgnoringBatteryOptimizations(mActivity.packageName)
            }
        })

        addBtn("启动服务", View.OnClickListener { KUtil.startService(PersistentService::class.java)})
        addBtn("", View.OnClickListener {})
        addBtn("", View.OnClickListener {})
        addBtn("", View.OnClickListener {})
    }
}