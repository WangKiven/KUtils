package com.kiven.sample

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.view.View
import android.widget.TextView
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.doAsync
import java.io.RandomAccessFile

/**
 * Created by wangk on 2018/3/9.
 */
class AHCPUMemory : KActivityDebugHelper() {

    private var isNext = true

    //    private var cpu = 0.0
//    private var mem = 0.0
    private var resultText = ""
    private val handler = Handler(Handler.Callback {
        findViewById<TextView>(R.id.tv_message).text = resultText
        return@Callback true
    })

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_cpu_memory)

        doAsync {
            val procStatFile = try {
                RandomAccessFile("/proc/stat", "r")
            } catch (e: Exception) {
                KLog.e(e)
                null
            }
            val appStatFile = try {
                RandomAccessFile("/proc/" + Process.myPid() + "/stat", "r")
            } catch (e: Exception) {
                KLog.e(e)
                null
            }
            var lastCpuTime: Double? = null
            var lastAppCpuTime: Double? = null
            val showText = StringBuilder()

            while (isNext) {
                showText.clear()
                // cpu
                if (procStatFile != null && appStatFile != null) {
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
                        showText.append("cpu = ${(appTime - lastAppCpuTime) / (cpuTime - lastCpuTime) * 100.0}%(Permission denied)\n")
                        lastCpuTime = cpuTime.toDouble()
                        lastAppCpuTime = appTime.toDouble()
                    }
                } else {
                    showText.append("cpu = 0%(Permission denied)\n")
                }

                // 内存
                val memInfo = mActivity.activityManager.getProcessMemoryInfo(intArrayOf(Process.myPid()))
                if (memInfo.isNotEmpty()) {
                    val totalPss = memInfo[0].totalPss
                    if (totalPss >= 0) {
                        showText.appendln("mem = ${totalPss / 1024.0} M (总体使用内存，含非java)")
                    }
                }

                val activityManager = mActivity.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
                showText.appendln("最大分配内存${activityManager.memoryClass}M(第一种获取方法)")
                showText.appendln("最大分配内存${activityManager.largeMemoryClass}M(第一种获取方法, 开启largeHeap时)")

                // Runtime 获取的是jVM里的内存情况
                val runtime = Runtime.getRuntime()
                showText.appendln("最大分配内存${toM(runtime.maxMemory())}M(第2种获取方法)")

                showText.appendln("已分配内存${toM(runtime.totalMemory())}M")
                showText.appendln("未使用内存${toM(runtime.freeMemory())}M")
                showText.appendln("已使用内存${toM(runtime.totalMemory() - runtime.freeMemory())}M")


                resultText = showText.toString()
                handler.sendEmptyMessage(0)

                Thread.sleep(500)
            }
        }
    }

    private fun toM(length: Long): Double = (length * 1.0) / (1024 * 1024)

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.btn_run_gc -> {
                Runtime.getRuntime().gc()
            }
            R.id.btn_run_gc2 -> {
                System.gc()
            }
        }
    }

    override fun onDestroy() {
        isNext = false
        super.onDestroy()
    }
}