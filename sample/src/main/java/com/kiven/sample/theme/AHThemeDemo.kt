package com.kiven.sample.theme

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.util.Linkify
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.AHFileManager
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.logHelper.KShowLog
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import com.kiven.sample.util.Const
import com.kiven.sample.util.showListDialog
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * Created by oukobayashi on 2019-09-17.
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
//                AppCompatDelegate.setDefaultNightMode(nightMode) // 设置全局的夜间模式
            }
            try {
                setTheme(themeId)
            } catch (e: Throwable) {
                KLog.e(e)
            }

            nestedScrollView().linearLayout {
                gravity = Gravity.CENTER
                orientation = LinearLayout.VERTICAL

                textView {
                    text = "设置夜间主题模式"
                }

                radioGroup {
                    radioButton {
                        id = R.id.rb_0
                        text = "跟随系统"
                    }
                    radioButton {
                        id = R.id.rb_1
                        text = "根据节电模式"
                    }
                    radioButton {
                        id = R.id.rb_2
                        text = "白天"
                    }
                    radioButton {
                        id = R.id.rb_3
                        text = "黑夜"
                    }
                    radioButton {
                        id = R.id.rb_4
                        text = "不指定（应该是会使用全局夜间）"// 如果全局夜间也是MODE_NIGHT_UNSPECIFIED，就会使用系统的夜间模式
                    }

                    check(when (nightMode) {
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> R.id.rb_1
                        AppCompatDelegate.MODE_NIGHT_NO -> R.id.rb_2
                        AppCompatDelegate.MODE_NIGHT_YES -> R.id.rb_3
                        AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> R.id.rb_4
                        else -> R.id.rb_0
                    })

                    setOnCheckedChangeListener { _, rb_id ->
                        nightMode = when (rb_id) {
                            R.id.rb_1 -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                            R.id.rb_2 -> AppCompatDelegate.MODE_NIGHT_NO
                            R.id.rb_3 -> AppCompatDelegate.MODE_NIGHT_YES
                            R.id.rb_4 -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
                            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        }

                        delegate.localNightMode = nightMode // 设置本界面的夜间模式
//                        AppCompatDelegate.setDefaultNightMode(nightMode) // 设置全局的夜间模式
                    }
                }

                button {
                    text = "选择主题"
                    setOnClickListener {
                        AlertDialog.Builder(mActivity).setItems(arrayOf("系统主题", "自定义主题 双style", "自定义主题 双color")) { _, position ->
                            themeId = when(position){
                                0 -> R.style.Theme_MaterialComponents_DayNight
                                1 -> R.style.ThemeDemo
                                else -> R.style.ThemeDemo2
                            }
                            recreate()
                        }.show()
                    }
                }

                button {
                    text = "AlertDialog"
                    setOnClickListener {
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

                textView {
                    text = when (themeId) {
                        R.style.Theme_MaterialComponents_DayNight -> "当前主题是 系统主题"
                        R.style.ThemeDemo -> "当前主题是 自定义主题 双style"
                        else -> "当前主题是 自定义主题 双color"
                    }
                }

                textView {
                    autoLinkMask = Linkify.WEB_URLS
                    text = "10款 Material Design 配色工具: https://blog.csdn.net/dsc114/article/details/52120080 " +
                            "\nAndroid Theme 属性详解: https://www.jianshu.com/p/06a3bbb7ce79 " +
                            "\n换肤框架：https://github.com/ximsfei/Android-skin-support " +
                            "\n可下载切换主题皮肤: https://www.jianshu.com/p/0d07a2e45be2?tdsourcetag=s_pcqq_aiomsg" +
                            "\n\n\n"
                }

                addView(Switch(mActivity))
                addView(ChipGroup(mActivity).apply {
                    addView(Chip(mActivity).apply {
                        text = "选项一"
                        id = R.id.chip1
                    })
                    addView(Chip(mActivity).apply {
                        text = "选项二"
                        id = R.id.chip2
                    })

                    check(R.id.chip1)
                    setOnCheckedChangeListener { _, checkedId -> check(checkedId) }
                })

                addView(ProgressBar(mActivity))
                addView(SeekBar(mActivity))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add(0, Menu.FIRST + 1, 0, "全局夜间").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return true
    }

    var count = 0

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            Menu.FIRST + 1 -> {
                AlertDialog.Builder(mActivity).setItems(arrayOf("跟随系统", "根据节电模式", "白天", "黑夜", "不指定")) { _, i ->
                    val nm = when (i) {
                        1 -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY// 节能模式时是黑暗主题，否则是白亮主题。
                        2 -> AppCompatDelegate.MODE_NIGHT_NO
                        3 -> AppCompatDelegate.MODE_NIGHT_YES
                        4 -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED // 设置为全局为不指定，应该会使用系统的夜间模式。
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }

                    Const.nightMode = nm
                    AppCompatDelegate.setDefaultNightMode(nm) // 设置全局的夜间模式
                }.show()

            }
            Menu.FIRST + 2 -> {
            }
            Menu.FIRST + 3 -> {}
            Menu.FIRST + 4 -> { }
        }
        return true
    }
}