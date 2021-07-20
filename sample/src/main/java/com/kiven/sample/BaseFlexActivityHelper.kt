package com.kiven.sample

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.util.addBtn
import com.kiven.sample.util.addTitle

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

    fun addTitle(text: String): TextView = flexBoxLayout.addTitle(text)
    fun addBtn(text: String, click: View.OnClickListener): Button = flexBoxLayout.addBtn(text, click)

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val scroll = NestedScrollView(activity)
        scroll.addView(flexBoxLayout)
        setContentView(scroll)
    }
}