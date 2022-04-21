package com.kiven.sample.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kiven.kutils.tools.KUtil

class CoordinateView(context: Context, attrs: AttributeSet?): View(context, attrs) {
    var paint = Paint()
    private var canvasHeight = 0
    private var canvasWidth = 0
    private var data = floatArrayOf()
    val padding = KUtil.dip2px(10f).toFloat()
    init {
        paint.isAntiAlias = true //抗锯齿
        setLayerType(LAYER_TYPE_SOFTWARE, paint) //关闭硬件加速，否则阴影绘制失败
    }

    fun changeData(newData: ByteArray) {
        val nn = FloatArray(newData.size * 2)
        for ((i, newDatum) in newData.withIndex()) {
            nn[i*2] = i.toFloat() + padding
            nn[i*2 + 1] = newDatum.toFloat() + padding
        }
        data = nn
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasHeight = h
        canvasWidth = w
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

//        canvas.drawColor(Color.WHITE);// 背景，不需要绘制背景，父类super.onDraw(canvas)已经实现了背景绘制
        paint.color = Color.BLACK

        // x轴
//        canvas.drawLine(0f, canvasHeight - padding, canvasWidth.toFloat(), canvasHeight - padding, paint)
        canvas.drawLine(0f, padding, canvasWidth.toFloat(), padding, paint)
        // y轴
        canvas.drawLine(padding, canvasHeight.toFloat(), padding, 0f, paint)
        // 数据
        canvas.drawPoints(data, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}