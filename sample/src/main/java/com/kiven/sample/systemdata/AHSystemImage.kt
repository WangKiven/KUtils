package com.kiven.sample.systemdata

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import com.kiven.sample.util.showTip
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max

/**
 * Created by oukobayashi on 2019-11-12.
 */
class AHSystemImage : KActivityDebugHelper() {
    private val adapter = MyAdapter()
    private val datas = mutableListOf<TreeMap<String, String>>()

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val recyclerView = RecyclerView(mActivity).apply {
            val dpScreenWith = KUtil.getScreenWith(mActivity) / KUtil.getScreenDensity(mActivity)
            layoutManager = GridLayoutManager(mActivity, max((dpScreenWith / 100).toInt(), 1))
            adapter = this@AHSystemImage.adapter
        }
        setContentView(recyclerView)


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
                                    map[names[i]] = cusor.getString(indexs[i]) ?: ""
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

    private inner class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.apply {
                Glide.with(context)
                        .load(datas[position][MediaStore.Images.Media.DATA])
                        .into(iv_test)
            }
        }

        override fun getItemCount(): Int = datas.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                object : RecyclerView.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_image, parent, false)) {}
    }
}