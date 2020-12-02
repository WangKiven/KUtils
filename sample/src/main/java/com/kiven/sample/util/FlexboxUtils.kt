package com.kiven.sample.util

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout

/**
 * Created by wangk on 2020/12/2.
 */
fun FlexboxLayout.addTitle(text: String): TextView {
    val tv = TextView(context)
    tv.text = text
    tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)

    addView(tv)
    return tv
}

fun FlexboxLayout.addBtn(text: String, click: View.OnClickListener): Button {
    val btn = Button(context)
    btn.text = text
    btn.setOnClickListener(click)

    addView(btn)
    return btn
}