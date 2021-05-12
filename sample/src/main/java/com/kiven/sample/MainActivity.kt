package com.kiven.sample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.transition.Slide
import android.view.Gravity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import com.kiven.kutils.activityHelper.KFragmentActivity
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.*
import com.kiven.sample.arch.AHArch
import com.kiven.sample.arcore.AHARCoreInlet
import com.kiven.sample.floatView.ActivityHFloatView
import com.kiven.sample.font.AHFont
import com.kiven.sample.gl.AHGL
import com.kiven.sample.helperTest.AHHelperTest
import com.kiven.sample.libs.AHLibs
import com.kiven.sample.media.AHMediaList
import com.kiven.sample.push.AHSxbPush
import com.kiven.sample.theme.AHTheme
import com.kiven.sample.util.addBtn
import com.kiven.sample.util.addTitle
import com.kiven.sample.util.showDialog
import com.kiven.sample.vpn.AHMyVpn
import kotlinx.android.synthetic.main.main_activity.*

/**
 * Created by wangk on 2020/12/2.
 */
class MainActivity : KActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setupWindowAnimations()

        loadUI()
    }

    private fun loadUI() {
        flexbox.apply {
            addTitle("系统 ${KNetwork.getIPAddress() ?: ""}")
            addBtn("原生UI控件") { AHNativeWidget().startActivity(this@MainActivity) }
            addBtn("liveData") {
                val data = MutableLiveData<Int>()
                data.observe(this@MainActivity, {
                    KLog.i("it = $it")
                    KAlertDialogHelper.Show1BDialog(this@MainActivity, "LiveData 行不行？")
                })
                Thread {
                    Thread.sleep(3000)
                    data.postValue(1)
                }.start()
            }
            addBtn("打开设置") { AHOpenSetting().startActivity(context) }
            addBtn("悬浮框") { ActivityHFloatView().startActivity(this@MainActivity) }
            addBtn("媒体文件处理") { AHMediaList().startActivity(this@MainActivity) }
            addBtn("Theme和Style") { AHTheme().startActivity(this@MainActivity) }
            addBtn("Data Binding") {
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, Pair(it, "text_transition_name"))
                ActivityCompat.startActivity(this@MainActivity, Intent(this@MainActivity, ActivityDataBinding::class.java), optionsCompat.toBundle())
            }.apply {
                transitionName = "text_transition_name"
            }
            addBtn("opengl") { AHGL().startActivity(this@MainActivity) }
            addBtn("arch") { AHArch().startActivity(this@MainActivity) }
            addBtn("服务自启动与保活") { AHAutoStartAndLiving().startActivity(this@MainActivity) }
            addBtn("VPN") { AHMyVpn().startActivity(this@MainActivity) }
            addBtn("打印各文件路径") {
                KGranting.requestPermissions(this@MainActivity, 345, Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间") { isSuccess ->
                    if (isSuccess) {
                        KLog.i("" + KFile.createFile("tmp", ".img", getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE))!!.absolutePath)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            KLog.i("" + KFile.createFile("tmp", ".img", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!)!!.absolutePath)
                            KLog.i("" + KFile.createFile("tmp", ".img", getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)!!.absolutePath)
                        }
                        KLog.i("" + KFile.createFile("tmp", ".img", getDatabasePath("db"))!!.absolutePath)
                        KLog.i("" + KFile.createFile("tmp", ".img", cacheDir)!!.absolutePath)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            KLog.i("" + KFile.createFile("tmp", ".img", dataDir)!!.absolutePath)
                        }


                        KLog.i("codeCacheDir = " + codeCacheDir.absolutePath)
                        KLog.i("obbDir = " + obbDir.absolutePath)
                        KLog.i("--------------------- obbDirs -----------------------")
                        obbDirs?.forEach {
                            KLog.i(it.absolutePath)
                        }
                        KLog.i("--------------------- obbDirs -----------------------")
                        KLog.i("--------------------- fileList -----------------------")
                        fileList()?.forEach {
                            KLog.i(it)
                        }
                        KLog.i("--------------------- fileList -----------------------")
                    }
                }
            }



            addTitle("三方库")
            addBtn("三方库") { AHLibs().startActivity(this@MainActivity) }



            addTitle("KUtils功能")
            addBtn("测试KActivityHelper") { AHHelperTest().startActivity(this@MainActivity) }
            addBtn("自定义的几个控件展示") {
                AHKUtilsWidgetDemo().startActivity(this@MainActivity)
            }
            addBtn("KGranting") {
                KGranting.requestAlbumPermissions(this@MainActivity, 233) {
                    if (it) {
                        showDialog("获取到了相册权限")
                    } else {
                        showDialog("获取相册权限失败")
                    }
                }
            }
            addBtn("KView.runUI") {
                val handler = Handler {
                    KView.runUI(this@MainActivity, { KAlertDialogHelper.Show1BDialog(this@MainActivity, "LiveData 行不行？") })
                    true
                }
                handler.sendEmptyMessageDelayed(0, 3000)
            }
            addBtn("KFragmentActivity") {
                val fproxyIntent = Intent(this@MainActivity, KFragmentActivity::class.java)
                fproxyIntent.putExtra("fragment_name", FragmentApple::class.java.name)
                startActivity(fproxyIntent)
            }
            addBtn("DebugView") {
                AHHelperTest().startActivity(this@MainActivity)
            }

            addTitle("其他")
            addBtn("小功能") { AHSmallAction().startActivity(this@MainActivity) }
            addBtn("遍历字体") { AHFont().startActivity(this@MainActivity) }
            addBtn("ARCore") { AHARCoreInlet().startActivity(this@MainActivity) }
            addBtn("三方平台推送") { AHSxbPush().startActivity(this@MainActivity) }
            addBtn("cpu、内存管理") { AHCPUMemory().startActivity(this@MainActivity) }
        }
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
}