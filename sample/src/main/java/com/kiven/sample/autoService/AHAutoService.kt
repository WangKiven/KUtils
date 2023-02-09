package com.kiven.sample.autoService

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.autoService.wechat.WXConst
import com.kiven.sample.autoService.wechat.WXLoadTagTask
import com.kiven.sample.autoService.wechat.WXShareTask
import com.kiven.sample.util.showListDialog
import com.kiven.sample.util.showSnack

/**
 * Created by oukobayashi on 2019-10-31.
 */
class AHAutoService : KActivityHelper() {
    private val selTags = mutableListOf<String>()

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(NestedScrollView(activity).apply {
            addView(flexboxLayout)
        })

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


        txtTag.setOnClickListener {
            selTags.clear()
            txtTag.text = "未选择标签"
        }

        val startWXTask = fun(task: AutoTaskInterface) {
            AutoInstallService.startWXTask(mActivity, task)
        }

        addView("获取微信标签", View.OnClickListener {
            WXConst.frindsTags.clear()
            startWXTask(WXLoadTagTask())
        })

        addView("选择标签", View.OnClickListener {
            if (WXConst.frindsTags.isEmpty()) {
                mActivity.showSnack("请先获取微信标签")
            } else {
                mActivity.showListDialog(WXConst.frindsTags.toList().map { "${it.first}(${it.second})" }) { index, _ ->
                    val tag = WXConst.frindsTags.toList()[index].first

                    if (!selTags.contains(tag)) {
                        selTags.add(tag)
                        txtTag.text = "已选标签：${selTags.joinToString()}"
                    }
                }
            }
        })


        addView("按标签分享", View.OnClickListener {
            if (selTags.isEmpty()) {
                mActivity.showSnack("请先选择标签")
            } else {
                startWXTask(WXShareTask(tagForFriends = selTags, isSendTags = true, mediaCount = 1))
            }
        })

        addView("排除标签分享", View.OnClickListener {
            if (selTags.isEmpty()) {
                mActivity.showSnack("请先选择标签")
            } else {
                startWXTask(WXShareTask(tagForFriends = selTags, isSendTags = false, mediaCount = 0))
            }
        })

        addView("微信无障碍lib", View.OnClickListener {
            /*KGranting.requestPermissions(mActivity, 899, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), arrayOf("内存")) {
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
            }*/
        })
    }
}