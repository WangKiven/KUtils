package com.kiven.sample

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity

/**
 * Created by oukobayashi on 2020/7/22.
 */
open class BaseFlexActivityHelper : KActivityDebugHelper() {
    private val flexBoxLayout by lazy {
        FlexboxLayout(mActivity)
    }

    val addTitle by lazy {
        fun(text: String): TextView {
            val tv = TextView(mActivity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexBoxLayout.addView(tv)
            return tv
        }
    }

    val addBtn by lazy {
        fun(text: String, click: View.OnClickListener): Button {
            val btn = Button(mActivity)
            btn.text = text
            btn.setOnClickListener(click)
            flexBoxLayout.addView(btn)
            return btn
        }
    }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(flexBoxLayout)
    }
}