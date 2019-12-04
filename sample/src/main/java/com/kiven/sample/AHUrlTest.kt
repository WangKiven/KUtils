package com.kiven.sample

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KTextView
import com.kiven.kutils.tools.KView
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.sdk.DownloadListener
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.ah_url_test.*

/**
 * Created by oukobayashi on 2019-12-04.
 */
class AHUrlTest : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        mActivity.apply {
            setContentView(R.layout.ah_url_test)

            webView.apply {
                KView.initWebView(this)
                /*webChromeClient =  object :WebChromeClient (){
                    override fun onPermissionRequest(request: PermissionRequest) {
                        KLog.i(request.resources.joinToString())
                        request.grant(request.resources)
                    }
                }*/

                loadUrl("https://www.baidu.com")
            }
            webView_tbs.apply {
                initX5WebView(this)

                loadUrl("https://www.baidu.com")
            }

            btn_go.setOnClickListener {
                val url = KTextView.getTrim(editText)
                if (webView.url == url) {
                    webView.reload()
                    webView_tbs.reload()
                }else {
                    webView.loadUrl(url)
                    webView_tbs.loadUrl(url)
                }
            }

            btn_change.setOnClickListener {
//                viewSwitcher.displayedChild = (viewSwitcher.displayedChild + 1) % viewSwitcher.childCount
                viewFlipper.displayedChild = (viewFlipper.displayedChild + 1) % viewFlipper.childCount
            }
        }
    }

    fun initX5WebView(webview: WebView) {
        /*webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                // mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);
                mTestHandler.sendEmptyMessageDelayed(com.example.test_webview_demo.BrowserActivity.MSG_OPEN_TEST_URL, 5000) // 5s?
                if (Build.VERSION.SDK.toInt() >= 16) changGoForwardButton(view)
            }
        })*/

        webview.setWebChromeClient(object : WebChromeClient() {
            override fun onJsConfirm(arg0: WebView, arg1: String, arg2: String,
                                     arg3: JsResult): Boolean {
                return super.onJsConfirm(arg0, arg1, arg2, arg3)
            }

            var myVideoView: View? = null
            var myNormalView: View? = null
            var callback: IX5WebChromeClient.CustomViewCallback? = null
            // /////////////////////////////////////////////////////////
//
            /**
             * 全屏播放配置
             */
            override fun onShowCustomView(view: View,
                                          customViewCallback: IX5WebChromeClient.CustomViewCallback) {
                val normalView = findViewById<View>(R.id.web_filechooser) as FrameLayout
                val viewGroup = normalView.parent as ViewGroup
                viewGroup.removeView(normalView)
                viewGroup.addView(view)
                myVideoView = view
                myNormalView = normalView
                callback = customViewCallback
            }

            override fun onHideCustomView() {
                if (callback != null) {
                    callback!!.onCustomViewHidden()
                    callback = null
                }
                if (myVideoView != null) {
                    val viewGroup = myVideoView!!.parent as ViewGroup
                    viewGroup.removeView(myVideoView)
                    viewGroup.addView(myNormalView)
                }
            }

            override fun onJsAlert(arg0: WebView, arg1: String, arg2: String,
                                   arg3: JsResult): Boolean {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3)
            }
        })

        webview.setDownloadListener(DownloadListener { arg0, arg1, arg2, arg3, arg4 ->
            KLog.i("url: $arg0")
            AlertDialog.Builder(mActivity)
                    .setTitle("allow to download？")
                    .setPositiveButton("yes"
                    ) { dialog, which ->
                        Toast.makeText(
                                mActivity,
                                "fake message: i'll download...",
                                Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("no"
                    ) { dialog, which ->
                        // TODO Auto-generated method stub
                        Toast.makeText(
                                mActivity,
                                "fake message: refuse download...",
                                Toast.LENGTH_SHORT).show()
                    }
                    .setOnCancelListener {
                        // TODO Auto-generated method stub
                        Toast.makeText(
                                mActivity,
                                "fake message: refuse download...",
                                Toast.LENGTH_SHORT).show()
                    }.show()
        })

        val webSetting: WebSettings = webview.getSettings()
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        /*webSetting.setAppCachePath(this.getDir("appcache", 0).getPath())
        webSetting.databasePath = this.getDir("databases", 0).getPath()
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath())*/
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
    }
}