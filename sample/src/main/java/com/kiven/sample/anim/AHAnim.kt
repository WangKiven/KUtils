package com.kiven.sample.anim

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.animation.FlingAnimation
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

        val addView = fun(text: String, click: View.OnClickListener?): Button {
            val btn = Button(activity)
            btn.text = text
            btn.text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
            return btn
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

        addView("Fling(滑动) Animation", View.OnClickListener {
            val fling = FlingAnimation(iv_1, FlingAnimation.TRANSLATION_Y)
            fling.setStartVelocity(300f)// 初始速度
                    .setMinValue(0f)// translationY 最小值
                    .setMaxValue(500f)// translationY 最大值
                    .setFriction(0.2f)// 摩擦系数
                    .setMinimumVisibleChange(FlingAnimation.MIN_VISIBLE_CHANGE_SCALE)// 设置最小可见变化, 越小检索效果越明显
                    .start()
        })

        // TODO: 2018/3/31 ----------------------------------------------------------

        addTitle("Animate Drawable Graphics")
        // 文档：https://developer.android.google.cn/guide/topics/graphics/drawable-animation.html

        addView("贞动画", null).isEnabled = false// animation-list
        addView("vectorAnim_move", View.OnClickListener {
            (iv_1.drawable as AnimatedVectorDrawable).start()
        })
        addView("vectorAnim_draw_path", View.OnClickListener {
            iv_2.setImageResource(R.drawable.anim_draw_path)
            (iv_2.drawable as AnimatedVectorDrawable).start()
        })
        addView("vectorAnim_path_to_path", View.OnClickListener {
            iv_2.setImageResource(R.drawable.anim_path_to_path)
            (iv_2.drawable as AnimatedVectorDrawable).start()
        })
        addView("vectorAnim_selector", object : View.OnClickListener {
            var i = 0
            override fun onClick(v: View) {
                when (i % 3) {
                /*0-> iv_2.setImageResource(R.drawable.anim_selector)
                1 -> iv_2.setImageState(intArrayOf(android.R.attr.state_checked), true)
                2 -> iv_2.setImageState(intArrayOf(), true)*/
                    0 -> iv_2.setImageResource(R.drawable.anim_selector)
                    1 -> iv_2.isEnabled = false
                    2 -> iv_2.isEnabled = true
                }
                i++
            }
        })
        // TODO: 2018/4/17 ----------------------------------------------------------
        addTitle("Auto Animate Layout Updates, 默认添加删除view动画, xml添加：android:animateLayoutChanges=\"true\"")

        addView("to do", object :View.OnClickListener {
            var btn:Button? = null
            override fun onClick(v: View?) {
                if (btn == null) {
                    btn = addView("new button", null)
                } else {
                    flexboxLayout.removeView(btn)
                    btn = null
                }
            }

        })

        addView("change text", object :View.OnClickListener {
            var i = 0
            override fun onClick(v: View?) {
                (v as Button).text = "change text $i"
                i++
            }

        })
    }
}