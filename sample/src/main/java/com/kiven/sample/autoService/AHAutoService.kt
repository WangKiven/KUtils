package com.kiven.sample.autoService

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.floatView.ServiceFloat
import com.kiven.sample.util.showListDialog
import com.kiven.sample.util.showSnack
import com.sch.share.WXShareMultiImageHelper
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * Created by oukobayashi on 2019-10-31.
 */
class AHAutoService : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

        val addTitle = fun(text: String): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)

            return tv
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }
        // TODO: 2018/3/28 ----------------------------------------------------------
        val txtTag = addTitle("未选择标签")

//        val tags = mutableListOf<String>()
        val selTags = mutableListOf<String>()

        txtTag.setOnClickListener {
            selTags.clear()
            txtTag.text = "未选择标签"
        }
        addView("按标签分享", View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!KUtil.canDrawOverlays()) {
                    KUtil.startOverlaySetting()
                    return@OnClickListener
                }
            }
//            KUtil.startService(ServiceFloat::class.java)


            if (selTags.isEmpty()) {
                if (WXConst.frindsTags.isEmpty()){
                    AutoInstallService.task = WXLoadTagTask()
                }else{
                    //
                    mActivity.showListDialog(WXConst.frindsTags.toList().map { "${it.first}(${it.second})" }){index, what ->
                        selTags.add(WXConst.frindsTags.toList()[index].first)
                        txtTag.text = "已选标签：${selTags.joinToString()}"
                    }

                    return@OnClickListener
                }
            }else{
                AutoInstallService.task = WXShareTask(tagForFriends = selTags, isSendTags = true)
            }

            if (!AutoInstallService.isStarted) {
                AccessibilityUtil.jumpToSetting(mActivity)
            }
        })
        addView("微信无障碍lib", View.OnClickListener {
            KGranting.requestPermissions(mActivity, 899, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), arrayOf("内存")) {
                if (it) {
//                    val images = listOf(BitmapFactory.decodeResource(resources, R.mipmap.fish))
                    val images = resources.assets.list("wallpaper")!!.map { BitmapFactory.decodeStream(resources.assets.open("wallpaper/$it")) }

                    if (WXShareMultiImageHelper.isServiceEnabled(mActivity)) {
                        WXShareMultiImageHelper.shareToTimeline(mActivity, images)
                    } else {
                        WXShareMultiImageHelper.openService(mActivity) {
                            if (it) {
                                val options = WXShareMultiImageHelper.Options()
                                options.isAutoFill = it
                                WXShareMultiImageHelper.shareToTimeline(mActivity, images, options)
                            } else mActivity.showSnack("无障碍开启失败")
                        }
                    }
                }
            }
        })
    }
}