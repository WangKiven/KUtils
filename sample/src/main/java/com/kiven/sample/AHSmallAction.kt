package com.kiven.sample

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Process
import android.provider.Settings
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.processes.AndroidProcesses
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KNetwork
import com.kiven.kutils.tools.KString
import com.kiven.sample.actions.AHThemeDemo
import com.kiven.sample.actions.BiometricDemo
import com.kiven.sample.anim.AHAnim
import com.kiven.sample.autoService.AHAutoService
import com.kiven.sample.charCode.AHUnicodeList
import com.kiven.sample.dock.ActivityDock
import com.kiven.sample.imui.ImActivity
import com.kiven.sample.jpushUI.AHImui
import com.kiven.sample.mimc.ChatMsg
import com.kiven.sample.mimc.UserManager
import com.kiven.sample.noti.AHNotiTest
import com.kiven.sample.service.LiveWallpaper2
import com.kiven.sample.spss.AHSpssTemple
import com.kiven.sample.systemdata.AHSysgemData
import com.kiven.sample.util.EncryptUtils
import com.kiven.sample.util.WallpaperUtil
import com.kiven.sample.util.callPhone
import com.kiven.sample.util.snackbar
import com.xiaomi.mimc.MIMCGroupMessage
import com.xiaomi.mimc.MIMCMessage
import com.xiaomi.mimc.MIMCServerAck
import com.xiaomi.mimc.common.MIMCConstant
import kotlinx.coroutines.*
import org.jetbrains.anko.coroutines.experimental.Ref
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.support.v4.nestedScrollView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by wangk on 2018/3/28.
 */
class AHSmallAction : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

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

        addTitle("壁纸锁屏桌面")
        addView("静态壁纸锁屏", View.OnClickListener {
            val wallPaperManager = WallpaperManager.getInstance(mActivity)

            // FLAG_LOCK 设置锁屏，FLAG_SYSTEM 设置壁纸
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallPaperManager.setResource(R.mipmap.fish, WallpaperManager.FLAG_LOCK)
                wallPaperManager.setResource(R.mipmap.fish, WallpaperManager.FLAG_SYSTEM)
                Snackbar.make(flexboxLayout, "设置锁屏", Snackbar.LENGTH_LONG).show()
            } else {
                // 7.0以下，似乎只能设置壁纸。7.0及之后，这个方法似乎同时设置壁纸和锁屏
                wallPaperManager.setResource(R.mipmap.fish)
                Snackbar.make(flexboxLayout, "设置壁纸和锁屏", Snackbar.LENGTH_LONG).show()
            }
        })

        // 没有系统权限，用不了
        addView("动态壁纸", View.OnClickListener {
            Snackbar.make(flexboxLayout, "没有系统权限，用不了。看来只能设置静态壁纸了。还是去写桌面应用吧！！", Snackbar.LENGTH_LONG).show()
            val intent = Intent(mActivity, LiveWallpaper2::class.java)
            mActivity.startService(intent)
//            WallpaperUtil.setLiveWallpaper(mActivity, 322)
        })
        addView("获取在用壁纸", View.OnClickListener {
            val bp = WallpaperUtil.getDefaultWallpaper(mActivity)
            if (bp == null) {
                showDialog("获取失败或者为动态壁纸")
            } else {
                val iv = ImageView(mActivity)
                iv.setImageBitmap(bp)
                AlertDialog.Builder(mActivity)
                        .setView(iv)
                        .show()
            }
        })
        addView("壁纸是本应用设置的吗", View.OnClickListener {
            val yes = WallpaperUtil.wallpaperIsUsed(mActivity)
            showDialog(if (yes) "是的" else "不是")
        })
        addView("壁纸是动态壁纸吗", View.OnClickListener {
            val yes = WallpaperUtil.isLivingWallpaper(mActivity)
            showDialog(if (yes) "是的" else "不是")
        })
        addView("自定义桌面", View.OnClickListener {
            mActivity.startActivity(Intent(mActivity, ActivityDock::class.java))
        })

        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("语音识别与合成")

        // http://doc.xfyun.cn/msc_android/%E9%A2%84%E5%A4%87%E5%B7%A5%E4%BD%9C.html
        addView("语音识别与合成", View.OnClickListener {
            // 在里面请求权限太麻烦，由于有多个地方都需要权限，所以在入口出先请求
            KGranting.requestPermissions(activity, 377, Manifest.permission.RECORD_AUDIO,
                    "录音") {
                if (it) {
                    AHXunfeiTest().startActivity(mActivity)
                }
            }
        })

        // TODO: 2018/6/4 ----------------------------------------------------------
        addTitle("kotlin 特性")
        addView("协程", View.OnClickListener {
            GlobalScope.launch {
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

            runBlocking {

            }

            val ct = Thread.currentThread()
            KLog.i("start -----------${ct.id}-------${ct.name}-----")
        })

        addView("anko Ref协程", View.OnClickListener {
            val ref: Ref<AHSmallAction> = this.asReference()

            // 进入协程
            /*async(UI) {
                delay(2000)

                // 启动ui线程
                ref().showDialog("anko Ref协程")
            }*/

            GlobalScope.launch(Dispatchers.Main) {
                delay(2000)

                // 启动ui线程
                ref().showDialog("anko Ref协程")
            }
        })

        addView("anko bg()协程", View.OnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val data = async {
                    Thread.sleep(2000)
                    "anko bg()协程"
                }

                // 启动ui线程
                showDialog(data.await())
            }
        })

        // todo
        addTitle("拨号与电话监听")
        addView("电话监听", View.OnClickListener {
            KGranting.requestPermissions(mActivity, 989, arrayOf(Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO), arrayOf("通话状态", "拨号", "录音")) {
                if (it) {
                    val telephonyManager = mActivity.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                    val lis = object : PhoneStateListener() {
                        var oldTime = 0L
                        var recorder: MediaRecorder? = null
                        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                            super.onCallStateChanged(state, phoneNumber)
                            val nowTime = System.currentTimeMillis()
                            val ss = when (state) {
                                TelephonyManager.CALL_STATE_IDLE -> "空闲状态"// 可能是挂断、拒绝接听或真的空闲
                                TelephonyManager.CALL_STATE_RINGING -> "响铃状态"
                                TelephonyManager.CALL_STATE_OFFHOOK -> "通话状态"
                                else -> "unknown"
                            }
                            Log.i("ULog_default", "$ss($phoneNumber):${nowTime - oldTime}")
                            oldTime = nowTime

                            if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                                if (recorder == null) {
                                    recorder = MediaRecorder().apply {
                                        try {
                                            setAudioSource(MediaRecorder.AudioSource.MIC) //RECORD_AUDIO权限
                                            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)//设置音频格式
                                            setOutputFile(mActivity.getDir(Environment.DIRECTORY_MUSIC, Context.MODE_PRIVATE).absolutePath
                                                    + "/${SimpleDateFormat("yyyyMMddHHmmss").format(Date())}.3gp")
                                            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)//设置音频编码
                                            prepare()
                                            start()
                                        } catch (e: Exception) {
                                            KLog.e(e)
                                        }

                                    }
                                }
                            } else {
                                recorder?.apply {
                                    stop()
                                    release()
                                }
                                recorder = null
                            }
                        }

                        override fun onDataConnectionStateChanged(state: Int, networkType: Int) {
                            super.onDataConnectionStateChanged(state, networkType)
                            Log.i("ULog_default", "$state($networkType)")
                        }
                    }

                    telephonyManager.listen(lis, PhoneStateListener.LISTEN_CALL_STATE)
                }
            }


        })
        addView("拨号", View.OnClickListener {
            KGranting.requestPermissions(mActivity, 101, Manifest.permission.CALL_PHONE, "拨号") { isSuccess ->
                val phoneno = "17132307428"
                if (isSuccess) {
                    mActivity.callPhone(phoneno)

                    // 与拨号并行，检测sim卡状态
                    val telephonyManager = mActivity.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                    val simState = telephonyManager.simState
                    if (simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN) {
                        mActivity.snackbar("未检测到sim卡或当前sim卡不可用，请另行拨号$phoneno")
                    }
                }
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
        addView("签名信息", View.OnClickListener { _ ->
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

            KAlertDialogHelper.Show1BDialog(mActivity, String(ss)) {
                KString.setClipText(mActivity, String(ss))
            }

        })
        addView("检测网络与IP", View.OnClickListener {
            val type = KNetwork.getNetworkType(mActivity)
            val ts = when (type) {
                0 -> {
                    "没有网络"
                }
                1 -> {
                    "WIFI"
                }
                2 -> {
                    "WAP"
                }
                3 -> {
                    "MNET"
                }
                else -> {
                    "检测失败"
                }
            }
            KAlertDialogHelper.Show1BDialog(mActivity, "网络类型：$ts\nIP:${KNetwork.getIPAddress()}")
        })
        addView("录音播放", View.OnClickListener { AHRecorderPlay().startActivity(mActivity) })
        addView("imui界面", View.OnClickListener { ImActivity().startActivity(mActivity) })
        addView("jpushUI", View.OnClickListener {

            UserManager.getInstance().setHandleMIMCMsgListener(object : UserManager.OnHandleMIMCMsgListener {
                override fun onHandleMessage(chatMsg: ChatMsg?) {
                }

                override fun onHandleGroupMessage(chatMsg: ChatMsg?) {
                }

                override fun onHandleStatusChanged(status: MIMCConstant.OnlineStatus?) {
                    mActivity.runOnUiThread {
                        if (status == MIMCConstant.OnlineStatus.ONLINE) {
                            val ahImui = AHImui()
                            ahImui.intent
                                    .putExtra("toAccount", "456")
                                    .putExtra("sessionType", 1)
                            ahImui.startActivity(mActivity)
                        }
                    }
                }

                override fun onHandleServerAck(serverAck: MIMCServerAck?) {
                }

                override fun onHandleCreateGroup(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleQueryGroupInfo(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleQueryGroupsOfAccount(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleJoinGroup(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleQuitGroup(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleKickGroup(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleUpdateGroup(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleDismissGroup(json: String?, isSuccess: Boolean) {
                }

                override fun onHandlePullP2PHistory(json: String?, isSuccess: Boolean) {
                }

                override fun onHandlePullP2THistory(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleSendMessageTimeout(message: MIMCMessage?) {
                }

                override fun onHandleSendGroupMessageTimeout(groupMessage: MIMCGroupMessage?) {
                }

                override fun onHandleJoinUnlimitedGroup(topicId: Long, code: Int, errMsg: String?) {
                }

                override fun onHandleQuitUnlimitedGroup(topicId: Long, code: Int, errMsg: String?) {
                }

                override fun onHandleDismissUnlimitedGroup(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleQueryUnlimitedGroupMembers(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleQueryUnlimitedGroups(json: String?, isSuccess: Boolean) {
                }

                override fun onHandleQueryUnlimitedGroupOnlineUsers(json: String?, isSuccess: Boolean) {
                }
            })

            // 登录小米通信
            val mdir = mActivity.getDir("mimc", Context.MODE_PRIVATE)
            if (!mdir.exists()) {
                mdir.mkdir()
            }
//            val user = MIMCUser.newInstance("123", mdir.absolutePath)
            UserManager.getInstance().newUser("123", mdir.absolutePath)?.login()

        })
        addView("发送/监听通知", View.OnClickListener { AHNotiTest().startActivity(activity) })
        addView("唯一标识", View.OnClickListener {
            val sb = StringBuilder()

            val uniqueID = UUID.randomUUID().toString()
            sb.append("UUID:$uniqueID, 每次获取都不一样\n")

            //实例化TelephonyManager对象
//            val telephonyManager = mActivity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            //获取IMEI号
//            val imei = telephonyManager.getDeviceId()


            // 厂商预装的系统应用可以拿到设备 ID。具体来说，应用必须获得 READ_PRIVILEGED_PHONE_STATE 权限，而这个权限只可能被赋予预装在系统分区的应用。

            // 不是厂商预装的系统应用，似乎拿不到唯一标识了

            val androidID = Settings.System.getString(mActivity.contentResolver, Settings.Secure.ANDROID_ID)
            sb.append("ANDROID_ID:$androidID\n")// 不同的设备可能会产生相同的ANDROID_ID

            sb.append("串号:${Build.SERIAL}\n")

            showDialog(sb.toString())
        })
        // https://www.jianshu.com/p/a90563606e1f
        // https://developer.android.google.cn/guide/slices/templates
        addView("Slice(提供App外搜索模块)", View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // 需要先安装SliceViewer：https://github.com/googlesamples/android-SliceViewer/releases
                // 一些设备可能禁止了这个功能
                mActivity.startActivity(Intent("android.intent.action.VIEW", Uri.parse("slice-content://com.kiven.sample/hello")))
            } else {
                mActivity.snackbar("该版本不支持")
            }
        })
        addView("KAlert和定时5秒后重启", View.OnClickListener {
            KAlertDialogHelper.Show2BDialog(mActivity, "这是一个KAlertDialogHelper。\n是否5秒后重启？？？") {
                /*ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                manager.killBackgroundProcesses(getPackageName());*/

                val intent = mActivity.packageManager
                        .getLaunchIntentForPackage(mActivity.packageName)
                val restartIntent = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_ONE_SHOT)
                val mgr = mActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
                mgr!!.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, restartIntent) // 5秒钟后重启应用

                Process.killProcess(Process.myPid())
            }
        })
        addView("指纹(生物)识别", View.OnClickListener {
            // 需要配置权限：
            // <!--指纹识别，低版本需要-->
            // <uses-permission android:name="android.permission.USE_FINGERPRINT"/>
            // <!-- 生物识别，高版本需要 -->
            // <uses-permission android:name="android.permission.USE_BIOMETRIC"/>
            BiometricDemo(mActivity).test()
        })
        addView("Theme", View.OnClickListener { AHThemeDemo().startActivity(mActivity) })
        addView("字符编码测试", View.OnClickListener { AHUnicodeList().startActivity(mActivity) })
        addView("无障碍", View.OnClickListener { AHAutoService().startActivity(mActivity) })
        addView("闹钟demo", View.OnClickListener { AHAlarmDemo().startActivity(mActivity) })
        addView("系统数据", View.OnClickListener { AHSysgemData().startActivity(mActivity) })
        addView("url访问测试", View.OnClickListener { AHUrlTest().startActivity(mActivity) })
        addView("socket", View.OnClickListener { AHSocketTest().startActivity(mActivity) })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
    }

    private suspend fun doSomthing(): Int {
        delay(1000)
        return suspendCoroutine {
            it.resume(6)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showDialog(word: String) {
        KAlertDialogHelper.Show1BDialog(mActivity, word)
        Log.i("ULog_default", word)
    }
}