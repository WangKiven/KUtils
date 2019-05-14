package com.kiven.sample.noti

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import com.kiven.sample.util.getInput
import com.kiven.sample.util.listPicker
import com.kiven.sample.util.toast
import kotlinx.android.synthetic.main.ah_noti_test.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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
                            mActivity.toast("没选择channel, 如果没有的话，请先创建channel")
                            return@launch
                        }

                        val pendingIntent = PendingIntent.getActivity(mActivity, 110, Intent(mActivity, ClickNotiActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

                        val mBuilder = NotificationCompat.Builder(mActivity, channelId)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setTicker("setTicker是什么$channelId $count") // 通知响起时，状态栏显示的内容
                                .setContentTitle("setContentTitle是什么$channelId $count")
                                .setContentText("setContentText是什么$channelId $count")
                                .setNumber(12)
                                .setContentInfo("setContentInfo是什么$channelId $count")
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)

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

                        notiManager.notify(count, mBuilder.build())
                        count++
                        changeTopText()
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

            btn_app_setting.setOnClickListener {

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", context.packageName, null)
                mActivity.startActivity(intent)
            }
        }
    }

    /*fun getInput(inputName: String, action: (CharSequence) -> Unit) {
        val et = EditText(mActivity)
        AlertDialog.Builder(mActivity)
                .setTitle(inputName)
                .setView(et)
                .setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
                .setPositiveButton("确定") { dialog, _ ->
                    val teamName = et.text.trim()
                    if (teamName.isNotBlank()) {
                        action(teamName)
                    } else {
                        mActivity.toast("$inputName 不能为空")
                    }
                    dialog.dismiss()
                }
                .show()
    }*/
}