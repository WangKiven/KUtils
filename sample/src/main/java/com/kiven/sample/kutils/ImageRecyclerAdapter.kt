package com.kiven.sample.kutils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAppHelper
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import com.kiven.sample.util.showImageDialog
import com.kiven.sample.util.showToast
import java.io.File

class ImageRecyclerAdapter(
    val context: Context,
    private val itemWidth: Float = 100f,
    private val itemHeight: Float = 100f
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        // TODO ImageView 在 ConstraintLayout中固定宽高比为 1：1，在嵌套的RecyclerView和NestScrollView里面才能正常显示
        //  否则，概率出现部分RecyclerView的内容显示不完整
        //  高度不固定的情况，目前没找到解决方法
//        return object : RecyclerView.ViewHolder(ImageView(context)) {}
        return object : RecyclerView.ViewHolder(ConstraintLayout(context).apply {
            val imageView = ImageView(context)
            val params =
                ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0).apply {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    dimensionRatio = "1:1"
                }

            addView(imageView, 0, params)
        }) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val root = holder.itemView as ConstraintLayout
        (root[0] as ImageView).apply {
            adjustViewBounds = true

            val item = data[position]

            Glide.with(context!!).let {
                when (item) {
                    is Uri -> it.load(item)
                    is String -> it.load(item)
                    is File -> it.load(item)
                    is Bitmap -> it.load(item)
                    else -> it.load(item.toString())
                }
            }.centerCrop()
                .error(android.R.drawable.ic_menu_report_image)
                .override(KUtil.dip2px(itemWidth), KUtil.dip2px(itemHeight))
                .into(this)

            setOnClickListener {
                try {
                    KAppHelper.getInstance().topActivity?.apply {
                        when (item) {
                            is Uri -> showImageDialog(item)
                            is String -> showImageDialog(item)
                            is File -> showImageDialog(item.path)
                            is Bitmap -> showImageDialog(item)
                            else -> showImageDialog(item.toString())
                        }
                    }
                } catch (t: Throwable) {
                    KLog.e(t)
                    showToast("图片不存在")
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size
}