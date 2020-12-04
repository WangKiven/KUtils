package com.kiven.sample.libs

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.flyco.animation.FlipEnter.FlipRightEnter
import com.flyco.dialog.listener.OnBtnClickL
import com.flyco.dialog.widget.ActionSheetDialog
import com.flyco.dialog.widget.MaterialDialog
import com.flyco.dialog.widget.NormalDialog
import com.flyco.dialog.widget.NormalListDialog
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.util.snackbar

/**
 * Created by wangk on 2020/12/4.
 *
 * https://github.com/H07000223/FlycoDialog_Master
 */
class AHFlycoDialogLib : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        addBtn("NormalDialog", View.OnClickListener {
            val dialog = NormalDialog(mActivity).style(NormalDialog.STYLE_TWO).title("温馨提示").content("打开的NormalDialog")
                    .btnText("确定1", "取消1")
            dialog.setOnDismissListener { mActivity.snackbar("Dismiss") }
            dialog.setOnCancelListener { mActivity.snackbar("Cancel") }
            dialog.setOnBtnClickL(OnBtnClickL {
                mActivity.snackbar("click1")
            }, OnBtnClickL {
                mActivity.snackbar("click2")
            })
            dialog.show()
        })
        addBtn("MaterialDialog", View.OnClickListener {
            val dialog = MaterialDialog(mActivity).title("温馨提示").content("打开的MaterialDialog")
                    .btnNum(3).btnText("忽略", "确定1", "取消1")
            dialog.setOnDismissListener { mActivity.snackbar("Dismiss") }
            dialog.setOnCancelListener { mActivity.snackbar("Cancel") }
            dialog.setOnBtnClickL(OnBtnClickL { mActivity.snackbar("click1") }
                    , OnBtnClickL { mActivity.snackbar("click2") }, OnBtnClickL { mActivity.snackbar("click3") })
            dialog.show()
        })
        addBtn("NormalListDialog", View.OnClickListener {
            val dialog = NormalListDialog(mActivity, arrayOf("收藏", "打包", "下载", "删除")).apply {
                setOnOperItemClickL { parent, view, position, id -> mActivity.snackbar("click$position");dismiss() }
                title("请选择")
            }
            dialog.show()
        })
        addBtn("ActionSheetDialog", View.OnClickListener {
            val dialog = ActionSheetDialog(mActivity, arrayOf("收藏", "打包", "下载", "删除"), it)
                    .cancelText("取消吗？？？").title("选吧！！！")
            dialog.setOnOperItemClickL { parent, view, position, id -> mActivity.snackbar("click$position");dialog.dismiss() }
            dialog.setOnCancelListener { mActivity.snackbar("Cancel") }
            dialog.show()

        })
        addBtn("BubblePopup", View.OnClickListener {
            val pop = SimpleCustomPop(mActivity)
            pop.anchorView(it)
            pop.gravity(Gravity.BOTTOM)
            pop.showAnim(FlipRightEnter())
            pop.show()
        })
    }
}