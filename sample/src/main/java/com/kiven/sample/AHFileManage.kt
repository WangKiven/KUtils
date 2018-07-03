package com.kiven.sample

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.file.KFile
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KString

class AHFileManage : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)


        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(flexboxLayout)

        val addTitle = fun(text: String):TextView {
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

        val logTv = addTitle("日志：\n")
        logTv.setOnClickListener { logTv.text = "日志：\n" }
        val appendLog = fun(text: String) {
            logTv.append(text + "\n")
        }

        addView("获取外部存储和sd卡路径", View.OnClickListener {
            appendLog(KFile.getStoragePaths(mActivity).contentToString())
        })

        addView("路径展示", View.OnClickListener {
            appendLog("Context 获取")
            appendLog("getDir：" + KFile.createFile("tmp", ".img", mActivity.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE))!!.absolutePath)
            appendLog("getExternalFilesDir：" + KFile.createFile("tmp", ".img", mActivity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!)!!.absolutePath)
            appendLog("getDatabasePath：" + KFile.createFile("tmp", ".img", mActivity.getDatabasePath("db"))!!.absolutePath)
            appendLog("cacheDir：" + KFile.createFile("tmp", ".img", mActivity.cacheDir)!!.absolutePath)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appendLog("dataDir：" + KFile.createFile("tmp", ".img", mActivity.dataDir)!!.absolutePath)
            }
            appendLog("\nEnvironment 获取")
            appendLog("getRootDirectory：" + Environment.getRootDirectory().absolutePath)
            appendLog("getDataDirectory：" + Environment.getDataDirectory().absolutePath)
            appendLog("getDownloadCacheDirectory：" + Environment.getDownloadCacheDirectory().absolutePath)
            appendLog("getExternalStorageDirectory：" + Environment.getExternalStorageDirectory().absolutePath)
            appendLog("getExternalStoragePublicDirectory(DIRECTORY_PICTURES)：" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath)
            appendLog("getExternalStorageDirectory：" + Environment.getExternalStorageState())
        })
    }
}