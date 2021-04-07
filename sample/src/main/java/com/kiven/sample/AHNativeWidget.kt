package com.kiven.sample

import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.helperTest.AHHelperTest
import com.kiven.sample.libs.AHTextViewDemo
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