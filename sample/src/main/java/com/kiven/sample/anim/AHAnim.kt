package com.kiven.sample.anim

import android.os.Bundle
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R

class AHAnim : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_anim)
        val flexboxLayout = findViewById<FlexboxLayout>(R.id.flex)
        val iv_1 = findViewById<ImageView>(R.id.iv_1)
        val iv_2 = findViewById<ImageView>(R.id.iv_2)

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }

        // TODO: 2018/3/30 ----------------------------------------------------------
        addTitle("Physics-based motion")

        // https://developer.android.google.cn/guide/topics/graphics/spring-animation.html
        addView("spring(弹簧) animation", View.OnClickListener {
            // dp -> px
            val ps = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, resources.displayMetrics)

            val anim1 = SpringAnimation(iv_1, SpringAnimation.TRANSLATION_Y)
                    .setSpring(SpringForce(100f)
                            .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)//设置弹性阻尼
                            .setStiffness(SpringForce.STIFFNESS_VERY_LOW))//弹性的生硬度
                    .setStartVelocity(ps)//起始速度，像素/秒。
            anim1.setStartValue(0f)

            val anim2 = SpringAnimation(iv_2, SpringAnimation.TRANSLATION_Y, 0f).setStartValue(0f)

            anim1.addUpdateListener { _, value, _ -> anim2.animateToFinalPosition(value) }

            SpringAnimation(iv_2, SpringAnimation.TRANSLATION_X, -100f).setStartValue(0f).start()
            anim2.start()
            anim1.start()
        })
    }
}