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
import com.kiven.sample.libs.AHTextViewDemo
import com.kiven.sample.util.addBtn
import com.kiven.sample.util.showToast

/**
 * Created by oukobayashi on 2020/6/12.
 */
class AHNativeWidget :BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        addTitle("弹窗")
        addBtn("Dialog") { AHDialogTest().startActivity(activity) }
        addBtn("PopupMenu") {
            val popupMenu = PopupMenu(mActivity, it)
            popupMenu.inflate(R.menu.show_log)
            popupMenu.show()
        }
        addBtn("BottomSheetDialog") {
            val dialog = BottomSheetDialog(mActivity)
            dialog.setContentView(R.layout.widget_layout)
            dialog.show()
        }
        addBtn("BottomSheetDialogFragment") {
            showToast()
        }



        addTitle("View")
        addBtn("ConstraintLayout") {
            AHConstraintLayoutTest().startActivity(activity)
        }
        addBtn("MotionLayout") {
            //https://developer.android.google.cn/training/constraint-layout/motionlayout
            showToast()
        }
        addBtn("TextView风格") { AHTextViewDemo().startActivity(activity) }
        addBtn("Toolbar") { AHHelperTest().startActivity(activity) }
        addBtn("") {}
        addBtn("") {}
        addBtn("") {}
    }
}