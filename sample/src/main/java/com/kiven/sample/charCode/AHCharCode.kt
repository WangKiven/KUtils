package com.kiven.sample.charCode

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KString
import com.kiven.sample.util.getInput
import com.kiven.sample.util.showListDialog
import org.jetbrains.anko.button
import org.jetbrains.anko.dip
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textView
import java.nio.charset.Charset
import kotlin.experimental.and

/**
 * Created by oukobayashi on 2019-10-23.
 *
 * https://zh.wikipedia.org/wiki/UTF-8#Windows
 * https://zh.wikipedia.org/wiki/ASCII
 * https://zh.wikipedia.org/wiki/%E7%B5%84%E5%90%88%E5%AD%97%E7%AC%A6
 * https://home.unicode.org/
 * unicode 与 utf-8 转化： https://blog.csdn.net/qq_36761831/article/details/82291166
 */
class AHCharCode : KActivityDebugHelper() {

    private var textView: TextView? = null

    private var textCode = "0f5d"
    private var textChinese = "马"

    private var useCharset = Charsets.UTF_8
    set(value) {
        field = value
        textView?.text = "使用编码: $value"
    }
    private val charsets = arrayOf(
            Charsets.UTF_8,
            Charsets.UTF_16,
            Charsets.UTF_16BE,
            Charsets.UTF_16LE,
            Charsets.US_ASCII,
            Charsets.ISO_8859_1,
            Charsets.UTF_32,
            Charsets.UTF_32LE,
            Charsets.UTF_32BE,
            Charset.forName("gbk") // = Charset.forName("gb2312")
    )

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        mActivity.linearLayout {
            orientation = LinearLayout.VERTICAL
            textView {
                textView = this
                text = "点击按钮显示结果"
                gravity = Gravity.CENTER
                left = dip(50)
                textSize = 25f
                setOnClickListener {
                    KString.setClipText(mActivity, text.toString())
                }
            }

            button {
                text = "16进制 转 字符"
                setOnClickListener {
                    mActivity.getInput("16进制", textCode) {
                        textView?.text = hexStr2Str(it.toString(), useCharset)
                        textCode = it.toString()
                    }
                }
            }
            button {
                text = "字符 转 16进制"
                setOnClickListener {
                    mActivity.getInput("汉字", textChinese) {
                        textView?.text = str2HexStr(it.toString(), useCharset)
                        textChinese = it.toString()
                    }
                }
            }

            button {
                text = "选择编码 - 所有可用编码"

                val cs = Charset.availableCharsets().keys.toList()
                setOnClickListener {
                    mActivity.showListDialog(
                            cs.mapIndexed { i, c -> "$i - $c" }
                    ) { index, _ ->
                        useCharset = Charset.forName(cs[index])
                    }
                }
            }

            button {
                text = "选择编码 - 仅列出常用编码"
                setOnClickListener {
                    mActivity.showListDialog(
                            charsets.map { it.toString() }
                    ) { index, _ ->
                        useCharset = charsets[index]
                    }
                }
            }
        }
    }

    private fun hexStr2Str(hexStr: String, charset: Charset = Charsets.UTF_8): String {
        if (hexStr.isBlank())
            return ""

        val str = "0123456789abcdef"

        val hexs = hexStr.toCharArray()
        val bytes = ByteArray(hexs.size / 2)

        for (i in bytes.indices) {
            var n = str.indexOf(hexs[2 * i]) * 16
            n += str.indexOf(hexs[2 * i + 1])

            bytes[i] = (n and 0xff).toByte()
        }

        return String(bytes, charset)
    }

    private fun str2HexStr(str: String, charset: Charset = Charsets.UTF_8): String {
        val chars = "0123456789abcdef".toCharArray()

        val sb = StringBuilder()
        val bs = str.toByteArray(charset)

        for (i in bs.indices) {
            val bit = (bs[i].toLong() and 0x0f0) shr 4
            sb.append(chars[bit.toInt()])

            val bit2 = bs[i] and 0x0f
            sb.append(chars[bit2.toInt()])
        }

        return sb.toString().trim()
    }
}