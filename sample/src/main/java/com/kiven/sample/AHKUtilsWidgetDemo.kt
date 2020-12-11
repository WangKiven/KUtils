package com.kiven.sample

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.widget.UIGridView
import com.kiven.sample.util.showSnack
import kotlinx.android.synthetic.main.ah_kutils_widget_demo.*
import org.jetbrains.anko.dip

/**
 * Created by wangk on 2020/12/6.
 */
class AHKUtilsWidgetDemo : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_kutils_widget_demo)
        activity.apply {
            rulingSeekbar.apply {
                setScale(5, 120)
                progress = 30;
                addNode(10, 0, true);
                addNode(30, 2, true);
                addNode(57, 1, true);
                addNode(85, 1, false);
                addNode(110, 2, true);
            }

            gridView.setAdapter(object : UIGridView.Adapter() {
                init {
                    setChildMargin(dip(5))
                }

                override fun getGridViewItemCount(): Int {
                    return 13
                }

                override fun getItemView(context: Context?, itemView: View?, parentView: ViewGroup?, position: Int): View {

                    return (itemView ?: ImageView(context)).also {
                        val iv = it as ImageView
                        iv.setImageResource(R.mipmap.emoji_0x1f343)
                    }
                }
            })
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        mActivity.showSnack("你点击了KNormalItemView")
    }
}