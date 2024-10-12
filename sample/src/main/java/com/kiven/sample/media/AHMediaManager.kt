package com.kiven.sample.media

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KOpenSetting
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.util.pickPhoneImage
import com.kiven.sample.util.showListDialog
import com.kiven.sample.util.showToast
import java.io.OutputStream

class AHMediaManager: BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        var tUri: Uri? = null
        val launcher1 = activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK && tUri != null) {
                val bitmap = Bitmap.createBitmap(flexBoxLayout.width, flexBoxLayout.height, Bitmap.Config.RGB_565)
                val canvas = Canvas(bitmap)
                flexBoxLayout.draw(canvas)

                val imageOut: OutputStream = activity.contentResolver.openOutputStream(tUri!!)!!
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, imageOut)
                } catch (e: Throwable) {
                    KLog.e(e)
                } finally {
                    imageOut.close()
                }
            }
        }


        // todo https://developer.android.google.cn/training/data-storage/shared/media?hl=zh-cn
        //  权限处理：https://developer.android.google.cn/training/data-storage/shared/media?hl=zh-cn#management-permission

        fun openMediaSetting() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val i = Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA)
                i.setData(Uri.fromParts("package", activity.getPackageName(), null))
                activity.startActivity(i)
            } else {
                KOpenSetting.openAppSetting(activity)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity.showListDialog(arrayOf("显示系统弹窗(默认)", "不显示系统弹窗")){i, s ->
                if (i == 1) {
                    KGranting.requestPermissions(activity,
                        arrayOf(Manifest.permission.MANAGE_MEDIA, Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE), arrayOf("媒体文件管理", "媒体文件定位", "内存")
                    ) {
                        if (it) {
                            showToast("完成授权")
                        } else {
                            showToast("未完成授权")
                        }
                    }
                }
            }
            /*if (!KGranting.checkPermission(activity, Manifest.permission.MANAGE_MEDIA)) {
                KAlertDialogHelper.Show2BDialog(activity, "没有媒体文件管理权限，是否授权？无权限时，操作文件时会有系统弹窗询问是否允许。") {
                    openMediaSetting()
                }
            }*/
        }

        addTitle("媒体文件管理")

        addBtn("修改权限") {
            openMediaSetting()
        }

        addBtn("createWriteRequest() 修改文件") {


            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "${System.currentTimeMillis()}.png")
            values.put(MediaStore.Images.Media.DESCRIPTION, "测试图片")
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")


            try {
                tUri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val editPendingIntent = MediaStore.createWriteRequest(activity.contentResolver, listOf(tUri))
//                    activity.startIntentSenderForResult(editPendingIntent.intentSender, EDIT_REQUEST_CODE, null, 0, 0, 0)
                    launcher1.launch(IntentSenderRequest.Builder(editPendingIntent.intentSender).build())
                } else {
                    val imageOut: OutputStream = activity.contentResolver.openOutputStream(tUri!!)!!
                    try {
                        val bitmap = Bitmap.createBitmap(flexBoxLayout.width, flexBoxLayout.height, Bitmap.Config.RGB_565)
                        val canvas = Canvas(bitmap)
                        flexBoxLayout.draw(canvas)

                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, imageOut)
                    } catch (e: Throwable) {
                        KLog.e(e)
                    } finally {
                        imageOut.close()
                    }
                }
            } catch (e: Throwable) {
                KLog.e(e)
            }
        }

        addBtn("createTrashRequest() 将文件移入和移出回收站") {
            tUri = null

            activity.showListDialog(arrayOf("移入回收站", "移除回收站")){i, s ->
                when(i) {
                    0 -> {
                        activity.pickPhoneImage {
                            val pi = MediaStore.createTrashRequest(
                                activity.contentResolver,
                                listOf(it),
                                true
                            )
                            launcher1.launch(IntentSenderRequest.Builder(pi.intentSender).build())
                        }
                    }
                    1 -> {}
                }
            }

        }

        // todo 红米k40游戏增强版测试，还是把图片放到回收站了
        addBtn("createDeleteRequest() 删除文件") {
            tUri = null

            activity.pickPhoneImage {
//                        activity.showImage(it)
                val pi = MediaStore.createDeleteRequest(
                    activity.contentResolver,
                    listOf(it)
                )
                launcher1.launch(IntentSenderRequest.Builder(pi.intentSender).build())
            }
        }

    }
}