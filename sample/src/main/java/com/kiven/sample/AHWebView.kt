package com.kiven.sample

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KView

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

        webView.loadUrl(activity.intent.getStringExtra("url"))

        val backView = ImageView(activity)
        backView.setImageResource(R.drawable.emoji_backspace)
        backView.setOnClickListener { finish() }
        (webView.parent as ViewGroup).addView(backView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    override fun onBackPressed(): Boolean {

        if (webView.canGoBack()) {
            webView.goBack()
            return false
        }

        return super.onBackPressed()
    }
}