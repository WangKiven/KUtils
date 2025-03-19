package com.kiven.sample.theme

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R

/**
 * Created by wangk on 2019/5/29.
 */
class AHTheme : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START
        flexboxLayout.fitsSystemWindows = true

        setContentView(NestedScrollView(activity).apply {
            addView(flexboxLayout)
        })

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addBtn = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }

        addBtn("Dialog的全屏", View.OnClickListener {
            val textView = TextView(mActivity)
            textView.text = "Hello World!"
            textView.setBackgroundColor(Color.WHITE)
            val dialog = AlertDialog.Builder(mActivity, R.style.Dialog_Nobackground)
                    .setView(textView).create()
            dialog.show()
        })
        addBtn("设置主题", View.OnClickListener { AHThemeDemo().startActivity(mActivity) })
        addBtn("", View.OnClickListener {  })
        addBtn("", View.OnClickListener {  })
        addBtn("", View.OnClickListener {  })
        addBtn("", View.OnClickListener {  })
    }
}