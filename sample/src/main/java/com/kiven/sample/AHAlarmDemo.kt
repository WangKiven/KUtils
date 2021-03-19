package com.kiven.sample

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.AlarmClock
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.dock.ActivityDock
import com.kiven.sample.util.showDialog
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.*

/**
 * Created by oukobayashi on 2019-11-14.
 *
 * https://blog.csdn.net/danwuxie/article/details/88605418
 */
class AHAlarmDemo : KActivityHelper() {
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
        // TODO: 2019-11-14 ----------------------------------------------------------
        addTitle("AlarmClock 闹钟")
        val txtTag = addTitle("mnifest权限：com.android.alarm.permission.SET_ALARM 注意格式，和其他权限不一样，" +
                "有的功能没效果，可能是系统不支持")
        addView("设置系统闹钟,下一分钟响铃", View.OnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.SECOND, 70)

            val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "测试闹钟 ⏰")
                putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
                putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE))
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)// 不显示设置界面
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            mActivity.startActivity(alarmIntent)
        })
        addView("打开闹钟设置", View.OnClickListener {
            mActivity.startActivity(Intent(AlarmClock.ACTION_SET_ALARM))
        })
        addView("查看闹钟", View.OnClickListener {
            mActivity.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS))
        })
        addView("关闭闹钟", View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val alarmIntent = Intent(AlarmClock.ACTION_DISMISS_ALARM).apply {
                    putExtra(AlarmClock.EXTRA_MESSAGE, "测试闹钟⏰")
                    putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL)
                    putExtra(AlarmClock.EXTRA_SKIP_UI, true)// 不显示设置界面
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                mActivity.startActivity(alarmIntent)
            } else mActivity.showDialog("需要大于等于 Android M")
        })

        addTitle("AlarmClock 定时器")
        addView("设置定时器，10秒后", View.OnClickListener {
            val alarmIntent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "测试闹钟⏰")
                putExtra(AlarmClock.EXTRA_LENGTH, 10) // 定时多久，单位秒
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)// 不显示设置界面
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            mActivity.startActivity(alarmIntent)
        })
        addView("打开定时器设置", View.OnClickListener {
            mActivity.startActivity(Intent(AlarmClock.ACTION_SET_TIMER))
        })
        addView("显示定时器", View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mActivity.startActivity(Intent(AlarmClock.ACTION_SHOW_TIMERS))
            } else mActivity.showDialog("需要大于等于 Android Q")
        })

        addView("删除定时器", View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mActivity.startActivity(Intent(AlarmClock.ACTION_DISMISS_TIMER))
            } else mActivity.showDialog("需要大于等于 Android P")
        })

        addTitle("AlarmManager")
        addView("AlarmManager 定时任务, 5秒后打开自定义桌面", View.OnClickListener {
            val alarmManager = mActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//            val pi = PendingIntent.getBroadcast(mActivity, 888, Intent(AlarmClock.ACTION_SHOW_ALARMS), 0)
            val pi = PendingIntent.getActivity(mActivity, 888, Intent(mActivity, ActivityDock::class.java), 0)

            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, pi)
        })
        addView("", View.OnClickListener {
        })
    }
}