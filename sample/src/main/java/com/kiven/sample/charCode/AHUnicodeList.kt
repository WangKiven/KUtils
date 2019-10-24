package com.kiven.sample.charCode

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import com.kiven.sample.util.showListDialog
import kotlinx.android.synthetic.main.item_unicode.view.*
import org.jetbrains.anko.button
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textView
import java.io.InputStreamReader


/**
 * Created by oukobayashi on 2019-10-24.
 */
class AHUnicodeList : KActivityDebugHelper() {

    var showSel: TextView? = null

    private val adapter = MyAdapter()
    private var charArray = IntArray(0)
    private var charCount = 0

    //    val startCode = 0x0000
//    val endCode = 0xFFFF
    var curGoup = UnicodeGroup("0000", "FFFF", "全部")

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        activity.linearLayout {
            orientation = LinearLayout.VERTICAL



            linearLayout {
                button {
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
                }

                button {
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
                }
            }

            textView {
                showSel = this
                onChangeSel()
            }


            val recyclerView = RecyclerView(activity)
            addView(recyclerView)

            val layoutManager = FlexboxLayoutManager(mActivity)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.CENTER

            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

    private fun onChangeSel() {
        val size = curGoup.end - curGoup.start + 1
        val ca = IntArray(size)

        var a = 0
        for (i in IntRange(curGoup.start, curGoup.end)){
            if (Character.isDefined(i)){
                ca[a] = i
                a++
            }
        }

        charArray = ca
        charCount = a

        adapter.notifyDataSetChanged()
        showSel?.text = "当前显示范围：${curGoup.startCode} - ${curGoup.endCode}：${curGoup.detail}，实际可用 $a/$size"
    }

    private inner class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val curCode = /*curGoup.start + position*/charArray[position]

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

//                PaintCompat.hasGlyph() // 确定绘图上的字体集是否有一个字形以向后兼容的方式支持字符串。
            }
        }

        override fun getItemCount(): Int = /*curGoup.end - curGoup.start + 1*/charCount

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