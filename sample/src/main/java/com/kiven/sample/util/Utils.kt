package com.kiven.sample.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.flyco.dialog.widget.NormalListDialog
import com.google.android.material.snackbar.Snackbar
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAlertDialogHelper

/**
 * Created by wangk on 2019/5/14.
 */
/// 获取一个输入
fun Activity.getInput(inputName: String, action: (CharSequence) -> Unit) {
    getInput(inputName, "", action)
}

fun Activity.getInput(inputName: String, text: String, action: (CharSequence) -> Unit) {
    val et = EditText(this)
    et.setText(text)
    AlertDialog.Builder(this)
            .setTitle(inputName)
            .setView(et)
            .setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
            .setPositiveButton("确定") { dialog, _ ->
                val teamName = et.text.trim()
                if (teamName.isNotBlank()) {
                    action(teamName)
                } else {
                    snackbar("$inputName 不能为空")
                }
                dialog.dismiss()
            }
            .show()
}

/// 显示提示
fun Activity.snackbar(text: String) {
    Snackbar.make(window.decorView, text, Snackbar.LENGTH_SHORT).show()
    KLog.i(text)
}

/// 选择列表
fun Activity.listPicker(title: String, items: Array<String>, action: (Int) -> Unit) {
    AlertDialog.Builder(this).setItems(items) { dialog, p ->
        action(p)
        dialog.dismiss()
    }.show()
}

/// 打电话
fun Activity.callPhone(phoneNum: String) {
    startActivity(Intent().apply {
        action = Intent.ACTION_CALL
        data = Uri.parse("tel:$phoneNum")
    })
}

fun showTip(word: String) {
    KLog.d(word)
}

fun Activity.showDialog(word: String) {
    KAlertDialogHelper.Show1BDialog(this, word)
    Log.i("ULog_default", word)
}

fun Activity.showListDialog(list: List<String>, onClickItem: (Int, String) -> Unit) {
    showListDialog(list.toTypedArray(), onClickItem)
}

fun Activity.showListDialog(list: Array<String>, onClickItem: (Int, String) -> Unit) {
    val dialog = NormalListDialog(this, list)
    dialog.isTitleShow(false)
    dialog.setOnOperItemClickL { _, _, position, _ -> onClickItem(position, list[position]) }
    dialog.show()
}

fun Activity.showSnack(word: String) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), word, Snackbar.LENGTH_LONG).show()
    Log.i("ULog_default", word)
}
