package com.kiven.sample.floatView

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout

import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import com.kiven.sample.util.snackbar
import org.jetbrains.anko.button
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

    private var isShow = false
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
//        setContentView(R.layout.activity_h_float_view)
        mActivity.linearLayout {
            orientation = LinearLayout.VERTICAL

            button {
                text = "activity float"
                setOnClickListener {
                    if (activityFloatView == null) {
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
                        mActivity.snackbar("23以下，该怎么打开呢")
                    }
                }
            }
        }
    }

    private fun startOverlaySetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + mActivity.packageName)
            mActivity.startActivity(intent)
        }
    }


    private fun startAppOutFloat() {
        val intent = Intent(mActivity, ServiceFloat::class.java)
        if (isShow) {
            mActivity.stopService(intent)
        } else {
            mActivity.startService(intent)
        }
        isShow = !isShow
    }

    /*override fun onPause() {
        if (activityFloatView != null && activityFloatView!!.isShow) {
            activityFloatView!!.hideFloat()
        }
        super.onPause()
    }*/
}
