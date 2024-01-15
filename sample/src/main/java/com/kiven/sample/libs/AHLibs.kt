package com.kiven.sample.libs

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
//import com.alibaba.android.arouter.launcher.ARouter
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KPath
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.libs.chatkit.AHChatList
import com.kiven.sample.media.AHGif
import com.kiven.sample.util.*
import com.zxy.tiny.Tiny
import id.zelory.compressor.Compressor
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.DateFormat
import java.util.*

/**
 * Created by wangk on 2018/3/27.
 */
class AHLibs : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        addTitle("网络与图片加载")
        addBtn("AndroidAsync") { AHAndroidAsyncLib().startActivity(activity) }
        addBtn("xUtil") { AHXUtilLib().startActivity(activity) }
        // https://developer.android.google.cn/training/volley/simple.html
        // https://github.com/google/volley
        addBtn("volley") {
            var queue: RequestQueue? = null
            val volley = fun(http: String) {
                if (queue == null) {
                    queue = Volley.newRequestQueue(mActivity)
                }
                val request = StringRequest(
                    Request.Method.GET,
                    http,
                    { Log.i(KLog.getTag(), http + DateFormat.getTimeInstance().format(Date())) },
                    { Log.i(KLog.getTag(), http + DateFormat.getTimeInstance().format(Date())) })
                queue!!.add(request)
            }

            volley("https://github.com/google/volley")
            volley("http://blog.csdn.net/linmiansheng/article/details/21646753")
        }
        addBtn("OkHttp") { AHOkHttpLib().startActivity(activity) }
        addBtn("glide") {
            val iv = ImageView(activity).apply {
                layoutParams = ViewGroup.LayoutParams(KUtil.dip2px(50f), KUtil.dip2px(50f))

                var count = 0
                val showNext = fun() {
                    val urls = Const.IMAGES.subList(
                        0,
                        2
                    ) + "/storage/emulated/0/DCIM/Camera/1557910396757.jpg"
                    Glide.with(activity).load(urls[count % urls.size]).circleCrop().into(this@apply)
                    count++
                }

                showNext()
                setOnClickListener { showNext() }
            }


            activity.newDialog(iv).show()
        }


        // android emoji说明：https://www.jianshu.com/p/d82ac2edc7e8
        // emoji所有表情(官宣)：http://www.unicode.org/emoji/charts/full-emoji-list.html
        addTitle("emoji")
        // https://github.com/vanniktech/Emoji
        addBtn("Emoji库及各库TextView的展示对比", View.OnClickListener { AHEmoji().startActivity(activity) })

        addTitle("其他")
        addBtn("ChatKit", View.OnClickListener { AHChatList().startActivity(activity) })
        addBtn(
            "MPAndroidChart",
            View.OnClickListener { AHMPAndroidChart().startActivity(activity) })
        // 安卓平台下，图片或视频转化为ascii，图片转化成低多边形风格图形，emoji表情填充图片，合并视频用到ffmpeg库。后期会加入带色彩的ascii码图片或视频
        // https://github.com/LineCutFeng/PlayPicdio
        addBtn("PlayPicdio(图片或视频转化为ascii)", View.OnClickListener { })

        // https://github.com/burhanrashid52/PhotoEditor
        addBtn("PhotoEditor(图片编辑)", View.OnClickListener { })
        // https://github.com/alibaba/ARouter ，TODO 需要注意代码混淆和加固的问题, 我们的helper不适用，只能用于activity
//        addBtn(
//            "ARouter",
//            View.OnClickListener { ARouter.getInstance().build("/dock/home").navigation() })
        addBtn(
            "GPUImage & PhotoView",
            View.OnClickListener { AHGPUImageLib().startActivity(activity) })
        addBtn("gif", View.OnClickListener { AHGif().startActivity(mActivity) })
//        addBtn("spring for Android", View.OnClickListener { AHSpring().startActivity(mActivity) })
        addBtn("autofittextview") { AHTextViewDemo().startActivity(mActivity) }
        addBtn("Compressor") {
            activity.pickPhoneImage {
                runBlocking {
                    val result = Compressor.compress(activity, File(KPath.getPath(it)))

                    activity.showImageDialog(result.absolutePath)
                }
            }
        }
        addBtn("tiny") {
            activity.pickPhoneImage {
                runBlocking {
                    val result = Tiny.getInstance().source(it).asFile()
                        .withOptions(Tiny.FileCompressOptions()).compressSync()

                    if (result.success) {
                        activity.showImageDialog(result.outfile)
                        KLog.i(result.outfile)
                    }
                }
            }
        }
        addBtn("") { }
        addBtn("") { }
        addBtn("") { }
    }

}