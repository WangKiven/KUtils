package com.kiven.sample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.kiven.kutils.activityHelper.KFragmentActivity
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.kutils.callBack.CallBack
import com.kiven.kutils.file.KFile
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.*
import com.kiven.sample.arch.AHArch
import com.kiven.sample.arcore.AHARCoreInlet
import com.kiven.sample.floatView.ActivityHFloatView
import com.kiven.sample.gl.AHGL
import com.kiven.sample.libs.AHLibs
import com.kiven.sample.media.AHMediaList
import com.kiven.sample.push.AHSxbPush
import com.kiven.sample.theme.AHTheme
import com.kiven.sample.util.showDialog
import com.kiven.sample.util.showListDialog
import kotlinx.android.synthetic.main.activity_lauch.*
import me.grantland.widget.AutofitHelper

class LauchActivity : KActivity(), LifecycleOwner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lauch)

        KLog.printDeviceInfo()

        setupWindowAnimations()

        AutofitHelper.create(et_auto)


        iv_test.setOnClickListener(object : View.OnClickListener {
            var count = 0

            override fun onClick(v: View) {
                val urls = arrayOf("http://b.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513db777cf78376d55fbb3fbd9b3.jpg", "http://file5.gucn.com/file2/ShopLogoFile/20120413/Gucn_20120413327888131819Logo.jpg", "/storage/emulated/0/DCIM/Camera/1557910396757.jpg")
                /*ImageOptions options = new ImageOptions.Builder()
                        .setCircular(true)
                        .setAutoRotate(true).setFadeIn(true).build();
                x.image().bind(iv_test, urls[count%urls.length], options);*/


                Glide.with(this@LauchActivity).load(urls[count % urls.size]).circleCrop().into(iv_test)
                count++
            }
        })
        et_auto.setText(KNetwork.getIPAddress() ?: "")
        textView2.text = KString.fromHtml(getString(R.string.text_test, 5, 9))


        val addTitle = fun(text: String) {
            val tv = TextView(this)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flex.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(this)
            btn.text = text
            btn.setOnClickListener(click)
            flex.addView(btn)
        }

        addView("小功能", View.OnClickListener { AHSmallAction().startActivity(this) })
        addView("悬浮框", View.OnClickListener { ActivityHFloatView().startActivity(this) })
        addView("opengl", View.OnClickListener { AHGL().startActivity(this) })
        addView("cpu、内存管理", View.OnClickListener { AHCPUMemory().startActivity(this) })
        addView("测试KActivityHelper", View.OnClickListener { ActivityHTestBase().startActivity(this) })
        addView("三方库", View.OnClickListener { AHLibs().startActivity(this) })
        addView("liveData控制界面展示", View.OnClickListener {
            val handler = Handler(Handler.Callback {
                KView.runUI(this@LauchActivity, CallBack { KAlertDialogHelper.Show1BDialog(this@LauchActivity, "LiveData 行不行？") })
                true
            })
            handler.sendEmptyMessageDelayed(0, 5000)
        })
        addView("fragment代理activity", View.OnClickListener {
            val fproxyIntent = Intent(this, KFragmentActivity::class.java)
            fproxyIntent.putExtra("fragment_name", FragmentApple::class.java.name)
            startActivity(fproxyIntent)
        })
        addView("打开设置", View.OnClickListener {
            //            startActivity(Intent(Settings.ACTION_SETTINGS))
            val fields = Settings::class.java.fields
            val flist = mutableMapOf<String, String>()
            fields.forEach {
                try {
                    if (it.name.startsWith("ACTION_")) {
                        val value = it.get(Settings::class.java)
                        if (value != null && value is String) {
                            flist[it.name] = value
                        }
                    }
                } catch (e: Exception) {

                }
            }
            showListDialog(flist.keys.toList()) { _, key ->
                startActivity(Intent(flist[key]))
            }
        })
        addView("打开应用设置", View.OnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", packageName, null)
            startActivity(intent)
        })
        addView("打开WiFi设置", View.OnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        })
        addView("媒体文件处理", View.OnClickListener { AHMediaList().startActivity(this) })
        addView("Theme和Style", View.OnClickListener { AHTheme().startActivity(this) })
        addView("Data Binding", View.OnClickListener {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair(it, "text_transition_name"))
            ActivityCompat.startActivity(this, Intent(this, ActivityDataBinding::class.java), optionsCompat.toBundle())
        })
        addView("arch", View.OnClickListener { AHArch().startActivity(this) })
        addView("KGranting", View.OnClickListener {

            KGranting.requestAlbumPermissions(this, 233) {
                if (it) {
                    showDialog("获取到了相册权限")
                } else {
                    showDialog("获取相册权限失败")
                }
            }
        })
        addView("服务自启动与保活", View.OnClickListener { AHAutoStartAndLiving().startActivity(this) })
        addView("三方平台推送", View.OnClickListener { AHSxbPush().startActivity(this) })
        addView("ARCore", View.OnClickListener { AHARCoreInlet().startActivity(this) })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })

        /*startActivity(Intent(this, ClickNotiActivity::class.java))
        finish()*/

        /*AHSxbPush().startActivity(this)
        finish()*/
    }

    private fun setupWindowAnimations() {
        // Re-enter transition is executed when returning to this activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val slideTransition = Slide()
            slideTransition.slideEdge = Gravity.LEFT
            slideTransition.duration = 500
            window.reenterTransition = slideTransition
            window.exitTransition = slideTransition
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.item_path -> KGranting.requestPermissions(this, 345, Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间") { isSuccess ->
                if (isSuccess) {
                    KLog.i("" + KFile.createFile("tmp", ".img", getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE))!!.absolutePath)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        KLog.i("" + KFile.createFile("tmp", ".img", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!)!!.absolutePath)
                    }
                    KLog.i("" + KFile.createFile("tmp", ".img", getDatabasePath("db"))!!.absolutePath)
                    KLog.i("" + KFile.createFile("tmp", ".img", cacheDir)!!.absolutePath)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        KLog.i("" + KFile.createFile("tmp", ".img", dataDir)!!.absolutePath)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
