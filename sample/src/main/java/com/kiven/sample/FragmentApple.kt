package com.kiven.sample

import android.animation.*
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import com.kiven.kutils.tools.RequestPermissionFragment
import com.kiven.sample.databinding.FragmentAppleBinding
import com.kiven.sample.util.showDialog

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
        val binding = FragmentAppleBinding.bind(view)

        binding.sampleText.setOnClickListener {
            val alpha = ObjectAnimator.ofFloat(binding.sampleText, "alpha", 0f, 1f)
            val rgb = ObjectAnimator.ofInt(binding.sampleText, "backgroundColor", Color.RED, Color.GRAY, Color.TRANSPARENT).apply { setEvaluator(ArgbEvaluator()) }
            val tran = ObjectAnimator.ofFloat(binding.sampleText, "y", binding.sampleText.y, 0f, binding.sampleText.y)

            val animation = AnimatorSet()
            animation.apply {
                duration = 2000
                interpolator = DecelerateInterpolator()
                play(alpha).with(rgb).with(tran)
            }.start()
        }

        binding.sampleText2.setOnClickListener { view ->
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

        binding.btnTest.setOnClickListener {
            RequestPermissionFragment.requestPermissions(childFragmentManager, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                activity?.showDialog("获取存储权限情况：$it")
            }
            /*KGranting.requestPermissions(activity!!, 8899, android.Manifest.permission.READ_EXTERNAL_STORAGE, "存储") {
                activity?.showDialog("获取存储权限情况：$it")
            }*/
        }
    }
}
