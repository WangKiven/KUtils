package com.kiven.sample.util

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.flyco.dialog.widget.ActionSheetDialog
import com.flyco.dialog.widget.NormalListDialog
import com.google.android.material.snackbar.Snackbar
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KContext
import com.kiven.sample.R
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream

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
    KLog.i(word)
}

fun Activity.showDialog(word: String) {
    KAlertDialogHelper.Show1BDialog(this, word)
    Log.i("ULog_default", word)
}

fun Activity.showListDialog(list: List<String>, autoClose: Boolean, onClickItem: (Int, String) -> Unit) {
    showListDialog(list.toTypedArray(), autoClose, onClickItem)
}

fun Activity.showListDialog(list: List<String>, onClickItem: (Int, String) -> Unit) {
    showListDialog(list.toTypedArray(), true, onClickItem)
}

fun Activity.showListDialog(list: Array<String>, autoClose: Boolean, onClickItem: (Int, String) -> Unit) {
    val dialog = NormalListDialog(this, list)
    dialog.isTitleShow(false)
    dialog.setOnOperItemClickL { _, _, position, _ ->
        onClickItem(position, list[position])

        if (autoClose)
            dialog.dismiss()
    }
    dialog.show()
}

fun Activity.showListDialog(list: Array<String>, onClickItem: (Int, String) -> Unit) {
    showListDialog(list, true, onClickItem)
}

fun Activity.showBottomSheetDialog(list: List<String>, onClickItem: (Int, String) -> Unit) {
    showBottomSheetDialog(list.toTypedArray(), onClickItem)
}

fun Activity.showBottomSheetDialog(list: Array<String>, onClickItem: (Int, String) -> Unit) {

    val sheetDialog = ActionSheetDialog(this, list, null)
    sheetDialog.setOnOperItemClickL { _, _, position, _ ->
        onClickItem(position, list[position])
        sheetDialog.dismiss()
    }
    sheetDialog.show()
}

fun Activity.showSnack(word: String) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), word, Snackbar.LENGTH_LONG).show()
    Log.i("ULog_default", word)
}

fun Activity.showImageDialog(path: String?) {
    val bitmap = BitmapFactory.decodeFile(path ?: "")

    if (bitmap == null) {
        showDialog("获取图片失败,路径：$path")
    } else {
        showImageDialog(bitmap)
    }
}

fun Activity.showImageDialog(uri: Uri) {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }else{
        MediaStore.Images.Media.getBitmap(contentResolver, uri)
    }

    if (bitmap == null) {
        showDialog("获取图片失败,路径：${uri}")
    } else {
        showImageDialog(bitmap)
    }
}

fun Activity.showImageDialog(bitmap: Bitmap) {
    val dialog = object : Dialog(this, R.style.Dialog) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val imageView = ImageView(context)
            setContentView(imageView)
            imageView.setImageBitmap(bitmap)
        }
    }
    dialog.show()
}

private var preTime = 0L
/**
 * toast提示 仅用于无障碍模块
 */
fun showToast(word: String) {
    // Toast.LENGTH_SHORT 4s
    // Toast.LENGTH_LONG 7s
    val curTime = System.currentTimeMillis()
    if (curTime - preTime > 4000L) {
        Toast.makeText(KContext.getInstance(), word, Toast.LENGTH_SHORT).show()
        preTime = curTime
    }
}