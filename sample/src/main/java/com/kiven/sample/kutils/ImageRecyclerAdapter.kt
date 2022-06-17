package com.kiven.sample.kutils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiven.kutils.tools.KAppHelper
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.util.showImageDialog
import java.io.File

class ImageRecyclerAdapter(val context: Context, private val itemWidth:Float = 100f, private val itemHeight:Float = 100f) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data = mutableListOf<Any>()

    /**
     * 支持 Uri,String,File,Bitmap 等
     */
    fun changeData(d: List<Any>) {
        data.clear()
        data.addAll(d)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(ImageView(context)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as ImageView).apply {
            val item  = data[position]

            Glide.with(context!!).let {
                when(item) {
                    is Uri -> it.load(item)
                    is String -> it.load(item)
                    is File -> it.load(item)
                    is Bitmap -> it.load(item)
                    else -> it.load(item.toString())
                }
            }.centerCrop()
                .error(android.R.drawable.ic_menu_report_image)
                .override(KUtil.dip2px(itemWidth), KUtil.dip2px(itemHeight))
                .into(this as ImageView)

            setOnClickListener {
                KAppHelper.getInstance().topActivity?.apply {

                    when(item) {
                        is Uri -> showImageDialog(item)
                        is String -> showImageDialog(item)
                        is File -> showImageDialog(item.path)
                        is Bitmap -> showImageDialog(item)
                        else -> showImageDialog(item.toString())
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size
}