package com.kiven.sample.util

import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import java.util.*

/**
 * Created by wangk on 2019/5/14.
 */
/// 获取一个输入
fun Activity.getInput(
    inputName: String,
    text: String = "",
    inputType: Int = EditorInfo.TYPE_CLASS_TEXT,
    action: (CharSequence) -> Unit
) {
    getInput(inputName, text, inputType, {}, action)
}

fun Activity.getInput(
    inputName: String,
    text: String = "",
    inputType: Int = EditorInfo.TYPE_CLASS_TEXT,
    onCancel: () -> Unit,
    action: (CharSequence) -> Unit
) {
    val et = EditText(this)
    et.setText(text)
    et.inputType = inputType
    AlertDialog.Builder(this)
        .setTitle(inputName)
        .setView(et)
        .setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
        .setPositiveButton("确定") { dialog, _ ->
            val teamName = et.text.trim()
            if (teamName.isNotBlank()) {
                action(teamName)
            } else {
                showSnack("$inputName 不能为空")
            }
            dialog.dismiss()
        }.setOnCancelListener {
            onCancel()
        }
        .show()
}

/// 选择列表
/*fun Activity.listPicker(title: String, items: Array<String>, action: (Int) -> Unit) {
    AlertDialog.Builder(this).setItems(items) { dialog, p ->
        action(p)
        dialog.dismiss()
    }.show()
}*/

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
    Log.i(KLog.getTag(), word)
}

fun Activity.showDialogClose(word: String) {
    KAlertDialogHelper.Show1BDialog(this, word) {
        finish()
    }
    Log.i(KLog.getTag(), word)
}

fun Activity.showListDialog(
    list: List<String>,
    autoClose: Boolean,
    onClickItem: (Int, String) -> Unit
) {
    showListDialog(list.toTypedArray(), autoClose, onClickItem)
}

fun Activity.showListDialog(list: List<String>, onClickItem: (Int, String) -> Unit) {
    showListDialog(list.toTypedArray(), true, onClickItem)
}

fun Activity.showListDialog(
    list: Array<String>,
    autoClose: Boolean,
    onClickItem: (Int, String) -> Unit
) {
//    val dialog = NormalListDialog(this, list)
//    dialog.isTitleShow(false)
//    dialog.setOnOperItemClickL { _, _, position, _ ->
//        onClickItem(position, list[position])
//
//        if (autoClose)
//            dialog.dismiss()
//    }
//    dialog.show()

    AlertDialog.Builder(this).setItems(list) { _, position ->
        onClickItem(position, list[position])
    }.setCancelable(autoClose).show()
}

fun Activity.showListDialog(list: Array<String>, onClickItem: (Int, String) -> Unit) {
    showListDialog(list, true, onClickItem)
}

fun Activity.showBottomSheetDialog(list: List<String>, onClickItem: (Int, String) -> Unit) {
    showBottomSheetDialog(list.toTypedArray(), {}, onClickItem)
}

fun Activity.showBottomSheetDialog(
    list: Array<String>,
    onCancel: () -> Unit = {},
    onClickItem: (Int, String) -> Unit
) {
    BottomSheetDialog(this).apply {
        setContentView(LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.VERTICAL
            for ((i, s) in list.withIndex()) {
                addView(TextView(context).apply {
                    text = s
                    setPadding(KUtil.dip2px(10f))
                    gravity = Gravity.CENTER
                    setOnClickListener {
                        onClickItem(i, s)
                        dismiss()
                    }
                })
            }
        })
        setOnCancelListener { onCancel() }
    }.show()
//    val sheetDialog = ActionSheetDialog(this, list, null)
//    sheetDialog.setOnOperItemClickL { _, _, position, _ ->
//        onClickItem(position, list[position])
//        sheetDialog.dismiss()
//    }
//    sheetDialog.setOnCancelListener {
//        onCancel()
//    }
//    sheetDialog.show()
}

fun Activity.showSnack(word: String) {
    Snackbar.make(window.decorView.findViewById(android.R.id.content), word, Snackbar.LENGTH_LONG)
        .show()
    Log.i(KLog.getTag(), word)
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
    } else {
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

fun Activity.newDialog(view: View): Dialog {
    return object : Dialog(this, R.style.Dialog) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val root = LinearLayout(context).apply {
                gravity = Gravity.CENTER
                addView(view)
            }
            setContentView(root)
        }
    }
}

fun Activity.newDialog(@LayoutRes viewId: Int): Dialog {
    return object : Dialog(this, R.style.Dialog) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(viewId)
        }
    }
}

private var preTime = 0L

/**
 * toast提示 仅用于无障碍模块
 */
fun showToast(word: String = "还没做") {
    // Toast.LENGTH_SHORT 4s
    // Toast.LENGTH_LONG 7s
    val curTime = System.currentTimeMillis()
    if (curTime - preTime > 4000L) {
        Toast.makeText(KUtil.getApp(), word, Toast.LENGTH_SHORT).show()
        preTime = curTime
    }
}

fun Activity.pickPhoneImage(onErrorOrCancel: (String) -> Unit = {}, call: (Uri) -> Unit) {
    phoneImages(onErrorOrCancel) { images ->
        if (images.isEmpty()) {
            onErrorOrCancel("相册空空如也")
            return@phoneImages
        }
        val dialog = BottomSheetDialog(this)

        val recyclerView = RecyclerView(this)
        recyclerView.layoutManager = GridLayoutManager(this, KUtil.getScreenWith()/KUtil.dip2px(80f))
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder = object : RecyclerView.ViewHolder(
                LayoutInflater.from(this@pickPhoneImage).inflate(R.layout.item_image, parent, false)
            ) {}

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder.itemView.apply {
                    val item = images[position]
                    val id = item[MediaStore.Images.Media._ID]?.toLong() ?: 0L
                    val pathUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    Glide.with(context)
                        .load(pathUri)
                        .thumbnail(0.2f)
                        .into(findViewById(R.id.iv_test))
                    findViewById<TextView>(R.id.tv_position).text = position.toString()


                    setOnClickListener {
                        dialog.dismiss()
                        call(pathUri)
                    }
                }
            }

            override fun getItemCount(): Int = images.size
        }


        dialog.setContentView(recyclerView)
        dialog.setOnCancelListener {
            onErrorOrCancel("取消选择")
        }
        dialog.show()
    }
}

/**
 * 随机获取相册图片
 */
fun Activity.randomPhoneImage(onError: (String) -> Unit = {}, call: (Uri) -> Unit) {
    phoneImages(onError) {
        if (it.isEmpty()) {
            onError("相册空空如也")
            return@phoneImages
        }

        val id = it.random()[MediaStore.Images.Media._ID]?.toLong() ?: 0L
//        val id = it[0][MediaStore.Images.Media._ID]?.toLong() ?: 0L
        val pathUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )
        call.invoke(pathUri)
    }
}

/**
 * 获取相册图片
 */
fun Activity.phoneImages(
    onError: (String) -> Unit = {},
    call: (List<Map<String, String>>) -> Unit
) {
    KGranting.requestAlbumPermissions(this, 887) {
        if (it) {
            Thread {
                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " desc"
                )?.use { cusor ->
                    val iDatas = mutableListOf<TreeMap<String, String>>()

                    if (cusor.count > 0) {
                        KLog.i("查到图片${cusor.count}张")
                        val names = cusor.columnNames
                        val indexs = names.map { cusor.getColumnIndex(it) }

                        cusor.moveToFirst()

                        do {
                            val map = TreeMap<String, String>()

                            for (i in names.indices) {
//                            map[names[i]] = cusor.getString(indexs[i]) ?: ""
                                map[names[i]] = when (cusor.getType(indexs[i])) {
                                    Cursor.FIELD_TYPE_FLOAT -> cusor.getFloatOrNull(indexs[i])
                                        ?.toString() ?: ""
                                    Cursor.FIELD_TYPE_INTEGER -> cusor.getIntOrNull(indexs[i])
                                        ?.toString() ?: ""
                                    Cursor.FIELD_TYPE_STRING -> cusor.getStringOrNull(indexs[i]) ?: ""
                                    Cursor.FIELD_TYPE_BLOB -> {
                                        ""
                                    }
                                    else -> ""
                                }
                            }
                            iDatas.add(map)
                        } while (cusor.moveToNext())
                    }

                    cusor.close()

                    runOnUiThread {
                        call.invoke(iDatas)
                    }
                }
            }.start()
        } else onError("没有权限")
    }
}