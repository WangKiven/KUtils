package com.kiven.sample.kutils

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kiven.kutils.tools.KAppHelper
import com.kiven.kutils.tools.KUtil
import com.kiven.kutils.widget.UIGridView
import com.kiven.sample.util.showImageDialog

class ImageAdapter(private val itemWidth:Float = 100f, private val itemHeight:Float = 100f) : UIGridView.Adapter() {
    private val data = mutableListOf<Uri>()
    fun changeData(d: List<Uri>) {
        data.clear()
        data.addAll(d)
        notifyDataSetChanged()
    }
    override fun getGridViewItemCount() = data.size
    override fun getItemView(context: Context?, itemView: View?, parentView: ViewGroup?, position: Int): View {
        return (itemView ?: ImageView(context)).also {
            val iv = it as ImageView

            Glide.with(context!!)
                .load(data[position])
                .centerCrop()
                .error(android.R.drawable.ic_menu_report_image)
                .override(KUtil.dip2px(itemWidth), KUtil.dip2px(itemHeight))
                .into(iv)

            iv.setOnClickListener {
                KAppHelper.getInstance().topActivity?.showImageDialog(data[position])
            }
        }
    }
}