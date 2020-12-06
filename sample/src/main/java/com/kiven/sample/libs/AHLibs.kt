package com.kiven.sample.libs

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.flyco.animation.FlipEnter.FlipRightEnter
import com.flyco.dialog.listener.OnBtnClickL
import com.flyco.dialog.widget.ActionSheetDialog
import com.flyco.dialog.widget.MaterialDialog
import com.flyco.dialog.widget.NormalDialog
import com.flyco.dialog.widget.NormalListDialog
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KToast
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.libs.chatkit.AHChatList
import com.kiven.sample.media.AHGif
import com.kiven.sample.util.Const
import com.kiven.sample.util.newDialog
import com.kiven.sample.util.snackbar
import com.kiven.sample.xutils.db.AHDbDemo
import com.kiven.sample.xutils.net.AHNetDemo
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.server.AsyncHttpServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.jetbrains.anko.dip
import org.jetbrains.anko.support.v4.nestedScrollView
import org.xutils.image.ImageOptions
import java.io.IOException
import java.io.InputStream
import java.net.Inet4Address
import java.net.NetworkInterface
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.DateFormat
import java.util.*
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

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
                val request = StringRequest(Request.Method.GET, http, { Log.i(KLog.getTag(), http + DateFormat.getTimeInstance().format(Date())) }, { Log.i(KLog.getTag(), http + DateFormat.getTimeInstance().format(Date())) })
                queue!!.add(request)
            }

            volley("https://github.com/google/volley")
            volley("http://blog.csdn.net/linmiansheng/article/details/21646753")
        }
        addBtn("OkHttp") { AHOkHttpLib().startActivity(activity) }
        addBtn("glide") {
            val iv = ImageView(activity).apply {
                layoutParams = ViewGroup.LayoutParams(activity.dip(50), activity.dip(50))

                var count = 0
                val showNext = fun() {
                    val urls = Const.IMAGES.subList(0, 2) + "/storage/emulated/0/DCIM/Camera/1557910396757.jpg"
                    Glide.with(activity).load(urls[count % urls.size]).circleCrop().into(this@apply)
                    count++
                }

                showNext()
                setOnClickListener { showNext() }
            }


            activity.newDialog(iv).show()
        }

        addTitle("弹窗")
        addBtn("FlycoDialog") { AHFlycoDialogLib().startActivity(activity) }


        // android emoji说明：https://www.jianshu.com/p/d82ac2edc7e8
        // emoji所有表情(官宣)：http://www.unicode.org/emoji/charts/full-emoji-list.html
        addTitle("emoji")
        // https://github.com/rockerhieu/emojicon
        addBtn("emojicon", View.OnClickListener { /*AHEmojiconLibs().startActivity(activity)*/DFEmojicon().show(activity.supportFragmentManager, null) })
        // https://github.com/rockerhieu/emojiconize
        addBtn("", View.OnClickListener { })
        // https://github.com/w446108264/XhsEmoticonsKeyboard
        addBtn("", View.OnClickListener { })
        // https://github.com/SiberiaDante/EmotionApp
        addBtn("", View.OnClickListener { })
        // https://github.com/vanniktech/Emoji
        addBtn("Emoji库及各库TextView的展示对比", View.OnClickListener { AHEmoji().startActivity(activity) })

        addTitle("其他")
        addBtn("ChatKit", View.OnClickListener { AHChatList().startActivity(activity) })
        addBtn("MPAndroidChart", View.OnClickListener { AHMPAndroidChart().startActivity(activity) })
        // 安卓平台下，图片或视频转化为ascii，图片转化成低多边形风格图形，emoji表情填充图片，合并视频用到ffmpeg库。后期会加入带色彩的ascii码图片或视频
        // https://github.com/LineCutFeng/PlayPicdio
        addBtn("PlayPicdio(图片或视频转化为ascii)", View.OnClickListener { })

        // https://github.com/burhanrashid52/PhotoEditor
        addBtn("PhotoEditor(图片编辑)", View.OnClickListener { })
        // https://github.com/alibaba/ARouter ，TODO 需要注意代码混淆和加固的问题, 我们的helper不适用，只能用于activity
        addBtn("ARouter", View.OnClickListener { ARouter.getInstance().build("/dock/home").navigation() })
        addBtn("GPUImage & PhotoView", View.OnClickListener { AHGPUImageLib().startActivity(activity) })
        addBtn("gif", View.OnClickListener { AHGif().startActivity(mActivity) })
        addBtn("spring for Android", View.OnClickListener { AHSpring().startActivity(mActivity) })
        addBtn("autofittextview") { AHTextViewDemo().startActivity(mActivity) }
        addBtn("") {  }
        addBtn("") {  }
        addBtn("") {  }
        addBtn("") {  }
        addBtn("") {  }
    }

}