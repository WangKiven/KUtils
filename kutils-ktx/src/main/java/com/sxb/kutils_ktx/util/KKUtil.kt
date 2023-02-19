package com.sxb.kutils_ktx.util

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.kiven.kutils.callBack.Consumer
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KAppTool
import kotlinx.coroutines.*

/**
 * 根据 Parcel 生成 Parcelable 对象的实例。由于 Kotlin 的问题，导致 @Parcelize注解的Parcelable子类 不能直接调用 CREATOR ，只能通过反射来了。
 */
fun <T : Parcelable> createFromParcel(t: Class<T>, parcel: Parcel): T {
    val field = t.getField("CREATOR")
    val creator = field.get(t) as Parcelable.Creator<T>
    return creator.createFromParcel(parcel)
}

/**
 * GlobalScope主线程调用
 */
fun GlobalScope.main(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch(Dispatchers.Main, block = block)

/**
 * 调用系统下载功能下载apk并安装
 * http://events.jianshu.io/p/ecb21640287b
 * https://blog.csdn.net/zly19931101/article/details/89676772
 */
fun Activity.downloadApk(url: String, call: Consumer<Float>) {
    val uri = Uri.parse(url)
    val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        dm.remove()
    val request = DownloadManager.Request(uri)
    val downloadId = dm.enqueue(request)

    Thread {
        while (true) {
            Thread.sleep(5000)
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = dm.query(query) ?: break
            if (cursor.moveToNext()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {

                    val f = dm.getUriForDownloadedFile(downloadId)
                    runOnUiThread {
                        KAppTool.installApk(this, f)
                    }

                    break
                }

                if (status == DownloadManager.STATUS_FAILED) {
                    KAlertDialogHelper.Show1BDialog(this, "下载失败")
                    break
                }
            }
        }
    }.start()
}