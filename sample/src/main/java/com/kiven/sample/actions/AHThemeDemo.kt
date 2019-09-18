package com.kiven.sample.actions

import android.os.Bundle
import android.text.util.Linkify
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import com.kiven.sample.util.showListDialog
import org.jetbrains.anko.*

/**
 * Created by oukobayashi on 2019-09-17.
 *
 * Theme 属性详解: https://www.jianshu.com/p/06a3bbb7ce79
 *
 * 日夜主题切换(google推荐): https://www.jianshu.com/p/27f1aad049f7
 *
 * 助你快速搭配 Material Design 配色方案的10款Web工具: https://blog.csdn.net/dsc114/article/details/52120080
 */
class AHThemeDemo : KActivityDebugHelper() {
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
            return KUtil.getSharedPreferencesIntValue("AHThemeDemo.themeId", R.style.Theme_AppCompat_DayNight)
        }

    private fun initUI() {
        mActivity.apply {
            // delegate.localNightMode 不报错，但是编译不通过。
            // delegate.getLocalNightMode()是不对外提供的方法，应该是kotlin编译器出错，才没有报错，但是依然编译不通过。
            // delegate.setLocalNightMode() 能正常使用
            if (nightMode != AppCompatDelegate.getDefaultNightMode()) {
//                delegate.setLocalNightMode(nightMode)
                AppCompatDelegate.setDefaultNightMode(nightMode)
            }
            setTheme(themeId)

            linearLayout {
                gravity = Gravity.CENTER
                orientation = LinearLayout.VERTICAL

                textView {
                    text = "设置夜间主题模式"
                }

                radioGroup {
                    radioButton {
                        id = R.id.rb_0
                        text = "系统默认"
                    }
                    radioButton {
                        id = R.id.rb_1
                        text = "自动"
                    }
                    radioButton {
                        id = R.id.rb_2
                        text = "白天"
                    }
                    radioButton {
                        id = R.id.rb_3
                        text = "黑夜"
                    }

                    check(when (nightMode) {
                        AppCompatDelegate.MODE_NIGHT_AUTO -> R.id.rb_1
                        AppCompatDelegate.MODE_NIGHT_NO -> R.id.rb_2
                        AppCompatDelegate.MODE_NIGHT_YES -> R.id.rb_3
                        else -> R.id.rb_0
                    })

                    setOnCheckedChangeListener { _, rb_id ->
                        nightMode = when (rb_id) {
                            R.id.rb_1 -> AppCompatDelegate.MODE_NIGHT_AUTO
                            R.id.rb_2 -> AppCompatDelegate.MODE_NIGHT_NO
                            R.id.rb_3 -> AppCompatDelegate.MODE_NIGHT_YES
                            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        }

                        // delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES // 不会报错，但是编译不能通过。
//                        delegate.setLocalNightMode(nightMode)
                        AppCompatDelegate.setDefaultNightMode(nightMode)
                        recreate()
                    }
                }

                button {
                    text = "选择主题"
                    setOnClickListener {
                        showListDialog(listOf("系统主题", "自定义主题 双style", "自定义主题 双color")) { index, _ ->
                            themeId = when(index){
                                0 -> R.style.Theme_AppCompat_DayNight
                                1 -> R.style.ThemeDemo
                                else -> R.style.ThemeDemo2
                            }
//                            initUI()
                            recreate()
                        }
                    }
                }
                textView {
                    text = when (themeId) {
                        R.style.Theme_AppCompat_DayNight -> "当前主题是 系统主题"
                        R.style.ThemeDemo -> "当前主题是 自定义主题 双style"
                        else -> "当前主题是 自定义主题 双color"
                    }
                }

                textView {
                    autoLinkMask = Linkify.WEB_URLS
                    text = "10款 Material Design 配色工具: https://blog.csdn.net/dsc114/article/details/52120080 " +
                            "\nAndroid Theme 属性详解: https://www.jianshu.com/p/06a3bbb7ce79"
                }
            }
        }
    }

}