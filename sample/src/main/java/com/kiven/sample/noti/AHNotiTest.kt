package com.kiven.sample.noti

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KImage
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
                    result.append("有${groups.size}个分组, ${channels.size}个channel")
                }

                tv_count.text = result
            }
            changeTopText()

            var count = 0
            btn_send.setOnClickListener {
                val pendingIntent = PendingIntent.getActivity(mActivity, 110, Intent(mActivity, ClickNotiActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                val channelId = when (rg_channel.checkedRadioButtonId) {
                    R.id.rb_channel1 -> "channel1"
                    else -> "channel2"
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notiManager.getNotificationChannel(channelId) == null){
                        val channel = NotificationChannel(channelId, "我是通知:$channelId", NotificationManager.IMPORTANCE_DEFAULT)
                        channel.enableLights(true)
                        channel.lightColor = Color.GREEN
//                        channel.setSound()
                        channel.enableVibration(true) // 震动
                        notiManager.createNotificationChannel(channel)
                    }
                }

                val mBuilder = NotificationCompat.Builder(mActivity, channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker("setTicker是什么$channelId $count") // 通知响起时，状态栏显示的内容
                        .setContentTitle("setContentTitle是什么$channelId $count")
                        .setContentText("setContentText是什么$channelId $count")
                        .setNumber(12)
                        .setContentInfo("setContentInfo是什么$channelId $count")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (rg_group.checkedRadioButtonId) {
                        R.id.rb_0 -> {
                        }
                        R.id.rb_1 -> {
                            mBuilder.setGroup("group1")

                            if (notiManager.notificationChannelGroups.firstOrNull { it.id == "group1" } == null) {
                                Snackbar.make(this, "分组不存在。还是能通知，但是通知不在设置的分组内", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        R.id.rb_2 -> {
                            mBuilder.setGroup("group2")

                            if (notiManager.notificationChannelGroups.firstOrNull { it.id == "group2" } == null) {
                                Snackbar.make(this, "分组不存在。还是能通知，但是通知不在设置的分组内", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                notiManager.notify(count, mBuilder.build())
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
                changeTopText()
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
                changeTopText()
            }
            btn_delete_groups.setOnClickListener {
                notiManager.notificationChannelGroups.forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notiManager.deleteNotificationChannelGroup(it.id)
                    }
                }

                changeTopText()
            }

            btn_delete_channel.setOnClickListener {
                when (rg_channel.checkedRadioButtonId) {
                    R.id.rb_channel1 -> notiManager.deleteNotificationChannel("channel1")
                    R.id.rb_channel2 -> notiManager.deleteNotificationChannel("channel2")
                }
                changeTopText()
            }
            btn_delete_channels.setOnClickListener {
                notiManager.notificationChannels.forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notiManager.deleteNotificationChannel(it.id)
                    }
                }
                changeTopText()
            }
        }
    }
}