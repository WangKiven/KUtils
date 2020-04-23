package com.kiven.sample.arcore

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KGranting
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * https://developers.google.cn/ar/develop/java/enable-arcore
 */
class AHARCore : KActivityDebugHelper() {
    var supportARCoreTextView: TextView? = null
    var isSupport = -1

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

        val addTitle = fun(text: String): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
            return tv
        }

        val addView = fun(text: String, click: View.OnClickListener): Button {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
            return btn
        }

        addTitle("ARCore测试，国内需要先在应用商城下载ARCore，否则显示不支持")

        //supportARCoreBtn = addView("点我检测", View.OnClickListener { checkARCore() })
        supportARCoreTextView = addTitle("ARCore检测中")
        checkARCore()

        addView("go", View.OnClickListener {
            KGranting.requestPermissions(activity, 233, Manifest.permission.CAMERA) {
                if (it) {
                    when(ArCoreApk.getInstance().requestInstall(activity, true)) {
                        ArCoreApk.InstallStatus.INSTALLED -> {
                            val session = Session(activity)
                        }
                        ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                            //如果 requestInstall() 返回 INSTALL_REQUESTED，则当前 Activity 将暂停，并提示用户安装或更新 ARCore：
                        }
                        else -> {}
                    }
                }
            }
        })
    }

    private fun checkARCore() {
        val availability = ArCoreApk.getInstance().checkAvailability(mActivity)
        if (availability.isTransient) {
            Handler().postDelayed(Runnable {
                checkARCore()
            }, 200)
        }

        if (availability.isSupported) {
            supportARCoreTextView?.text = "检测结果：支持ARCore ${availability.isTransient}"
            isSupport = 1
        } else {
            supportARCoreTextView?.text = "检测结果：不支持ARCore ${availability.isTransient}"
            isSupport = 0
        }
    }
}