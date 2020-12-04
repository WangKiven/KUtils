package com.kiven.sample.libs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.util.Const
import com.kiven.sample.util.newDialog
import com.kiven.sample.util.showImageDialog
import com.kiven.sample.xutils.db.AHDbDemo
import com.kiven.sample.xutils.net.AHNetDemo
import kotlinx.android.synthetic.main.activity_lauch.*
import org.jetbrains.anko.dip
import org.xutils.image.ImageOptions
import org.xutils.x

/**
 * Created by wangk on 2020/12/4.
 */
class AHXUtilLib : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        addBtn("Net FrameWork") { AHNetDemo().startActivity(mActivity) }
        addBtn("数据库") { AHDbDemo().startActivity(mActivity) }
        addBtn("图片") {
            val iv = ImageView(activity).apply {
                layoutParams = ViewGroup.LayoutParams(activity.dip(50), activity.dip(50))

                var count = 0
                val showNext = fun() {
                    val urls = Const.IMAGES.subList(0, 2) + "/storage/emulated/0/DCIM/Camera/1557910396757.jpg"
                    val options = ImageOptions.Builder()
                            .setCircular(true)
                            .setAutoRotate(true).setFadeIn(true).build()
                    org.xutils.x.image().bind(this@apply, urls[count%urls.size], options)

                    count++
                }

                showNext()
                setOnClickListener { showNext() }
            }


            activity.newDialog(iv).show()
        }
    }
}