package com.kiven.sample

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.fragment_apple.*

/**
 * Created by kiven on 2017/3/28.
 */

class FragmentApple : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val xmlResourceParser = resources.getLayout(R.layout.fragment_apple)
        return inflater.inflate(xmlResourceParser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sample_text.setOnClickListener {
            val alpha = ObjectAnimator.ofFloat(sample_text, "alpha", 0f, 1f)
            val rgb = ObjectAnimator.ofInt(sample_text, "backgroundColor", Color.RED, Color.GRAY, Color.TRANSPARENT).apply { setEvaluator(ArgbEvaluator()) }
            val tran = ObjectAnimator.ofFloat(sample_text, "y", sample_text.y, 0f, sample_text.y)

            val animation = AnimatorSet()
            animation.apply {
                duration = 2000
                interpolator = DecelerateInterpolator()
                play(alpha).with(rgb).with(tran)
            }.start()
        }

        sample_text2.setOnClickListener { view ->
            // 创建动画
            val anim = ValueAnimator.ofObject(TypeEvaluator<Point> { fraction, startValue, endValue ->
                val startPoint = startValue as Point
                val endPoint = endValue as Point
                val x = startPoint.x + fraction * (endPoint.x - startPoint.x)
                val y = startPoint.y + fraction * (endPoint.y - startPoint.y)
                Point(x.toInt(), y.toInt())
            }, Point(0, 0), Point(400, 510))
            // 设置其他属性
            with(anim) {
                addUpdateListener {
                    // view的位置设定
                    val point = it.animatedValue as Point
                    view.x = point.x.toFloat()
                    view.y = point.y.toFloat()
                }
                duration = 1500
            }
            // 开始动画
            anim.start()
        }
    }
}
