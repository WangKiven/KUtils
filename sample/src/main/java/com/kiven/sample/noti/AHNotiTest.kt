package com.kiven.sample.noti

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import kotlinx.android.synthetic.main.ah_noti_test.view.*

/**
 * Created by wangk on 2019/5/13.
 */
class AHNotiTest : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_noti_test)

        val root = findViewById<View>(R.id.root)

        val notiManager = NotificationManagerCompat.from(mActivity)

        root.apply {
            val changeTopText = fun() {
                val result = StringBuilder("系统版本号大于26才有通知分组和通知Channel, 当前系统版本号${Build.VERSION.SDK_INT}\n")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val groups = notiManager.notificationChannelGroups

                    val channels = notiManager.notificationChannels
                    result.append("有${groups.size}个分组, ${channels.size}个通知")
                }

                tv_count.text = result
            }
            changeTopText()

            var count = 0
            btn_send.setOnClickListener {
                val pendingIntent = PendingIntent.getActivity(mActivity, 110, Intent(mActivity, ClickNotiActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                val channelId = "522$count"

                val mBuilder = NotificationCompat.Builder(mActivity, channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker("新消息$count") // 通知响起时，状态栏显示的内容
                        .setContentTitle("你的新消息$count")
                        .setContentText("巴拉巴拉巴拉$count")
                        .setNumber(12)
                        .setContentInfo("它是一系统级的全局通知")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (rg_group.checkedRadioButtonId) {
                        R.id.rb_0 -> {
                        }
                        R.id.rb_1 -> {
                            mBuilder.setGroup("group1")

                            if (notiManager.notificationChannelGroups.first { it.id == "group1" } == null) {
                                Snackbar.make(this, "分组不存在。还是能通知，但是通知不在设置的分组内", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        R.id.rb_2 -> {
                            mBuilder.setGroup("group2")

                            if (notiManager.notificationChannelGroups.first { it.id == "group2" } == null) {
                                Snackbar.make(this, "分组不存在。还是能通知，但是通知不在设置的分组内", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notiManager.getNotificationChannel(channelId) == null)
                        notiManager.createNotificationChannel(NotificationChannel(channelId, "发个通知$count", NotificationManager.IMPORTANCE_DEFAULT))
                }

                notiManager.notify(0, mBuilder.build())

                count++
                changeTopText()
            }



            btn_create_group.setOnClickListener {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (rg_group.checkedRadioButtonId) {
                        R.id.rb_0 -> {
                            Snackbar.make(this, "选中分组错误", Snackbar.LENGTH_SHORT).show()
                        }
                        R.id.rb_1 -> {
                            notiManager.createNotificationChannelGroup(NotificationChannelGroup("group1", "分组1"))
                        }
                        R.id.rb_2 -> {
                            notiManager.createNotificationChannelGroup(NotificationChannelGroup("group2", "分组2"))
                        }
                    }

                } else {
                    Snackbar.make(this, "系统低于26", Snackbar.LENGTH_SHORT).show()
                }
            }
            btn_delete_group.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (rg_group.checkedRadioButtonId) {
                        R.id.rb_0 -> {
                            Snackbar.make(this, "选中分组错误", Snackbar.LENGTH_SHORT).show()
                        }
                        R.id.rb_1 -> {
                            notiManager.deleteNotificationChannelGroup("group1")
                        }
                        R.id.rb_2 -> {
                            notiManager.deleteNotificationChannelGroup("group2")
                        }
                    }

                } else {
                    Snackbar.make(this, "系统低于26", Snackbar.LENGTH_SHORT).show()
                }
            }
            btn_delete_groups.setOnClickListener {
                notiManager.notificationChannelGroups.forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notiManager.deleteNotificationChannelGroup(it.id)
                    }
                }

            }

            btn_delete_channel.setOnClickListener { }
            btn_delete_channels.setOnClickListener {
                notiManager.notificationChannels.forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notiManager.deleteNotificationChannel(it.id)
                    }
                }
            }
        }
    }
}