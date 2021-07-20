package com.kiven.sample.theme

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import com.kiven.sample.util.Const

/**
 * Created by oukobayashi on 2019-09-17.
 *
 * material设计规则官网：https://material.io/
 * 不期望界面开启时跟换日夜模式，在manifest中对应activity设置android:configChanges="uiMode"：https://developer.android.com/guide/topics/ui/look-and-feel/darktheme#%E9%85%8D%E7%BD%AE%E5%8F%98%E6%9B%B4
 *
 * Theme 属性详解: https://www.jianshu.com/p/06a3bbb7ce79
 *
 * 日夜主题切换(google推荐): https://www.jianshu.com/p/27f1aad049f7
 *
 * 助你快速搭配 Material Design 配色方案的10款Web工具: https://blog.csdn.net/dsc114/article/details/52120080
 */
class AHThemeDemo : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        initUI()
    }

    var nightMode: Int
        set(value) {
            KUtil.putSharedPreferencesIntValue("AHThemeDemo.nightMode", value)
        }
        get() {
            return KUtil.getSharedPreferencesIntValue("AHThemeDemo.nightMode", AppCompatDelegate.getDefaultNightMode())
        }
    private var themeId: Int
        set(@StyleRes value) {
            KUtil.putSharedPreferencesIntValue("AHThemeDemo.themeId", value)
        }
        get() {
            return KUtil.getSharedPreferencesIntValue("AHThemeDemo.themeId", R.style.Theme_MaterialComponents_DayNight)
        }

    private fun initUI() {
        mActivity.apply {
            if (nightMode != delegate.localNightMode) {
                delegate.localNightMode = nightMode // 设置本界面的夜间模式
            }
            try {
                setTheme(themeId)
            } catch (e: Throwable) {
                KLog.e(e)
            }

            setContentView(R.layout.ah_theme_demo)

            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setHomeButtonEnabled(true)
                title = "主题展示设置"
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.btn_night -> {
                selectNightMode {
                    nightMode = it
                    mActivity.delegate.localNightMode = it // 设置本界面的夜间模式
                }
            }
            R.id.btn_theme -> {
                AlertDialog.Builder(mActivity).setItems(arrayOf("全局主题", "自定义主题 双style", "自定义主题 双color", "Material 主题")) { _, position ->
                    themeId = when(position){
                        0 -> R.style.AppTheme
                        1 -> R.style.ThemeDemo
                        2 -> R.style.ThemeDemo2
                        else -> R.style.ThemeDemo3
                    }
                    mActivity.recreate()
                }.show()
            }
            R.id.btn_alert -> {
                val builder = MaterialAlertDialogBuilder(mActivity)
                builder.setMessage("这是 AlertDialog 的样式。")
                builder.setPositiveButton("操作1") { _, _ ->
                }
                builder.setNegativeButton("操作2") { _, _ ->
                }
                builder.setNeutralButton("操作3") { _, _ ->

                }
                builder.create().show()
            }
        }
    }

    private fun selectNightMode(callback: (Int) -> Unit) {
        AlertDialog.Builder(mActivity).setItems(arrayOf("跟随系统", "根据节电模式", "白天", "黑夜", "不指定")) { _, i ->
            val nm = when (i) {
                1 -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY// 节能模式时是黑暗主题，否则是白亮主题。
                2 -> AppCompatDelegate.MODE_NIGHT_NO
                3 -> AppCompatDelegate.MODE_NIGHT_YES
                4 -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED // 设置为全局为不指定，应该会使用系统的夜间模式。
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            callback(nm)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add(0, Menu.FIRST + 1, 0, "全局夜间").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> finish()
            Menu.FIRST + 1 -> {
                selectNightMode { nm ->
                    Const.nightMode = nm
                    AppCompatDelegate.setDefaultNightMode(nm) // 设置全局的夜间模式
                }
            }
            Menu.FIRST + 2 -> {
            }
            Menu.FIRST + 3 -> {
            }
            Menu.FIRST + 4 -> {
            }
        }
        return true
    }
}