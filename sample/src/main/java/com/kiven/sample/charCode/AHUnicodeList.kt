package com.kiven.sample.charCode

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KString
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.AHWebView
import com.kiven.sample.R
import com.kiven.sample.util.showListDialog
import kotlinx.android.synthetic.main.item_unicode.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textView
import java.io.InputStreamReader
import java.nio.charset.Charset


/**
 * Created by oukobayashi on 2019-10-24.
 */
class AHUnicodeList : KActivityHelper() {

    var showSel: TextView? = null

    private val recyclerView by lazy {
        RecyclerView(mActivity)
    }
    private val adapter = MyAdapter()
    private var charArray = IntArray(0)
//    private var charCount = 0

    //    val startCode = 0x0000
//    val endCode = 0xFFFF
    var curGoup = UnicodeGroup("0000", "FFFF", "全部")

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        activity.linearLayout {
            orientation = LinearLayout.VERTICAL

            val flexBox = FlexboxLayout(mActivity)
            flexBox.flexDirection = FlexDirection.ROW
            flexBox.justifyContent = JustifyContent.CENTER
            addView(flexBox)

            flexBox.apply {
                addView(Button(mActivity).apply {
                    text = "选择0号平面分组"
                    setOnClickListener {
                        val fileInput = activity.assets.open("unicode_code_detail.txt")
                        val reader = InputStreamReader(fileInput)
                        val groups = reader.readLines()

                        activity.showListDialog(groups) { index, _ ->
                            groups[index].apply {
                                curGoup = UnicodeGroup(substring(0, 4), substring(5, 9), substring(10))

                                onChangeSel()
                            }
                        }
                    }
                })
                addView(Button(mActivity).apply {
                    text = "选择其他平面"
                    setOnClickListener {
                        val groups = arrayOf(
                                UnicodeGroup("10000", "1FFFF", "多文种补充平面"),
                                UnicodeGroup("20000", "2FFFF", "表意文字补充平面"),
                                UnicodeGroup("30000", "3FFFF", "表意文字第三平面（未正式使用）"),
                                UnicodeGroup("40000", "DFFFF", "（尚未使用）"),
                                UnicodeGroup("E0000", "EFFFF", "特别用途补充平面"),
                                UnicodeGroup("F0000", "FFFFF", "保留作为私人使用区（A区）"),
                                UnicodeGroup("100000", "10FFFF", "保留作为私人使用区（B区）")
                        )
                        activity.showListDialog(groups.map { "${it.startCode} - ${it.endCode}: ${it.detail}" }) { index, _ ->
                            groups[index].apply {
                                curGoup = groups[index]
                                onChangeSel()
                            }
                        }
                    }
                })
                addView(Button(mActivity).apply {
                    text = "滚动"

                    setOnClickListener {
                        if (charArray.isNotEmpty()) {
                            selScroll()
                        }

//                        recyclerView.scrollToPosition(0x6000)
                    }
                })
                addView(Button(mActivity).apply {
                    text = "更多"
                    setOnClickListener { AHCharCode().startActivity(activity) }
                })
            }


            textView {
                showSel = this
                onChangeSel()
            }


            addView(recyclerView)

            /*val layoutManager = FlexboxLayoutManager(mActivity)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.CENTER

            recyclerView.layoutManager = layoutManager*/ // 由于要计算每一个单元格的位置，当item太多的时候，会消耗大量内存存储相关数据
            recyclerView.layoutManager = GridLayoutManager(mActivity, KUtil.getScreenWith() / dip(70))
            recyclerView.adapter = adapter
        }
    }

    private fun selScroll() {

        val aa = "0123456789ABCDEF".toCharArray().map { it.toString() }
        val maxLength = String.format("%x", charArray.last()).length
        val ss = IntArray(maxLength)
        var step = 0

        mActivity.showListDialog(aa, false) { index, _ ->

            // 记录选择的数据
            if (step >= maxLength) return@showListDialog

            ss[step] = index
            step++


            // 计算选择的值
            var selValue = 0
            for (i in 0 until maxLength) {
                selValue = (selValue * 16) + ss[i]
            }

            // 找到位置并滚动
            val ii = charArray.indexOfFirst { it >= selValue }
//            KLog.i(String.format("%08x ---- %08x", selValue, ii))
            recyclerView.scrollToPosition(if (ii < 0) charArray.size - 1 else ii)

        }
    }

    private fun onChangeSel() {
        val size = curGoup.end - curGoup.start + 1
        val ca = IntArray(size)

        var a = 0
        for (i in IntRange(curGoup.start, curGoup.end)) {
            if (Character.isDefined(i)) {
                ca[a] = i
                a++
            }
        }

        charArray =
                if (a > 0) IntArray(a) { ca[it] }
                else IntArray(0)


        adapter.notifyDataSetChanged()
        showSel?.text = "当前显示范围：${curGoup.startCode} - ${curGoup.endCode}：${curGoup.detail}，实际可用 $a/$size"
    }

    private inner class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val curCode = charArray[position]

            holder.itemView.apply {
                // 判断字符有没有对应的unicode形式，就是通过unicode中是否定义了字符的unicode写法
                if (Character.isDefined(curCode)) {
                    // 采用UTF_32 大端解码，如果使用UTF-8需要更复杂的处理
                    tv_text.text = StringCodeUtil.hexStr2Str(String.format("%08x", curCode), Charsets.UTF_32BE)
                    tv_text.setTextColor(Color.BLACK)
                } else {
                    tv_text.text = "XX"
                    tv_text.setTextColor(Color.GRAY)
                }

                tv_code.text = String.format("%x", curCode)

                setOnClickListener {

                    val sb = StringBuilder()
                    sb.appendln("编号：$curCode - ${tv_code.text}")
                            .appendln("\n对应其他编码")
                    Charset.availableCharsets().forEach {
                        sb.appendln("${it.key}:${StringCodeUtil.str2HexStr(tv_text.text.toString(), it.value)}")
                    }

                    val builder = AlertDialog.Builder(mActivity)
                    builder.setMessage(sb.toString())
                    builder.setPositiveButton("复制") { dialog, which ->
                        KString.setClipText(mActivity, tv_text.text.toString())
                    }
                    builder.setNegativeButton("百度") { dialog, which ->
                        AHWebView().putExtra("url", "https://www.baidu.com/s?wd=${tv_text.text}")
                                .startActivity(mActivity)
                    }
                    builder.setNeutralButton("取消") { dialog, which ->

                    }
                    builder.create().show()
                }

//                PaintCompat.hasGlyph() // 确定绘图上的字体集是否有一个字形以向后兼容的方式支持字符串。
            }
        }

        override fun getItemCount(): Int = charArray.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                object : RecyclerView.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_unicode, parent, false)) {}
    }

    data class UnicodeGroup(val startCode: String, val endCode: String, val detail: String) {
        val start: Int
            get() {
                return startCode.toInt(16)
            }

        val end: Int
            get() {
                return endCode.toInt(16)
            }
    }
}