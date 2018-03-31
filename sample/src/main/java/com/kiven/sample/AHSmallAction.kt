package com.kiven.sample

import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.jaredrummler.android.processes.AndroidProcesses
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.anim.AHAnim
import com.kiven.sample.service.LiveWallpaper

/**
 * Created by wangk on 2018/3/28.
 */
class AHSmallAction : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(flexboxLayout)

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }
        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("检测与杀死app")

        addView("再运行的进程，系统方法", View.OnClickListener {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            // 通过调用ActivityManager的getRunningAppServicees()方法获得系统里所有正在运行的进程
            val serviceList = am.runningAppProcesses
            serviceList.forEach {
                KLog.i(it.processName)
            }
        })

        addView("再运行的进程，AndroidProcesses", View.OnClickListener {
            val process = AndroidProcesses.getRunningAppProcesses()
            process.forEach {
                KLog.i("name = ${it.name}, pkgName = ${it.packageName}")
            }
        })
        addView("关闭省心宝", View.OnClickListener {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses("com.jeeinc.save.worry")

            // 需要系统权限（Manifest.permission.FORCE_STOP_PACKAGES），无法获取。
            /*val method = am::class.java.getMethod("forceStopPackage", String::class.java)
            method.invoke(am, "com.jeeinc.save.worry")*/
        })
        // TODO: 2018/3/31 ----------------------------------------------------------
        addTitle("壁纸锁屏")
        addView("静态壁纸锁屏", View.OnClickListener {
            val wallPaperManager = WallpaperManager.getInstance(mActivity)

            // FLAG_LOCK 设置锁屏，FLAG_SYSTEM 设置壁纸
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallPaperManager.setResource(R.mipmap.fish, WallpaperManager.FLAG_LOCK)
            } else {
                // 7.0以下，似乎只能设置壁纸。7.0及之后，这个方法似乎同时设置壁纸和锁屏
                wallPaperManager.setResource(R.mipmap.fish)
            }
        })

        addView("动态壁纸", View.OnClickListener {
            val intent = Intent(mActivity, LiveWallpaper::class.java)
            mActivity.startService(intent)
        })

        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("其他")

        // https://developer.android.google.cn/guide/topics/graphics/spring-animation.html
        addView("Animations", View.OnClickListener {
            AHAnim().startActivity(mActivity)
        })
        addView("杀死一个进程杀死一个进程杀死一个进程", View.OnClickListener { })
    }
}