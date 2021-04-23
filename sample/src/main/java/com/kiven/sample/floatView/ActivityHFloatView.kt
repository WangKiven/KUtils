package com.kiven.sample.floatView

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout

import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import com.kiven.sample.util.showSnack
import org.jetbrains.anko.button
import org.jetbrains.anko.dip
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * 悬浮框
 *
 *
 * 学习文档：TODO http://blog.csdn.net/stevenhu_223/article/details/8504058
 *
 *
 * Created by kiven on 2016/10/31.
 */

class ActivityHFloatView : KActivityHelper() {

    private var activityFloatView: FloatView? = null

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        mActivity.linearLayout {
            orientation = LinearLayout.VERTICAL

            button {
                text = "activity float"
                setOnClickListener {
                    if (activityFloatView == null) {
                        // 应用内悬浮框，生命周期只能在Activity类，退出Activity时，记得关闭悬浮框。否则会报错，甚至崩溃
                        // 这里也可以使用应用外悬浮框，但是记得先开启权限。
                        // 应用外悬浮框，可以在Activity和ServiceFloat中开启
                        // 应用内悬浮框，应该只能在Activity中开启
                        activityFloatView = FloatView(mActivity, mActivity.windowManager, false)
                    }
                    if (activityFloatView!!.isShow) {
                        activityFloatView!!.hideFloat()
                    } else {
                        activityFloatView!!.showFloat()
                    }
                }
            }
            button {
                text = "activity out float"
                setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.canDrawOverlays(mActivity)) {
                            startOverlaySetting()
                        } else {
                            startAppOutFloat()
                        }
                    } else {
                        startAppOutFloat()
                    }
                }
            }
            button {
                text = "设置悬浮框"
                setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        startOverlaySetting()
                    } else {
                        mActivity.showSnack("23以下，该怎么打开呢")
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun startOverlaySetting() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + mActivity.packageName)
        mActivity.startActivity(intent)
    }


    private fun startAppOutFloat() {
        val intent = Intent(mActivity, ServiceFloat::class.java)
        if (KUtil.isRun(ServiceFloat::class.java)) {
            mActivity.stopService(intent)
        } else {
            mActivity.startService(intent)
        }


    }

    override fun onPause() {
        // 只能放在这里，放在onDestroy()，onStop()里面，退出时都会报错。
        if (activityFloatView != null && activityFloatView!!.isShow) {
            activityFloatView!!.hideFloat()
        }
        super.onPause()
    }
}
