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
 * Created by oukobayashi on 2019-12-23.
 */
class AHAutoStartAndLiving : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(NestedScrollView(activity).apply {
            addView(flexboxLayout)
        })

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }

        addView("加入白名单", View.OnClickListener {
            // 需配置权限<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
            val powerManager = mActivity.getSystemService(Context.POWER_SERVICE) as PowerManager?
            powerManager ?: return@OnClickListener

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                powerManager.isIgnoringBatteryOptimizations(mActivity.packageName)
            }
        })

        addView("启动服务", View.OnClickListener { KUtil.startService(PersistentService::class.java)})
        addView("", View.OnClickListener {})
        addView("", View.OnClickListener {})
        addView("", View.OnClickListener {})
    }
}