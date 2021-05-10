package com.kiven.sample.systemdata

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.database.getBlobOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KString
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import com.kiven.sample.util.*
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

/**
 * Created by oukobayashi on 2019-11-12.
 */
class AHSystemImage : KActivityHelper() {
    private val adapter = MyAdapter()
    private val datas = mutableListOf<TreeMap<String, String>>()

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val recyclerView = RecyclerView(mActivity).apply {
            val dpScreenWith = KUtil.getScreenWith() / KUtil.getScreenDensity()
            layoutManager = GridLayoutManager(mActivity, max((dpScreenWith / 80).toInt(), 1))
            adapter = this@AHSystemImage.adapter
        }
        setContentView(recyclerView)

        KGranting.requestPermissions(mActivity, 345, Manifest.permission.WRITE_EXTERNAL_STORAGE, "内存") {
            if (it) loadData()
        }
    }

    private fun loadData() {
        GlobalScope.launch {
            val iDatas = mutableListOf<TreeMap<String, String>>()

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
                                val map = TreeMap<String, String>()
//                                val sb = StringBuilder()

                                for (i in names.indices) {
                                    map[names[i]] = when(cusor.getType(indexs[i])) {
                                        Cursor.FIELD_TYPE_FLOAT -> cusor.getFloatOrNull(indexs[i])?.toString() ?: "空"
                                        Cursor.FIELD_TYPE_INTEGER -> cusor.getIntOrNull(indexs[i])?.toString() ?: "空"
                                        Cursor.FIELD_TYPE_STRING -> cusor.getStringOrNull(indexs[i]) ?: "空"
                                        Cursor.FIELD_TYPE_BLOB -> {
                                            "数据类型是BLOB, 长度是${cusor.getBlobOrNull(indexs[i])?.size}"
                                        }
                                        else -> "无数据类型"
                                    }
//                                    map[names[i]] = cusor.getString(indexs[i]) ?: ""
//                                    sb.append(names[i]).append(" = ").append(getData(indexs[i], types[i])).append(", ")

                                }
//                                    sb.append(MediaStore.Images.Media.DISPLAY_NAME).append(" = ").append(cusor.getString(cusor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)))
                                iDatas.add(map)
//                                showTip(sb.toString())
                            } while (cusor.moveToNext())

                            cusor.close()
                        }
            }

            withContext(Dispatchers.Main) {
                datas.clear()
                datas.addAll(iDatas)
                adapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private inner class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.apply {
                val item = datas[position]
//                val path = item[MediaStore.Images.Media.DATA]
                val id = item[MediaStore.Images.Media._ID]?.toLong() ?: 0L
                val pathUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                )

                Glide.with(context)
                        .load(pathUri)
                        .into(iv_test)
                tv_position.text = position.toString()

                setOnClickListener {
                    mActivity.showBottomSheetDialog(arrayOf("查看大图", "显示详细", "修改拍摄时间", "修改添加时间", "修改图片修改时间")) { index, _ ->
                        when (index) {
                            0 -> {
                                mActivity.showImageDialog(pathUri)
                            }
                            1 -> {
                                val list = item.toList()
                                        .sortedWith(kotlin.Comparator { o1, o2 ->
                                            val b1 = o1.second.isBlank()
                                            val b2 = o2.second.isBlank()

                                            if (b1 == b2) {
                                                return@Comparator o1.first.compareTo(o2.first)
                                            } else {
                                                return@Comparator if (b1) 1 else -1
                                            }
                                        })
                                mActivity.showListDialog(
                                        list.map {
                                            "${it.first}: ${it.second}"
                                        }, false)
                                { _, ss ->
                                    mActivity.showDialog(ss)
                                }
                            }
                            2 -> {
                                val key = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                    MediaStore.Images.ImageColumns.DATE_TAKEN
                                else
                                    "datetaken"
                                val pp = item[key] ?: ""

                                mActivity.getInput(key, format.format(Date(pp.toLongOrNull() ?: 0))){
                                    try {
                                        val date = format.parse(it.toString())
                                        date?.apply {
                                            update(id.toString(), key, time.toString())
                                        }
                                    } catch (e: Exception) {
                                        KLog.e(e)
                                        mActivity.showSnack("时间解析错误")
                                    }
                                }
                            }
                            3 -> {
                                val key = MediaStore.Images.ImageColumns.DATE_ADDED
                                val pp = item[key] ?: ""

                                xiugaiDate(id.toString(), key, pp)
                            }
                            4 -> {
                                val key = MediaStore.Images.ImageColumns.DATE_MODIFIED
                                val pp = item[key] ?: ""

                                xiugaiDate(id.toString(), key, pp)
                            }
                            /*5 -> {
                                mActivity.contentResolver.delete()
                            }*/
                        }
                    }
                }
            }
        }

        private fun xiugaiDate(id:String, key:String, value:String) {
            mActivity.getInput(key, format.format(Date((value.toLongOrNull() ?: 0) * 1000L))){
                try {
                    val date = format.parse(it.toString())
                    date?.apply {
                        update(id, key, (time/1000).toString())
                    }
                } catch (e: Exception) {
                    KLog.e(e)
                    mActivity.showSnack("时间解析错误")
                }
            }
        }

        private fun update(id:String, fieldName:String, value:String) {
            val cv = ContentValues()
            cv.put(fieldName, value)
            mActivity.contentResolver.update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv,
                    MediaStore.Images.Media._ID + "=?", arrayOf(id))

            loadData()
        }

        override fun getItemCount(): Int = datas.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                object : RecyclerView.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_image, parent, false)) {}
    }
}