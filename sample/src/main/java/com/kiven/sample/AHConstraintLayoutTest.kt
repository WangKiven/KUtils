package com.kiven.sample

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.constraintlayout.widget.Placeholder
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import kotlinx.android.synthetic.main.ah_constraint_layout_test.*

/**
 * Created by wangk on 2019/5/5.
 *
 * 文档：https://www.jianshu.com/p/17ec9bd6ca8a
 * https://blog.csdn.net/guolin_blog/article/details/53122387
 */
class AHConstraintLayoutTest : KActivityHelper() {

    private val animator = ValueAnimator.ofFloat(0f, 360f)

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_constraint_layout_test)

        animator.addUpdateListener {
            val value = it.animatedValue as Float
            val textView5 = findViewById<TextView>(R.id.textView5)
            val params = textView5.layoutParams as ConstraintLayout.LayoutParams
            params.circleAngle = value

            textView5.layoutParams = params
        }
        animator.repeatCount = INFINITE
        animator.duration = 5000
        animator.start()
    }

    override fun onDestroy() {
        animator.end()
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.button3 -> findViewById<Button>(R.id.button3).visibility = View.GONE
            R.id.button4 -> {
                // 目前似乎需要固定需要显示的控件宽度，否则textview显示不正常
                val placeholder1 = findViewById<Placeholder>(R.id.placeholder1)
                val placeholder2 = findViewById<Placeholder>(R.id.placeholder2)
                if (placeholder1.content == null) {
                    placeholder1.setContentId(R.id.textView)
                    placeholder2.setContentId(0)
                } else {
                    placeholder1.setContentId(0)
                    placeholder2.setContentId(R.id.textView)
                }

                findViewById<Button>(R.id.button3).visibility = View.VISIBLE
            }
            R.id.button6 -> {
                val group = findViewById<Group>(R.id.group)
                group.visibility = if (group.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }
    }
}