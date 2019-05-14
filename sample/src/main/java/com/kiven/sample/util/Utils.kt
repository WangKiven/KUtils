package com.kiven.sample.util

import android.app.Activity
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar

/**
 * Created by wangk on 2019/5/14.
 */
/// 获取一个输入
fun Activity.getInput(inputName: String, action: (CharSequence) -> Unit) {
    val et = EditText(this)
    AlertDialog.Builder(this)
            .setTitle(inputName)
            .setView(et)
            .setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
            .setPositiveButton("确定") { dialog, _ ->
                val teamName = et.text.trim()
                if (teamName.isNotBlank()) {
                    action(teamName)
                } else {
                    toast("$inputName 不能为空")
                }
                dialog.dismiss()
            }
            .show()
}

/// 显示提示
fun Activity.toast(text: String) {
    Snackbar.make(window.decorView, text, Snackbar.LENGTH_SHORT).show()
}

/// 选择列表
fun Activity.listPicker(title: String, items: Array<String>, action: (Int) -> Unit) {
    AlertDialog.Builder(this).setItems(items) { dialog, p ->
        action(p)
        dialog.dismiss()
    }.show()
}