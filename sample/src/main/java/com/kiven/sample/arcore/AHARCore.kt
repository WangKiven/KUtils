package com.kiven.sample.arcore

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
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * https://developers.google.cn/ar/develop/java/enable-arcore
 */
class AHARCore : KActivityDebugHelper() {
    var supportARCoreBtn: TextView? = null

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

        addTitle("ARCore测试，需要先在应用商城下载ARCore")

        //supportARCoreBtn = addView("点我检测", View.OnClickListener { checkARCore() })
        supportARCoreBtn = addTitle("ARCore检测中")
        checkARCore()
    }

    private fun checkARCore() {
        val availability = ArCoreApk.getInstance().checkAvailability(mActivity)
        if (availability.isTransient) {
            Handler().postDelayed(Runnable {
                checkARCore()
            }, 200)
        }

        if (availability.isSupported) {
            supportARCoreBtn?.text = "检测结果：支持ARCore ${availability.isTransient}"
        } else {
            supportARCoreBtn?.text = "检测结果：不支持ARCore ${availability.isTransient}"
        }
    }
}