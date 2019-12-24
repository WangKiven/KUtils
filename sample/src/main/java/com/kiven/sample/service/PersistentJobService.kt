package com.kiven.sample.service

import android.annotation.TargetApi
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KUtil

/**
 * Created by oukobayashi on 2019-12-23.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class PersistentJobService: JobService() {
    override fun onStopJob(params: JobParameters?): Boolean {
        KLog.i("PersistentJobService 开启任务")
        KUtil.startService(PersistentService::class.java)
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        KUtil.startService(PersistentService::class.java)
        return false
    }
}