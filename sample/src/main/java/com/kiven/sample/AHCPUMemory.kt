package com.kiven.sample

import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.widget.TextView
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.doAsync
import java.io.RandomAccessFile

/**
 * Created by wangk on 2018/3/9.
 */
class AHCPUMemory : KActivityHelper() {

    private var isNext = true

    private var cpu = 0.0
    private var mem = 0.0
    private val handler = Handler(Handler.Callback {
        findViewById<TextView>(R.id.tv_message).text = "cpu = ${cpu}%\nmem = $mem M"
        return@Callback true
    })

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_cpu_memory)

        doAsync {
            val procStatFile = RandomAccessFile("/proc/stat", "r")
            val appStatFile = RandomAccessFile("/proc/" + Process.myPid() + "/stat", "r")
            var lastCpuTime:Double? = null
            var lastAppCpuTime:Double? = null

            while (isNext) {
                // cpu
                procStatFile.seek(0)
                appStatFile.seek(0)

                val procStatString = procStatFile.readLine()
                val appStatString = appStatFile.readLine()
                val procStats = procStatString.split(" ")
                val appStats = appStatString.split(" ")
                val cpuTime = procStats[2].toLong() + procStats[3].toLong()
                +procStats[4].toLong() + procStats[5].toLong()
                +procStats[6].toLong() + procStats[7].toLong()
                +procStats[8].toLong()
                val appTime = appStats[13].toLong() + appStats[14].toLong()
                if (lastCpuTime == null || lastAppCpuTime == null) {
                    lastCpuTime = cpuTime.toDouble()
                    lastAppCpuTime = appTime.toDouble()
                } else {
                    cpu = (appTime - lastAppCpuTime) / (cpuTime - lastCpuTime) * 100.0
                    lastCpuTime = cpuTime.toDouble()
                    lastAppCpuTime = appTime.toDouble()
                }


                // 内存
                val memInfo = mActivity.activityManager.getProcessMemoryInfo(intArrayOf(Process.myPid()))
                if (memInfo.isNotEmpty()) {
                    val totalPss = memInfo[0].totalPss
                    if (totalPss >= 0) {
                        mem = totalPss / 1024.0
                    }
                }

                handler.sendEmptyMessage(0)
            }
        }
    }

    override fun onDestroy() {
        isNext = false
        super.onDestroy()
    }
}