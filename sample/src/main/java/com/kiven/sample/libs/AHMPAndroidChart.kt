package com.kiven.sample.libs

import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import java.util.ArrayList

/**
 * 图表库 MPAndroidChart：https://github.com/PhilJay/MPAndroidChart
 */
class AHMPAndroidChart : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_mp_android_chart)
        val lineChart = findViewById<LineChart>(R.id.lineChart)


        val colors = intArrayOf(Color.rgb(137, 230, 81), Color.rgb(240, 240, 30), Color.rgb(89, 199, 250), Color.rgb(250, 104, 104))
        val data = getData(10, 100f)

//        val mTf = Typeface.createFromAsset(mActivity.assets, "OpenSans-Bold.ttf")
//        data.setValueTypeface(mTf)

        // add some transparency to the color with "& 0x90FFFFFF"
        setupChart(lineChart, data, colors[(Math.random() * 100 % colors.size).toInt()])
    }

    private fun setupChart(chart: LineChart, data: LineData, color: Int) {

        (data.getDataSetByIndex(0) as LineDataSet).circleHoleColor = color

        // no description text
        chart.description.isEnabled = false

        // chart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false)
        //        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true)

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(false)// 缩放

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false)

        // 边框，边框会覆盖xy轴线
        chart.setDrawBorders(false)

//        chart.setBackgroundColor(color)

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        // 自定义offsets， 设置后就不会自动计算了
//        chart.setViewPortOffsets(20f, 0f, 0f, 40f)

        // add data
        chart.data = data

        // 表头， 必须在设置data后设置
        val l = chart.legend
        l.isEnabled = true
//        l.form = Legend.LegendForm.LINE

        chart.axisRight.isEnabled = false
        chart.axisLeft.apply {
            isEnabled = true

            // 预留空间，及轴向最大(小)的点与轴向显示的最大(小)的值的差值
            spaceTop = 40f
            spaceBottom = 70f

//        y.isInverted = true // 轴反向
//        y.typeface = tfLight
            setLabelCount(5, false)
            textColor = Color.GREEN
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            setDrawLabels(true)

            setDrawAxisLine(true)// 显示y轴
            axisLineColor = Color.YELLOW // y轴颜色


            setDrawZeroLine(false)// 显示位置0处的水平线
            setDrawGridLines(false)
        }

        chart.xAxis.apply {
            isEnabled = true
//            setLabelCount(5, false)
            setDrawLabels(true)
            disableAxisLineDashedLine()
            textColor = Color.BLACK
            position = XAxis.XAxisPosition.BOTTOM
//            spaceMin = 1f // 最小空白区域，坐标开始位置与第一个数据之间的单位距离知识留这么多

            setDrawAxisLine(true)// 显示x轴
            setDrawGridLines(false)// 刻度处显示竖向线

            // 限制线
            val ll1 = LimitLine(7f, "风险较高")
            ll1.lineWidth = 2f
            ll1.textColor = Color.RED
            ll1.enableDashedLine(10f, 10f, 0f)
//        ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll1.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            ll1.textSize = 10f
//        ll1.typeface = tfRegular

            addLimitLine(ll1)
        }


        // animate calls invalidate()...
        chart.animateY(1000)
    }

    private fun getData(count: Int, range: Float): LineData {

        val values = arrayListOf<Entry>()

        for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat() + 3
            values.add(Entry(i.toFloat(), `val`))
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "DataSet 1")
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

//        set1.cubicIntensity = 0.2f
        set1.lineWidth = 1.75f
        set1.circleRadius = 5f
        set1.circleHoleRadius = 2.5f
        set1.color = Color.WHITE
        set1.setCircleColor(Color.WHITE)
        set1.highLightColor = Color.WHITE
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER// 贝塞尔曲线
        set1.setDrawValues(false) // 显示值
        set1.setDrawCircles(false) // 圆圈、圆点

        // 辅助线, 点击点后，显示的交叉线
//        set1.setDrawVerticalHighlightIndicator(false)
//        set1.setDrawHorizontalHighlightIndicator(false)
        set1.setDrawHighlightIndicators(true)

        // 填充
        set1.setDrawFilled(true)
        set1.fillColor = Color.WHITE

        // create a data object with the data sets
//        return LineData(set1)


        val set2 = LineDataSet(listOf(values[2], values[values.size - 2]), "DataSet 2")
        set2.setDrawValues(true) // 显示值
        set2.setDrawCircles(true) // 圆圈、圆点
        set2.color = Color.TRANSPARENT //
        set2.setCircleColor(Color.WHITE)
        set2.fillColor = Color.BLUE
        set2.valueFormatter = object : ValueFormatter() {
            override fun getPointLabel(entry: Entry?): String {
                return "点：${entry?.x?.toInt()}"
            }
        }


        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set2)
        dataSets.add(set1)

        return LineData(dataSets)
    }
}