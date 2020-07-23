package com.kiven.sample

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.util.randomPhoneImage
import com.kiven.sample.util.showListDialog
import org.jetbrains.anko.toast

/**
 * Created by oukobayashi on 2020/7/23.
 * https://blog.csdn.net/baihua2001cn/article/details/86678416
 */
class AHShare : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        var isLimit = false
        addBtn("未指定分享App", View.OnClickListener {
            isLimit = !isLimit
            val bb = it as Button
            bb.text = if (isLimit) "已指定分享App" else "未指定分享App"
        })

        val startShare = fun(shareIntent: Intent) {
            if (isLimit) {
                // 仅能设置一个App， 同一个App不同的组件，显示的名称比较合理
                /*shareIntent.setPackage("com.tencent.mm")
                val ci = Intent.createChooser(shareIntent, "弹出显示的标题x")
                activity.startActivity(ci)*/

                // todo 实用限制多个App。但是同一个App的分享组件，显示的名称一样，不合理
                //  原理：
                //  1 先找到对应App的所有分享组件
                //  2 通过分享组件中的一个生成选择弹窗intent。所以使用了：targetIntents.removeAt(0)
                //  3 把其他分享组件注册到选择弹窗intent

                // 先找到对应App的所有分享组件
                val ri = activity.packageManager.queryIntentActivities(shareIntent, 0)
                val targetIntents = arrayListOf<Intent>()
                ri.forEach {
                    val activityInfo = it.activityInfo
                    val packageName = activityInfo.packageName
                    KLog.i(packageName + " " + activityInfo.name)
                    if (packageName.contains("com.tencent.")) {
                        val intent = Intent(shareIntent)
                        intent.setPackage(activityInfo.packageName)
                        intent.setClassName(activityInfo.packageName, activityInfo.name)
                        targetIntents.add(intent)
                    }
                }
                // 通过分享组件中的一个生成选择弹窗intent
                val ci = Intent.createChooser(targetIntents.removeAt(0), "弹出显示的标题x").apply {
                    // 加入其他分享组件
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toTypedArray())
                }
                activity.startActivity(ci)
            } else {
                val ci = Intent.createChooser(shareIntent, "弹出显示的标题x")
                activity.startActivity(ci)
            }
        }

        addBtn("文字分享", View.OnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "标题c")
                putExtra(Intent.EXTRA_TEXT, "内容c")
            }
            startShare(shareIntent)
        })

        // 单图分享，可以分享到盆友圈
        addBtn("单图片分享", View.OnClickListener {
            activity.randomPhoneImage { uri ->
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startShare(shareIntent)
            }
        })
        // 多图分享，不可以分享到盆友圈
        addBtn("多图片分享1", View.OnClickListener {
            activity.randomPhoneImage { uri1 ->
                activity.randomPhoneImage { uri2 ->
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND_MULTIPLE
                        type = "image/*"
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(uri1, uri2))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startShare(shareIntent)
                }
            }
        })
        addBtn("直接指定App分享", View.OnClickListener {
            activity.showListDialog(listOf(
                    "微信",
                    "盆友圈",
                    "QQ",
                    "QQ分享到电脑",
                    "QQ文件互传",
                    "QQ收藏"
            )){i, _ ->
                activity.randomPhoneImage { uri ->
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        when(i) {
                            0 -> { setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI") }
                            1 -> { setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI") }
                            2 -> { setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity") }
                            // qq 分享到电脑
                            3 -> { setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.qfileJumpActivity") }
                            // qq 文件互传
                            4 -> { setClassName("com.tencent.mobileqq", "cooperation.qlink.QlinkShareJumpActivity") }
                            // qq 收藏
                            5 -> { setClassName("com.tencent.mobileqq", "cooperation.qqfav.widget.QfavJumpActivity") }
                        }
                    }
                    startShare(shareIntent)
                }
            }
        })
        addBtn("", View.OnClickListener { })
        addBtn("", View.OnClickListener { })
        addBtn("", View.OnClickListener { })
        addBtn("", View.OnClickListener { })
        addBtn("", View.OnClickListener { })
        addBtn("", View.OnClickListener { })
    }
}