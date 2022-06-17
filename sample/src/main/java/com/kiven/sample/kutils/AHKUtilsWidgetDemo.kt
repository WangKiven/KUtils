package com.kiven.sample.kutils

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KUtil
import com.kiven.kutils.widget.UIGridView
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.R
import com.kiven.sample.databinding.AhKutilsWidgetDemoBinding
import com.kiven.sample.util.showSnack

/**
 * Created by wangk on 2020/12/6.
 */
class AHKUtilsWidgetDemo : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        activity.apply {
            val binding = AhKutilsWidgetDemoBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.rulingSeekbar.apply {
                setScale(5, 120)
                progress = 30;
                addNode(10, 0, true)
                addNode(30, 2, true)
                addNode(57, 1, true)
                addNode(85, 1, false)
                addNode(110, 2, true)
            }

            binding.gridView.setAdapter(object : UIGridView.Adapter() {
                init {
                    setChildMargin(KUtil.dip2px(5f))
                }

                override fun getGridViewItemCount(): Int {
                    return 13
                }

                override fun getItemView(context: Context?, itemView: View?, parentView: ViewGroup?, position: Int): View {

                    return (itemView ?: ImageView(context)).also {
                        val iv = it as ImageView
                        iv.setImageResource(R.mipmap.ic_launcher_u)
                    }
                }
            })
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when(view?.id) {
            R.id.item1 -> {
                AHGvInRecyclerView().startActivity(mActivity)
            }
            R.id.item2 -> {
                AHInRecyclerView().startActivity(mActivity)
            }
            R.id.item3 -> {
                AHRecyclerInScrollView().startActivity(mActivity)
            }
        }
        mActivity.showSnack("你点击了KNormalItemView")
    }
}