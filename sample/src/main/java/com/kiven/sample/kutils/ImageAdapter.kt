package com.kiven.sample.kutils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kiven.kutils.tools.KAppHelper
import com.kiven.kutils.tools.KUtil
import com.kiven.kutils.widget.UIGridView
import com.kiven.sample.util.showImageDialog
import java.io.File

class ImageAdapter(private val itemWidth:Float = 100f, private val itemHeight:Float = 100f) : UIGridView.Adapter() {
    private val data = mutableListOf<Any>()

    /**
     * 支持 Uri,String,File,Bitmap 等
     */
    fun changeData(d: List<Any>) {
        data.clear()
        data.addAll(d)
        notifyDataSetChanged()
    }
    override fun getGridViewItemCount() = data.size
    override fun getItemView(context: Context?, itemView: View?, parentView: ViewGroup?, position: Int): View {
        return (itemView ?: ImageView(context)).also { iv ->

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
                .into(iv as ImageView)

            iv.setOnClickListener {
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
}