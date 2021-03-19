package com.kiven.sample.font

import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import java.io.File
import java.io.FileInputStream

/**
 * Created by oukobayashi on 2020/6/23.
 */
class AHFont : KActivityHelper() {
    private val files = mutableListOf<File>()

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val listView = ListView(activity)
        setContentView(listView)


        val floder = File("system/fonts")
        if (floder.exists()) {
            if (floder.isFile) {
                log("是文件")
            } else if (floder.isDirectory) {
                files.addAll(floder.listFiles() ?: emptyArray())
                if (files.isNotEmpty()) {
                    // 排序
                    files.sortedWith(kotlin.Comparator { o1, o2 -> o1.name.compareTo(o2.name) })
                    listView.adapter = MyAdapter()
                } else {
                    log("是空文件夹")
                }
            } else {
                log("都不是")
            }
        } else {
            log("文件夹不存在")
        }
        val systemFontConfigLocation = File("/system/etc/")
        val configFilename = File(systemFontConfigLocation, "fonts.xml")
        /*File configFilename = new File(systemFontConfigLocation, "fallback_fonts.xml");*/
        try {
            /*for (File file : systemFontConfigLocation.listFiles()) {
                KLog.i(file.getAbsolutePath());
            }*/
            val fin = FileInputStream(configFilename)
            val length = fin.available()
            val buffer = ByteArray(length)
            fin.read(buffer)
            KLog.i(String(buffer))
            fin.close()
        } catch (e: Exception) {
        }
    }

    private fun log(info: String) {
        KLog.e(info)
    }

    private inner class MyAdapter : BaseAdapter() {
        private var assetManager: AssetManager? = null
        private var ttfParser: TTFParser? = null
        override fun getCount(): Int {
            return files.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return files[position]
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convertView: View? = convertView
            var textView: TextView? = null
            if (convertView == null) {
                textView = TextView(mActivity)
                textView.setPadding(10, 10, 10, 10)
                textView.setBackgroundColor(Color.parseColor("#eeeeee"))
                textView.setTextColor(Color.parseColor("#000000"))
                convertView = textView
            } else {
                textView = convertView as TextView?
            }
            val file = getItem(position) as File
            if (file.isFile) {
                if (file.name.endsWith(".ttf") || file.name.endsWith(".otf")) {
                    try {
                        val typeface = Typeface.createFromFile(file.path)
                        ttfParser!!.parse(file.path)
                        textView!!.typeface = typeface
                        textView.text = """$position ${file.name}

字体名称: ${ttfParser!!.fontName}

 familyName: ${ttfParser!!.getFontPropertie(TTFParser.FAMILY_NAME)}

 subFamilyName: ${ttfParser!!.getFontPropertie(TTFParser.FONT_SUBFAMILY_NAME)}

Hello word, 1234567890 !

你好,世界!"""
                    } catch (e: Exception) {
                        textView!!.text = file.name + "创建错误"
                    }
                } else {
                    textView!!.text = file.name + " 不是TTF文件"
                }
            } else {
                textView!!.text = file.name + " 不是文件"
            }
            return convertView
        }

        init {
            assetManager = mActivity.assets
            ttfParser = TTFParser()
        }
    }
}