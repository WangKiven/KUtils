package com.kiven.sample

import android.Manifest
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.iflytek.cloud.*
import com.jaredrummler.android.processes.AndroidProcesses
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KString
import com.kiven.sample.anim.AHAnim
import com.kiven.sample.net.AHNetDemo
import com.kiven.sample.service.LiveWallpaper2
import com.kiven.sample.spss.AHSpssTemple
import com.kiven.sample.util.EncryptUtils
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.coroutines.experimental.Ref
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.coroutines.experimental.bg
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Created by wangk on 2018/3/28.
 */
class AHSmallAction : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(flexboxLayout)

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }
        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("检测与杀死app")

        addView("再运行的进程，系统方法", View.OnClickListener {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            // 通过调用ActivityManager的getRunningAppServicees()方法获得系统里所有正在运行的进程
            val serviceList = am.runningAppProcesses
            serviceList.forEach {
                KLog.i(it.processName)
            }
        })

        addView("再运行的进程，AndroidProcesses", View.OnClickListener {
            val process = AndroidProcesses.getRunningAppProcesses()
            process.forEach {
                KLog.i("name = ${it.name}, pkgName = ${it.packageName}")
            }
        })
        addView("关闭省心宝", View.OnClickListener {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses("com.jeeinc.save.worry")

            // 需要系统权限（Manifest.permission.FORCE_STOP_PACKAGES），无法获取。
            /*val method = am::class.java.getMethod("forceStopPackage", String::class.java)
            method.invoke(am, "com.jeeinc.save.worry")*/
        })
        // TODO: 2018/3/31 ----------------------------------------------------------
        // Android锁屏实现与总结: https://www.jianshu.com/p/6c3a6b0f145e

        addTitle("壁纸锁屏")
        addView("静态壁纸锁屏", View.OnClickListener {
            val wallPaperManager = WallpaperManager.getInstance(mActivity)

            // FLAG_LOCK 设置锁屏，FLAG_SYSTEM 设置壁纸
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallPaperManager.setResource(R.mipmap.fish, WallpaperManager.FLAG_LOCK)
                Snackbar.make(flexboxLayout, "设置锁屏", Snackbar.LENGTH_LONG).show()
            } else {
                // 7.0以下，似乎只能设置壁纸。7.0及之后，这个方法似乎同时设置壁纸和锁屏
                wallPaperManager.setResource(R.mipmap.fish)
                Snackbar.make(flexboxLayout, "设置壁纸和锁屏", Snackbar.LENGTH_LONG).show()
            }
        })

        // 没有系统权限，用不了
        addView("动态壁纸", View.OnClickListener {
            Snackbar.make(flexboxLayout, "没有系统权限，用不了", Snackbar.LENGTH_LONG).show()
            val intent = Intent(mActivity, LiveWallpaper2::class.java)
            mActivity.startService(intent)
//            WallpaperUtil.setLiveWallpaper(mActivity, 322)
        })

        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("语音识别")

        // http://doc.xfyun.cn/msc_android/%E9%A2%84%E5%A4%87%E5%B7%A5%E4%BD%9C.html
        val mAsr = getXunfei()
        addView("讯飞识别", View.OnClickListener {
            KGranting.requestPermissions(activity, 377, Manifest.permission.RECORD_AUDIO,
                    "录音", KGranting.GrantingCallBack {
                if (it) {
                    val ret = mAsr.startListening(mRecognizerListener)
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("听写失败,错误码：$ret")
                    }
                }
            })
        })

        // TODO: 2018/6/4 ----------------------------------------------------------
        addTitle("kotlin 特性")
        addView("协程", View.OnClickListener {
            launch(CommonPool) {
                delay(1000)
                val data = doSomthing()
                KLog.i("data = $data")

                val dea = suspendCoroutine<Int> {
                    Thread {
                        Thread.sleep(1000)
                        val cuth = Thread.currentThread()
                        KLog.i("Threadid = ${cuth.id}, Threadname = ${cuth.name}")
                        it.resume(7)
                    }.start()
                }
                KLog.i("dea = $dea")
                val cuth = Thread.currentThread()
                KLog.i("id = ${cuth.id}, name = ${cuth.name}")
            }

            val ct = Thread.currentThread()
            KLog.i("start -----------${ct.id}-------${ct.name}-----")
        })

        addView("anko Ref协程", View.OnClickListener {
            val ref: Ref<AHSmallAction> = this.asReference()

            // 进入协程
            async(UI) {
                delay(2000)

                // 启动ui线程
                ref().showDialog("anko Ref协程")
            }
        })

        addView("anko bg()协程", View.OnClickListener {
            // 进入协程
            async(UI) {
                val data: Deferred<String> = bg {
                    Thread.sleep(2000)
                    "anko bg()协程"
                }

                // 启动ui线程
                showDialog(data.await())
            }
        })

        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("其他")

        // https://developer.android.google.cn/guide/topics/graphics/spring-animation.html
        addView("动画", View.OnClickListener {
            AHAnim().startActivity(mActivity)
        })
        addView("统计分析", View.OnClickListener { AHSpssTemple().startActivity(mActivity) })
        addView("文件管理方案", View.OnClickListener { AHFileTemple().startActivity(mActivity) })
        addView("二维码", View.OnClickListener { AHQrCode().startActivity(mActivity) })
        // 微信要的签名信息是：将MD5中的字母消息后的字符串
        addView("签名信息", View.OnClickListener {_ ->
            val flag = if (Build.VERSION.SDK_INT >= 28) {
                PackageManager.GET_SIGNING_CERTIFICATES
            } else {
                PackageManager.GET_SIGNATURES
            }

            val info = mActivity.packageManager.getPackageInfo(mActivity.packageName, flag)

            val sign = if (Build.VERSION.SDK_INT >= 28) {
                info.signingInfo.apkContentsSigners
            } else
                info.signatures

            val ss = StringBuffer()
            for (si in sign) {
                val bytes = si.toByteArray()

                val md5 = EncryptUtils.encryptMD5ToString(bytes)
                val sha1 = EncryptUtils.encryptSHA1ToString(bytes)
                val sha256 = EncryptUtils.encryptSHA256ToString(bytes)
                ss.append("sign : \nmd5 = $md5 \nsha1 = $sha1 \nsha256 = $sha256")
            }

            KAlertDialogHelper.Show1BDialog(mActivity, String(ss)){
                KString.setClipText(mActivity, String(ss))
            }

        })
        addView("Net FrameWork", View.OnClickListener { AHNetDemo().startActivity(mActivity)})
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })

    }

    private suspend fun doSomthing(): Int {
        /*return async(CommonPool){
            val data = 6
            data
        }.await()*/
        return suspendCoroutine {
            it.resume(6)
        }
    }

    /**
     * 初始讯飞
     * http://doc.xfyun.cn/msc_android/%E9%A2%84%E5%A4%87%E5%B7%A5%E4%BD%9C.html
     */
    private fun getXunfei(): SpeechRecognizer {
        SpeechUtility.createUtility(mActivity, SpeechConstant.APPID + "=5a15147f")

        val mAsr = SpeechRecognizer.createRecognizer(mActivity) { code ->
            KLog.i("SpeechRecognizer init() code = $code")
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：$code")
            }

        }
        // 设置引擎类型
        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, null)
        mAsr.setParameter(SpeechConstant.SUBJECT, null)

        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8")

        /*val dia = RecognizerDialog(mActivity, { code ->
            KLog.i("SpeechRecognizer init() code = $code")
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：$code")
            }
        })
        dia.show()*/

        return mAsr
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showTip(word: String) {
//        KAlertDialogHelper.Show1BDialog(mActivity, word)
        KLog.d(word)
    }

    private fun showDialog(word: String) {
        KAlertDialogHelper.Show1BDialog(mActivity, word)
    }

    val mTranslateEnable = false
    private val mRecognizerListener = object : RecognizerListener {

        override fun onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话")
        }

        override fun onError(error: SpeechError) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (mTranslateEnable && error.errorCode == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能")
            } else {
                showTip(error.getPlainDescription(true))
            }
        }

        override fun onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话")
        }

        override fun onResult(results: RecognizerResult, isLast: Boolean) {
            KLog.i(results.resultString)
            if (mTranslateEnable) {
                printTransResult(results)
            } else {
                printResult(results)
            }

            if (isLast) {
                // TODO 最后的结果
            }
        }

        override fun onVolumeChanged(volume: Int, data: ByteArray) {
            KLog.i("当前正在说话，音量大小：$volume")
            KLog.i("返回音频数据：" + data.size)
        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    }


    private fun printTransResult(results: RecognizerResult) {
        KLog.d("结果t：${results.resultString}")
    }

    private fun printResult(results: RecognizerResult) {
        KLog.d("结果：${results.resultString}")
    }
}