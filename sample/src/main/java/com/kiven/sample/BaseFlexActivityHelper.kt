package com.kiven.sample

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.util.addBtn
import com.kiven.sample.util.addTitle
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * Created by oukobayashi on 2020/7/22.
 */
open class BaseFlexActivityHelper : KActivityHelper() {
    private val flexBoxLayout by lazy {
        FlexboxLayout(mActivity).apply {
            flexWrap = FlexWrap.WRAP
            alignContent = AlignContent.FLEX_START
        }
    }

    /*val addTitle by lazy {
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
    }*/
    fun addTitle(text: String): TextView = flexBoxLayout.addTitle(text)
    fun addBtn(text: String, click: View.OnClickListener): Button = flexBoxLayout.addBtn(text, click)

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
//        setContentView(flexBoxLayout)
        activity.nestedScrollView { addView(flexBoxLayout) }
    }
}