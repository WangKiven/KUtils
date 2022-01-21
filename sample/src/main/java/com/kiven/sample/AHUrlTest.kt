package com.kiven.sample

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KTextView
import com.kiven.kutils.tools.KView
import com.kiven.sample.databinding.AhUrlTestBinding
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView

/**
 * Created by oukobayashi on 2019-12-04.
 */
class AHUrlTest : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        mActivity.apply {
            val binding = AhUrlTestBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.webView.apply {
                KView.initWebView(this)
                /*webChromeClient =  object :WebChromeClient (){
                    override fun onPermissionRequest(request: PermissionRequest) {
                        KLog.i(request.resources.joinToString())
                        request.grant(request.resources)
                    }
                }*/

                loadUrl("https://www.baidu.com")
            }
            binding.webViewTbs.apply {
                initX5WebView(this)

                loadUrl("https://www.baidu.com")
            }

            findViewById<Button>(R.id.btn_go).setOnClickListener {
                val url = KTextView.getTrim(findViewById(R.id.editText))
                if (binding.webView.url == url) {
                    binding.webView.reload()
                    binding.webViewTbs.reload()
                }else {
                    binding.webView.loadUrl(url)
                    binding.webViewTbs.loadUrl(url)
                }
            }

            binding.btnChange.setOnClickListener {
                val viewFlipper = findViewById<ViewFlipper>(R.id.viewFlipper)
                viewFlipper.displayedChild = (viewFlipper.displayedChild + 1) % viewFlipper.childCount
            }
        }
    }

    private fun initX5WebView(webview: WebView) {

        webview.setDownloadListener { arg0, arg1, arg2, arg3, arg4 ->
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
        }

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