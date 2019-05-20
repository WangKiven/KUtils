package com.kiven.sample

import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.kiven.sample.anim.AHAnim
import com.kiven.sample.imui.ImActivity
import com.kiven.sample.jpushUI.AHImui
import com.kiven.sample.mimc.ChatMsg
import com.kiven.sample.mimc.UserManager
import com.kiven.sample.noti.AHNotiTest
import com.kiven.sample.service.LiveWallpaper2
import com.kiven.sample.spss.AHSpssTemple
import com.kiven.sample.util.EncryptUtils
import com.kiven.sample.xutils.db.AHDbDemo
import com.kiven.sample.xutils.net.AHNetDemo
import com.xiaomi.mimc.MIMCGroupMessage
import com.xiaomi.mimc.MIMCMessage
import com.xiaomi.mimc.MIMCServerAck
import com.xiaomi.mimc.common.MIMCConstant
import kotlinx.coroutines.*
import org.jetbrains.anko.coroutines.experimental.Ref
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.support.v4.nestedScrollView
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
        addView("讯飞识别", View.OnClickListener {
            AHXunfeiTest().startActivity(mActivity)
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
        addTitle("xUtil")
        addView("Net FrameWork", View.OnClickListener { AHNetDemo().startActivity(mActivity) })
        addView("数据库", View.OnClickListener { AHDbDemo().startActivity(mActivity) })

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
        addView("检测网络", View.OnClickListener {
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
            KAlertDialogHelper.Show1BDialog(mActivity, "网络类型：$ts")
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
        addView("Dialog", View.OnClickListener { AHDialogTest().startActivity(activity) })
        addView("ConstraintLayout Test", View.OnClickListener {
            AHConstraintLayoutTest().startActivity(activity)
        })
        addView("通知", View.OnClickListener { AHNotiTest().startActivity(activity) })
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
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
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

    private fun showTip(word: String) {
//        KAlertDialogHelper.Show1BDialog(mActivity, word)
        KLog.d(word)
    }

    private fun showDialog(word: String) {
        KAlertDialogHelper.Show1BDialog(mActivity, word)
        Log.i("ULog_default", word)
    }
}