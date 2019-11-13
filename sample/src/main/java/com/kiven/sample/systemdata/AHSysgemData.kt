package com.kiven.sample.systemdata

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.util.showTip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * Created by oukobayashi on 2019-11-12.
 * 参考demo：https://gitee.com/WangKiven/storage-samples/tree/master/MediaStore
 * 参考文档：https://www.jianshu.com/p/498c9d06c193
 */
class AHSysgemData:KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

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
        // TODO: 2019-11-12 ----------------------------------------------------------
        val txtTag = addTitle("")
        addView("查询相册", View.OnClickListener {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val resolver = mActivity.contentResolver
                    /*resolver.openFileDescriptor(Uri.parse(imags[0]), "rwt")?.use {

                    }*/
                    val projection = arrayOf(
                            MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.SIZE,
                            MediaStore.Images.Media.DATA
                    )

                    showTip(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
                    /*resolver.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection,
                            MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                            arrayOf("image/jpeg", "image/png"),
                            MediaStore.Images.Media.DATE_MODIFIED + " desc"
                    )*/
                    resolver.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            null,
                            null,
                            null,
                            MediaStore.Images.Media.DATE_MODIFIED + " desc"
                    )
                            ?.use { cusor ->
                                val names = cusor.columnNames
                                val indexs = names.map { cusor.getColumnIndex(it) }

                                val types = indexs.map {
                                    try {
                                        cusor.getType(it)
                                    } catch (e: Throwable) {
                                        Cursor.FIELD_TYPE_NULL
                                    }
                                }

                                val getData = fun(index: Int, type: Int): Any? {
                                    /*return when (type) {
                                        Cursor.FIELD_TYPE_STRING -> cusor.getString(index)
                                        Cursor.FIELD_TYPE_INTEGER -> cusor.getInt(index)
                                        Cursor.FIELD_TYPE_FLOAT -> cusor.getFloat(index)
                                        else -> null
                                    }*/
                                    return cusor.getString(index)
                                }

                                cusor.moveToFirst()
                                do {
                                    val sb = StringBuilder()
                                    for (i in names.indices) {
                                        sb.append(names[i]).append(" = ").append(getData(indexs[i], types[i])).append(", ")
                                    }
//                                    sb.append(MediaStore.Images.Media.DISPLAY_NAME).append(" = ").append(cusor.getString(cusor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)))
                                    showTip(sb.toString())
                                } while (cusor.moveToNext())

                                cusor.close()
                            }
                }
            }

        })
    }
}