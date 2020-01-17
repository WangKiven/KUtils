package com.kiven.sample.noti

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.Orientation
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textView
import java.lang.Exception

class ClickNotiActivity : AppCompatActivity() {

    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textView = TextView(this)
        textView?.text = "没有收到Intent"

        linearLayout {
            orientation = LinearLayout.VERTICAL
            textView { text = "你点击了通知" }

            addView(textView)
        }

        if (intent != null)
            showIntent(intent)
        else
            textView?.text = "没有收到Intent"
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null)
            showIntent(intent)
        else
            textView?.text = "没有收到新的Intent"
    }

    private fun showIntent(newIntent: Intent) {
        val sb = StringBuilder()

        newIntent.data?.apply {
            sb.append("Intent 的data(${this}): \n")
            try {
                queryParameterNames.forEach {
                    sb.append(" $it : ${getQueryParameter(it)}\n")
                }
            }catch (e:Exception) {
                sb.append(" 参数异常：${e.message}\n")
            }
        } ?: sb.append("Intent 没有data\n")

        newIntent.extras?.apply {
            sb.append("Intent 的extras: \n")

            keySet().forEach {
                sb.append(" $it : ${get(it)}\n")
            }

        } ?: sb.append("Intent 没有extras\n")


        textView?.text = sb
    }
}
