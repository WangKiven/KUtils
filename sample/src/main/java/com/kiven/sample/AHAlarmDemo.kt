package com.kiven.sample

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KGranting
import com.kiven.sample.systemdata.AHSystemImage
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.*

/**
 * Created by oukobayashi on 2019-11-14.
 *
 * https://blog.csdn.net/danwuxie/article/details/88605418
 */
class AHAlarmDemo:KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

        val addTitle = fun(text: String): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)

            return tv
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }
        // TODO: 2019-11-12 ----------------------------------------------------------
        val txtTag = addTitle("mnifest权限：com.android.alarm.permission.SET_ALARM 注意格式，和其他权限不一样")
        addView("设置系统闹钟,下一分钟响铃", View.OnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.SECOND, 70)

            val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "测试闹钟⏰")
                putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
                putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE))
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            mActivity.startActivity(alarmIntent)
        })
        addView("AlarmManager闹钟", View.OnClickListener {
        })
        addView("打开闹钟设置", View.OnClickListener {
//            KGranting.requestPermissions(mActivity, 344, Manifest.permission.SET_ALARM, "设置闹钟"){
//                if (it) mActivity.startActivity(Intent(AlarmClock.ACTION_SET_ALARM))
//            }
            mActivity.startActivity(Intent(AlarmClock.ACTION_SET_ALARM))
        })
        addView("", View.OnClickListener {
        })
    }
}