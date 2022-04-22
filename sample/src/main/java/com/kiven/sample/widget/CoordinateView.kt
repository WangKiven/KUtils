package com.kiven.sample.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.util.showTip
import java.util.*
import kotlin.math.abs
import kotlin.math.min

class CoordinateView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    var paint = Paint()
    private var canvasHeight = 0
    private var canvasWidth = 0
    private var data = floatArrayOf()
    val padding = KUtil.dip2px(10f).toFloat()

    // y轴对应的x值
    private var xo = 0f

    private var yMin = 0f
    private var yMax = 0f

    init {
        paint.isAntiAlias = true //抗锯齿
        setLayerType(LAYER_TYPE_SOFTWARE, paint) //关闭硬件加速，否则阴影绘制失败
    }

    fun changeData(newData: ByteArray) {
        if (newData.isEmpty()) {
            invalidate()
            return
        }
        yMin = newData[0].toFloat()
        yMax = yMin
        val nn = FloatArray(newData.size * 2)
        for ((i, newDatum) in newData.withIndex()) {
            nn[i * 2] = i.toFloat()
            val y = newDatum.toFloat()
            nn[i * 2 + 1] = y

            if (yMin > y) yMin = y
            if (yMax < y) yMax = y
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
        val yo = (canvasHeight / 2).toFloat()
//        canvas.drawLine(0f, canvasHeight - padding, canvasWidth.toFloat(), canvasHeight - padding, paint)
        canvas.drawLine(0f, yo, canvasWidth.toFloat(), yo, paint)
        // y轴
        canvas.drawLine(padding, canvasHeight.toFloat(), padding, 0f, paint)
        // 数据
        val yCenter = if (yo > yMax && yo > abs(yMin)) 0f else ((yMax + yMin) / 2); // 竖向中间位置的值, 有值超出界面时，才计算

        val v = (xo - padding).toInt() * 2
        val w = (xo - padding + canvasWidth).toInt() * 2
//        val dd = when {
//            data.isEmpty() -> floatArrayOf()
//            w == v -> floatArrayOf()
//            else -> data.copyOfRange(min(data.size, v), min(data.size, w))
//        }
//        dd.flatMapIndexed { index, fl -> listOf(index.toFloat(), fl) }

        canvas.drawPoints(data.mapIndexed { index, fl -> if (index % 2 == 0) (fl + padding - xo) else (yo - fl + yCenter) }
            .toFloatArray(), paint)

        // 最大，最小，交叉点
        paint.color = Color.RED
        canvas.drawText(yMax.toString(), padding, yo - yMax + yCenter, paint)
        canvas.drawText(yMin.toString(), padding, yo - yMin + yCenter, paint)
        canvas.drawText(yCenter.toString(), padding, yo, paint)
    }

    private var touchX = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                // 阻止父控件获得触摸操作，拦截触摸事件，防止与ViewPager等控件发生手势冲突
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x - touchX
                val mx = xo - x
                xo = when {
                    mx < 0 -> 0f
                    data.size /2 < mx -> {
                        data.size /2f
                    }
                    else -> mx
                }
                touchX = event.x

                invalidate()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {}
        }
        return true
    }
}