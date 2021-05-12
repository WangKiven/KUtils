package com.kiven.pushlibrary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kiven.kutils.logHelper.KLog

class ClickNotiActivity : AppCompatActivity() {

    //    private lateinit var textView: TextView
    companion object {
        internal var onClickNotiListener = fun(activity: Activity, contxt: String?) {
            activity.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        textView = TextView(this)
//        setContentView(textView)

        reloveIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        reloveIntent(intent)
    }

    private fun reloveIntent(intent: Intent?) {
        val sb = StringBuilder("你点击了通知\n\n")

        if (intent != null)
            showIntent(intent, sb)
        else
            sb.appendLine("没有收到Intent")

//        textView.text = sb.toString()
        KLog.i(sb.toString())

        /*val argu = intent?.data?.getQueryParameter("argu") ?: intent?.extras?.getString("argu")

        onClickNotiListener(this, argu)*/
        onClickNotiListener(this, PushClient.findData(intent))
    }

    private fun showIntent(newIntent: Intent, sb: StringBuilder) {

        newIntent.data?.apply {
            sb.append("Intent 的data(${this}): \n")
            try {
                queryParameterNames.forEach {
                    sb.append(" $it : ${getQueryParameter(it)}\n")
                }
            } catch (e: Exception) {
                sb.append(" 参数异常：${e.message}\n")
            }
        } ?: sb.append("Intent 没有data\n")

        newIntent.extras?.apply {
            sb.append("\nIntent 的extras: \n")

            keySet().forEach {
                sb.append(" $it : ${get(it)}\n")
            }

        } ?: sb.append("Intent 没有extras\n")
    }
}
