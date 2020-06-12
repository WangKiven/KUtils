package com.kiven.sample

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.AHFileManager
import com.kiven.sample.util.showToast

/**
 * Created by oukobayashi on 2020/6/12.
 */
class AHNativeWidget :KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(flexboxLayout)

        val addTitle = fun(text: String): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)

            return tv
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }

        addTitle("弹窗")
        addView("Dialog", View.OnClickListener { AHDialogTest().startActivity(activity) })
        addView("PopupMenu", View.OnClickListener {
            val popupMenu = PopupMenu(mActivity, it)
            popupMenu.inflate(R.menu.show_log)
            popupMenu.show()
        })
        addView("BottomSheetDialog", View.OnClickListener {
            val dialog = BottomSheetDialog(mActivity)
            dialog.setContentView(R.layout.widget_layout)
            dialog.show()
        })
        addView("BottomSheetDialogFragment", View.OnClickListener {
            showToast()
        })



        addTitle("View")
        addView("ConstraintLayout", View.OnClickListener {
            AHConstraintLayoutTest().startActivity(activity)
        })
        addView("MotionLayout", View.OnClickListener {
            //https://developer.android.google.cn/training/constraint-layout/motionlayout
            showToast()
        })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
    }
}