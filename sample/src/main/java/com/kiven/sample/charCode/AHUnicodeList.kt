package com.kiven.sample.charCode

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

//    val startCode = 0x0000
//    val endCode = 0xFFFF
    var curGoup = UnicodeGroup("0000", "FFFF", "全部")

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        activity.linearLayout {
            orientation = LinearLayout.VERTICAL

            button {
                text = "选择分组(当前：全部)"
                setOnClickListener {
                    val fileInput = activity.assets.open("unicode_code_detail.txt")
                    val reader = InputStreamReader(fileInput)
                    val groups = reader.readLines()

                    activity.showListDialog(groups) { index, _ ->
                        groups[index].apply {
                            curGoup = UnicodeGroup(substring(0, 4), substring(5, 9), substring(10))

                            adapter.notifyDataSetChanged()
                            onChangeSel()
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
        showSel?.text = "当前显示范围：${curGoup.startCode} - ${curGoup.endCode}：${curGoup.detail}"
    }

    private inner class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val curCode = curGoup.start + position

            holder.itemView.apply {
                // 采用UTF_32 大端解码，如果使用UTF-8需要更复杂的处理
                tv_text.text = StringCodeUtil.hexStr2Str(String.format("%08x", curCode), Charsets.UTF_32BE)
                tv_code.text = String.format("%x", curCode)
            }
        }

        override fun getItemCount(): Int = curGoup.end - curGoup.start + 1

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