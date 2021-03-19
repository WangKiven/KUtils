package com.kiven.sample.noti

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import com.kiven.sample.util.getInput
import com.kiven.sample.util.listPicker
import com.kiven.sample.util.showSnack
import com.kiven.sample.util.snackbar
import kotlinx.android.synthetic.main.ah_noti_test.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Created by wangk on 2019/5/13.
 */
class AHNotiTest : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_noti_test)

        val root = findViewById<View>(R.id.root)

        val notiManager = NotificationManagerCompat.from(mActivity)

        root.apply {
            val changeTopText = fun() {
                val result = StringBuilder("系统版本号大于26才有通知分组和通知Channel, 当前系统版本号${Build.VERSION.SDK_INT}\n\n")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val groups = notiManager.notificationChannelGroups

                    val channels = notiManager.notificationChannels
                    result.append("有${groups.size}个分组, ${channels.size}个channel\n\n")

                    if (groups.isNotEmpty())
                        result.append("groups:${groups.joinToString { "\n   ${it.id}:${it.name} - {${it.channels.joinToString { it.id }}}" }}\n\n")

                    if (channels.isNotEmpty()) {
                        result.append("channels: ${channels.joinToString {
                            "\n   ${it.id}:${it.name} - ${it.group ?: "没分组"}"
                        }}")
                    }
                }

                tv_count.text = result
            }
            changeTopText()

            var count = 0
            btn_send.setOnClickListener {

                GlobalScope.launch {
                    val channelId = suspendCoroutine<String> {
                        GlobalScope.launch(Dispatchers.Main) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channels = notiManager.notificationChannels
                                if (channels.isNotEmpty()) {
                                    AlertDialog.Builder(mActivity).setItems(channels.map { "${it.id}:${it.name} - ${it.group}" }.toTypedArray()) { dialog, p ->
                                        it.resume(channels[p].id)
                                        dialog.dismiss()
                                    }.show()
                                } else {
                                    it.resume("")
                                }
                            } else {
                                it.resume("default")
                            }
                        }
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        if (channelId.isEmpty()) {
                            mActivity.snackbar("没选择channel, 如果没有的话，请先创建channel")
                            return@launch
                        }

                        val pendingIntent =
                                if (rg_receiver.checkedRadioButtonId == R.id.rb_receiver_activity)
                                    PendingIntent.getActivity(mActivity, 110, Intent(mActivity, ClickNotiActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                                else
                                    PendingIntent.getBroadcast(mActivity, 111, Intent(mActivity, NotificationClickReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

                        val mBuilder = NotificationCompat.Builder(mActivity, channelId)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setTicker("setTicker是什么$channelId $count") // 通知响起时，状态栏显示的内容
                                .setContentTitle("setContentTitle是什么$channelId $count")
                                .setContentText("setContentText是什么$channelId $count")
                                .setNumber(12)
                                .setContentInfo("setContentInfo是什么$channelId $count")
                                .setAutoCancel(true)
                                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)//图标类型
                                .setContentIntent(pendingIntent)
                        /*when (rg_delay.checkedRadioButtonId) {
                            R.id.rb_delay_5s -> {mBuilder.setTimeoutAfter(5000)} // 华为好像没作用
                        }*/

                        when (rg_noti_group.checkedRadioButtonId) {
                            R.id.rb_noti_group0 -> {

                            }
                            R.id.rb_noti_group1 -> {
                                mBuilder.setGroup("notiGroup1")
                            }
                            R.id.rb_noti_group2 -> {
                                mBuilder.setGroup("notiGroup2")
//                                mBuilder.setGroupSummary(true)
                            }
                        }

                        when (rg_delay.checkedRadioButtonId) {
                            R.id.rb_delay_5s -> delay(5000)
                        }

                        notiManager.notify(count, mBuilder.build())
                        count++
                        changeTopText()

                        runUI {
                            BadgeUtil.setBadgeCount(mActivity, 5, R.drawable.ic_launcher)
                        }
                    }
                }
            }



            btn_create_group.setOnClickListener {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mActivity.getInput("分组Id") { groupId ->
                        mActivity.getInput("分组name") { groupName ->
                            val channel = NotificationChannelGroup(groupId.toString(), groupName)
                            notiManager.createNotificationChannelGroup(channel)

                            changeTopText()
                        }
                    }

                } else {
                    Snackbar.make(this, "系统低于26", Snackbar.LENGTH_SHORT).show()
                }
            }
            btn_delete_group.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val groups = notiManager.notificationChannelGroups

                    AlertDialog.Builder(mActivity).setItems(groups.map { "${it.id}:${it.name}" }.toTypedArray()) { dialog, p ->
                        notiManager.deleteNotificationChannelGroup(groups[p].id)
                        changeTopText()
                        dialog.dismiss()
                    }.show()

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

                changeTopText()
            }

            btn_create_channel.setOnClickListener {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mActivity.getInput("channelId") { channelId ->
                        mActivity.getInput("channelName") { channelName ->
                            val groups = notiManager.notificationChannelGroups

                            AlertDialog.Builder(mActivity).setTitle("选择分组").setItems(groups.map { "${it.id}:${it.name}" }.toTypedArray() + "无分组") { dialog, p ->
                                val channel = NotificationChannel(channelId.toString(), "$channelName", NotificationManager.IMPORTANCE_DEFAULT)
                                channel.enableLights(true)
                                channel.lightColor = Color.GREEN
                                if (p < groups.size) {
                                    channel.group = groups[p].id
                                }
//                        channel.setSound()
                                channel.enableVibration(true) // 震动
                                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC // 锁屏可见
                                channel.setShowBadge(true)
                                channel.description = "这是一个测试用的通知分类" // 描述
                                try {
                                    channel.setAllowBubbles(true) // 小红点显示。华为崩了，所以放try里面
                                } catch (e: NoSuchMethodError) {
                                }
                                channel.setBypassDnd(true) // 免打扰模式下，允许响铃或震动

                                notiManager.createNotificationChannel(channel)


                                changeTopText()

                                dialog.dismiss()
                            }.show()
                        }
                    }
                } else {
                    Snackbar.make(this, "系统低于26", Snackbar.LENGTH_SHORT).show()
                }
            }

            btn_delete_channel.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channels = notiManager.notificationChannels

                    AlertDialog.Builder(mActivity).setItems(channels.map { "${it.id}:${it.name} - ${it.group}" }.toTypedArray()) { dialog, p ->
                        notiManager.deleteNotificationChannel(channels[p].id)
                        changeTopText()

                        dialog.dismiss()
                    }.show()

                } else {
                    Snackbar.make(this, "系统低于26", Snackbar.LENGTH_SHORT).show()
                }
            }
            btn_delete_channels.setOnClickListener {
                notiManager.notificationChannels.forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notiManager.deleteNotificationChannel(it.id)
                    }
                }
                changeTopText()
            }

            btn_noti_setting.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notiManager.areNotificationsEnabled()) {
                        val channels = notiManager.notificationChannels
                        mActivity.listPicker("选择channel", channels.map { "${it.id}:${it.name} - ${it.group}" }.toTypedArray()) {
                            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, mActivity.packageName)
                            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channels[it].id)
                            mActivity.startActivity(intent)
                        }
                    } else { // todo 通知被关闭，不能打开通知管理界面，需要打开应用详情界面去设置
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        mActivity.startActivity(intent)
                    }
                } else {
                    val intent = Intent()
                    intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    intent.putExtra("app_package", mActivity.packageName)
                    intent.putExtra("app_uid", mActivity.applicationInfo.uid)
                    mActivity.startActivity(intent)
                }
            }

            btn_noti_listener_setting.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    mActivity.startActivity(intent)
                } else {
                    mActivity.showSnack("系统版本太小")
                }

            }

            btn_noti_listener_setting_check.setOnClickListener {
                // 网上找到两种方式

                // Settings.Secure.ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
                // ENABLED_NOTIFICATION_LISTENERS 已被标记 @Deprecated @UnsupportedAppUsage
                /*val flat = Settings.Secure.getString(activity.contentResolver, "enabled_notification_listeners")

                var result = false
                if (!flat.isNullOrBlank()) {
                    val names = flat.split(":")
                    names.forEach {
                        val cn = ComponentName.unflattenFromString(it)
                        if (cn != null && cn.packageName == activity.packageName)
                            result = true
                    }
                }*/

                var result = false
                NotificationManagerCompat.getEnabledListenerPackages(activity)
                        .forEach {
                            if (it == activity.packageName)
                                result = true
                        }

                mActivity.showSnack(if (result) "当前状态：开启" else "当前状态：关闭")
            }

            btn_noti_listener_voice_status.setOnClickListener {
                MyNotificationListenerService.isReadNoti = !MyNotificationListenerService.isReadNoti
                activity.snackbar("已设置状态(isReadNoti) = ${MyNotificationListenerService.isReadNoti}")
            }

            btn_app_setting.setOnClickListener {

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", context.packageName, null)
                mActivity.startActivity(intent)
            }
        }
    }
}