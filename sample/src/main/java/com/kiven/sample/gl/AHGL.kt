package com.kiven.sample.gl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity


/**
 * opengl
 * Created by wangk on 2018/5/12.
 */
class AHGL : KActivityHelper() {
    private lateinit var surfaceView: GLSurfaceView

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(flexboxLayout)

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

        addTitle("OpenGL sample")
        addView("简单图形", View.OnClickListener {
            AHGLSample().startActivity(mActivity)
        })
        addView("光照面", View.OnClickListener {
            AHGLLightFace().startActivity(mActivity)
        })
        addView("光照球", View.OnClickListener {
            AHGLLight().startActivity(mActivity)
        })
        addView("发光物体", View.OnClickListener {
            AHGLShine().startActivity(mActivity)
        })
        addView("", View.OnClickListener {

        })
    }
}