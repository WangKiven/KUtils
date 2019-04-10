package com.kiven.sample

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity

class AHDialogTest : KActivityHelper() {
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

        // TODO: 2019/4/10 ------------------------
        addTitle("状态栏配置 - 做不到状态栏透明，只能做到半透明，或者直接全屏")
        addView("透明状态栏，无继承style", View.OnClickListener {
            val dialo = object : Dialog(activity, R.style.Dialog_Test1){
                init {
                    setContentView(R.layout.ah_dialog_test)

//                    window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                }
            }
            dialo.show()
        })


        addView("透明状态栏，继承style", View.OnClickListener {
            val dialo = object : Dialog(activity, R.style.Dialog_Test1){
                init {
                    setContentView(R.layout.ah_dialog_test)

//                    window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                }
            }
            dialo.show()
        })
    }
}