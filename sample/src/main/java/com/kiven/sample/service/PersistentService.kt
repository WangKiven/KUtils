package com.kiven.sample.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PersistentService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val channelId = "persistentChannel"
    private val channelName = "KUtils后台通知服务"

    override fun onCreate() {
        super.onCreate()


        // 前台保活处理
        createChannel()

        val mBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("后台服务已开启-最新消息及时达")

        startForeground(System.currentTimeMillis().toInt(), mBuilder.build())

        // 周期任务保活
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
            if (jobScheduler != null) {
                val jobId = 10001
                if (jobScheduler.allPendingJobs.firstOrNull { it.id == jobId } == null) {
                    val jobInfo = JobInfo.Builder(jobId, ComponentName(this, PersistentJobService::class.java.name))
//                            .setPeriodic(15 * 60 * 1000 + 10)// 据说周期最小15分钟
                    jobInfo.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS) //执行的最小延迟时间
                    jobInfo.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)// 执行的最长延时时间
                    jobInfo.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR) //线性重试方案

                    jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                    jobInfo.setRequiredNetwork(NetworkRequest.Builder().)
                    jobInfo.setRequiresCharging(true)// 当插入充电器，执行该任务
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        jobInfo.setRequiresBatteryNotLow(true)
                    }

                    jobScheduler.schedule(jobInfo.build())
                }

            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        GlobalScope.launch {
            repeat(Int.MAX_VALUE) {
                KLog.i("任务运行 $it")
                delay(15000)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notiManager = NotificationManagerCompat.from(this)

            notiManager.notificationChannels.forEach {
                if (it.id == channelId) {
                    return
                }
            }

            val channel = NotificationChannel(channelId, "$channelName", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(false)
//            channel.lightColor = Color.GREEN
//                        channel.setSound()
            channel.setSound(null, null)
            channel.enableVibration(false) // 震动
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC // 锁屏可见
            channel.setShowBadge(true)
            channel.description = "这是一个测试用的通知分类" // 描述
            try {
                channel.setAllowBubbles(true) // 小红点显示。华为崩了，所以放try里面
            }catch (e:NoSuchMethodError){}
            channel.setBypassDnd(true) // 免打扰模式下，允许响铃或震动

            notiManager.createNotificationChannel(channel)
        }
    }
}
