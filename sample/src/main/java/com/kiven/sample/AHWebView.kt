package com.kiven.sample

import android.os.Bundle
import android.webkit.WebView
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KView

/**
 * Created by oukobayashi on 2019-10-24.
 */
class AHWebView : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val webView = WebView(activity)
        setContentView(webView)

        KView.initWebView(webView)

        webView.loadUrl(activity.intent.getStringExtra("url"))
    }
}