package com.kiven.sample

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.ui.unit.dp
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KString
import com.kiven.kutils.tools.KView
import com.kiven.sample.util.showSnack

/**
 * Created by oukobayashi on 2019-10-24.
 */
class AHWebView : KActivityHelper() {
    private val webView by lazy { WebView(mActivity) }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        setContentView(webView)

        KView.initWebView(webView)

        // 防止打开浏览器。return false:表示没处理链接，交由系统处理。
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null || view == null) return super.shouldOverrideUrlLoading(view, url)

                KLog.i(url)


                view.loadUrl(url)
                return true
            }
        }

        val url = activity.intent.getStringExtra("url") ?: ""
        webView.loadUrl(url)

        val backView = ImageView(activity)
        backView.setImageResource(R.drawable.emoji_backspace)
        backView.setOnClickListener { finish() }

        val shareView = ImageView(activity)
        shareView.setImageResource(R.drawable.ic_send)
        shareView.setOnClickListener {
            KString.setClipText(mActivity, url)
            activity.showSnack("已复制链接")
        }
        (webView.parent as ViewGroup).apply {
            val wc = ViewGroup.LayoutParams.WRAP_CONTENT
            addView(backView, ViewGroup.LayoutParams(wc, wc))

            val f = FrameLayout.LayoutParams(wc, wc)
            f.rightMargin = 15.dp.value.toInt()
            addView(shareView, f)
        }
    }

    override fun onBackPressed(): Boolean {

        if (webView.canGoBack()) {
            webView.goBack()
            return false
        }

        return super.onBackPressed()
    }
}