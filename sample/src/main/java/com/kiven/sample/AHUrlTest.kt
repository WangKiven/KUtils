package com.kiven.sample

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KTextView
import com.kiven.kutils.tools.KView
import com.kiven.sample.databinding.AhUrlTestBinding
import com.kiven.sample.util.pickPhoneImage
import com.kiven.sample.util.randomPhoneImage
import com.kiven.sample.util.showToast
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewCallbackClient

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
                webChromeClient = object: WebChromeClient() {
                    override fun onShowFileChooser(
                        webView: android.webkit.WebView?,
                        filePathCallback: ValueCallback<Array<Uri>>?,
                        fileChooserParams: FileChooserParams?
                    ): Boolean {
//                        val b = super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                        KLog.i("onShowFileChooser mode=${fileChooserParams?.mode} acceptTypes=${fileChooserParams?.acceptTypes} isCaptureEnabled=${fileChooserParams?.isCaptureEnabled} title=${fileChooserParams?.title}")
                        mActivity.randomPhoneImage {
                            selectImage {
                                filePathCallback?.onReceiveValue(if (it == null) arrayOf() else arrayOf(it))
                            }
                        }
                        return true
                    }
                }

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

            binding.btnBack.setOnClickListener { if (binding.webView.canGoBack()) binding.webView.goBack() }
            binding.btnNext.setOnClickListener { if (binding.webView.canGoForward()) binding.webView.goForward() }
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
        webview.webChromeClient = object: com.tencent.smtt.sdk.WebChromeClient() {
            override fun openFileChooser(
                p0: com.tencent.smtt.sdk.ValueCallback<Uri>?,
                p1: String?,
                p2: String?
            ) {
//                super.openFileChooser(p0, p1, p2)

                selectImage {
                    p0?.onReceiveValue(it)
                }
                KLog.i("x5 openFileChooser $p1 $p2")
            }

            override fun onShowFileChooser(
                p0: WebView?,
                p1: com.tencent.smtt.sdk.ValueCallback<Array<Uri>>?,
                p2: FileChooserParams?
            ): Boolean {
//                val b = super.onShowFileChooser(p0, p1, p2)
                KLog.i("x5 onShowFileChooser mode=${p2?.mode} acceptTypes=${p2?.acceptTypes} isCaptureEnabled=${p2?.isCaptureEnabled} title=${p2?.title}")
                selectImage {
                    p1?.onReceiveValue(if (it == null) arrayOf() else arrayOf(it))
                }
                return true
            }
        }
    }

    fun selectImage(call: (Uri?) -> Unit) {
        mActivity.pickPhoneImage({
            showToast(it)
            call(null)
        }, {
            call(it)
        })
    }
}