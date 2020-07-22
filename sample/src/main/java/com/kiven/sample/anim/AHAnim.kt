package com.kiven.sample.anim

import android.animation.*
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.material.snackbar.Snackbar
import androidx.transition.*
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
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

        val addView = fun(text: String, click: OnClickListener?): Button {
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
        addView("spring(弹簧) animation", OnClickListener {
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

        addView("Fling(滑动) Animation", OnClickListener {
            val fling = FlingAnimation(iv_1, FlingAnimation.TRANSLATION_Y)
            fling.setStartVelocity(300f)// 初始速度
                    .setMinValue(0f)// translationY 最小值
                    .setMaxValue(250f)// translationY 最大值
                    .setFriction(0.2f)// 摩擦系数
                    .setMinimumVisibleChange(FlingAnimation.MIN_VISIBLE_CHANGE_SCALE)// 设置最小可见变化, 越小检索效果越明显
                    .start()
        })

        // TODO: 2018/3/31 ----------------------------------------------------------

        addTitle("Animate Drawable Graphics")
        // 文档：https://developer.android.google.cn/guide/topics/graphics/drawable-animation.html

        addView("贞动画", null).isEnabled = false// animation-list

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addView("vectorAnim_move", OnClickListener {
                (iv_1.drawable as AnimatedVectorDrawable).start()
            })
            addView("vectorAnim_draw_path", OnClickListener {
                iv_2.setImageResource(R.drawable.anim_draw_path)
                (iv_2.drawable as AnimatedVectorDrawable).start()
            })
            addView("vectorAnim_path_to_path", OnClickListener {
                iv_2.setImageResource(R.drawable.anim_path_to_path)
                (iv_2.drawable as AnimatedVectorDrawable).start()
            })
        }
        addView("vectorAnim_selector", object : OnClickListener {
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

        addView("to do", object : OnClickListener {
            var btn: Button? = null
            override fun onClick(v: View?) {
                if (btn == null) {
                    btn = addView("new button", null)
                } else {
                    flexboxLayout.removeView(btn)
                    btn = null
                }
            }
        })

        addView("change text", object : OnClickListener {
            var i = 0
            override fun onClick(v: View?) {
                (v as Button).text = "change text $i"
                i++
            }

        })

        // TODO: 2018/4/26 ----------------------------------------------------------
        addTitle("属性动画（ValueAnimator，ObjectAnimator，AnimatorSet）")
        // doc 简单理解: https://blog.csdn.net/harvic880925/article/details/50525521
        // doc ObjectAnimator使用: https://blog.csdn.net/feiduclear_up/article/details/45915377
        addView("ValueAnimator.ofInt", OnClickListener {
            val animator = ValueAnimator.ofInt(0, 100)
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                iv_2.layout(value, value, value + 200, value + 200)
            }
            animator.start()
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addView("ValueAnimator.ofArgb", OnClickListener { cv ->
                val animator = ValueAnimator.ofArgb(Color.YELLOW, Color.parseColor("#789876"), Color.DKGRAY)
                animator.duration = 2500
                animator.addUpdateListener {
                    val value = it.animatedValue as Int
                    (cv as Button).setTextColor(value)
                }
                animator.start()
            })
        }
        addView("ArgbEvaluator", OnClickListener { cv ->
            val animator = ValueAnimator.ofInt(Color.parseColor("#ffff00ff"),
                    Color.parseColor("#78ab34"), Color.parseColor("#980d34"))
            animator.duration = 2500
            animator.setEvaluator(ArgbEvaluator())// 没有这行，会出现闪动效果
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                (cv as Button).setTextColor(value)
            }
            animator.start()
        })
        addView("ValueAnimator.ofObject", OnClickListener { cv ->
            class RunObj(val a: Int, val b: Int)
            class RunType : TypeEvaluator<RunObj> {
                override fun evaluate(fraction: Float, startValue: RunObj, endValue: RunObj): RunObj {
                    val a = startValue.a + (endValue.a - startValue.a) * fraction
                    val b = startValue.b + (endValue.b - startValue.b) * fraction
                    return RunObj(a.toInt(), b.toInt())
                }
            }

            val animator = ValueAnimator.ofObject(RunType(), RunObj(0, 0), RunObj(100, 700))
            animator.duration = 2500
            animator.addUpdateListener {
                val value = it.animatedValue as RunObj
                iv_1.x = value.a.toFloat()
                iv_1.y = value.b.toFloat()
            }
            animator.start()
        })
        addView("ObjectAnimator.ofFloat", OnClickListener { cv ->
            val animator = ObjectAnimator.ofFloat(cv, "alpha", 1.0f, 0.3f, 1.0f)
            animator.duration = 2500
            animator.start()
        })
        addView("组合属性动画", OnClickListener { cv ->
            val animator = ObjectAnimator.ofFloat(cv, "alpha", 1.0f, 0.0f, 1.0f)
            val ani2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ObjectAnimator.ofArgb(cv, "textColor", Color.DKGRAY, Color.CYAN, Color.YELLOW)
            } else {
                ObjectAnimator.ofFloat(cv, "textScaleX", 1f, 1.2f, 0.7f, 1f)
            }

            val set = AnimatorSet()
            set.play(animator).with(ani2)
            set.duration = 2500
            set.start()
        })
        addView("插值器(Interpolator)", OnClickListener { cv ->
            val animator = ObjectAnimator.ofFloat(iv_1, "y", 0f, 300f)
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.duration = 1000
            animator.start()
        })

        // TODO: 2018/4/26 ----------------------------------------------------------
        addTitle("属性动画2（PropertyValuesHolder, ViewPropertyAnimator）")
        addView("Keyframe", OnClickListener { cv ->
            val kf1 = Keyframe.ofFloat(0f, 1.0f)
            val kf2 = Keyframe.ofFloat(0.3f, 0.4f)
            val kf3 = Keyframe.ofFloat(0.6f, 0.1f)
            val kf4 = Keyframe.ofFloat(0.8f, 0.8f)
            val kf5 = Keyframe.ofFloat(1f, 1f)
            val animator = ObjectAnimator.ofPropertyValuesHolder(cv, PropertyValuesHolder.ofKeyframe(View.ALPHA, kf1, kf2, kf3, kf4, kf5))
            animator.duration = 2500
            animator.start()
        })
        addView("ViewPropertyAnimator", OnClickListener { cv ->
            val animator = cv.animate()
            animator.alpha(if (cv.alpha < 1f) 1f else 0.5f)
            animator.duration = 2500
            animator.start()
        })
        // TODO: 2018/4/27 ----------------------------------------------------------
        addTitle("揭露动画")
        addView("hide", OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val anim = ViewAnimationUtils.createCircularReveal(iv_2, 50, 50
                        , Math.hypot(50.0, 50.0).toFloat(), 0f)
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        iv_2.visibility = View.GONE
                    }
                })
                anim.start()
            } else {
                Snackbar.make(flexboxLayout, "api < 21", Snackbar.LENGTH_LONG).show()
            }
        })
        addView("show", OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val anim = ViewAnimationUtils.createCircularReveal(iv_2, 50, 50
                        , 0f, Math.hypot(50.0, 50.0).toFloat())

                iv_2.visibility = View.VISIBLE
                anim.start()
            } else {
                Snackbar.make(flexboxLayout, "api < 21", Snackbar.LENGTH_LONG).show()
            }
        })
        // TODO: 2018/4/28 ----------------------------------------------------------
        addTitle("布局切换动画（AutoTransition，Fade，Slide，Explode，ChangeBounds...）")

        // https://developer.android.google.cn/training/transitions/
        addView("start", object : OnClickListener {
            var i = 0
            override fun onClick(v: View?) {
                val mSceneRoot = findViewById<FrameLayout>(R.id.fl_transition)
                if (i % 2 == 0) {
                    val startScene = Scene.getSceneForLayout(mSceneRoot, R.layout.ah_anim_area1, mActivity)
                    TransitionManager.go(startScene, Explode())
                } else {
                    val endScene = Scene.getSceneForLayout(mSceneRoot, R.layout.ah_anim_area2, mActivity)
//                    TransitionManager.go(endScene, AutoTransition())
//                    TransitionManager.go(endScene)
                    TransitionManager.go(endScene, Slide())
                }
                i++
            }
        })

        addView("Activity动画", OnClickListener {
            Snackbar.make(flexboxLayout, "查看链接：https://developer.android.google.cn/training/transitions/start-activity#java", Snackbar.LENGTH_LONG).show()
        })
    }
}