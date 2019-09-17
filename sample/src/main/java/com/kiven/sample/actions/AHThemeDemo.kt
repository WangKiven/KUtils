package com.kiven.sample.actions

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import org.jetbrains.anko.*

/**
 * Created by oukobayashi on 2019-09-17.
 *
 * Theme 属性详解: https://www.jianshu.com/p/06a3bbb7ce79
 *
 * 日夜主题切换(google推荐): https://www.jianshu.com/p/27f1aad049f7
 */
class AHThemeDemo : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        initUI()
    }

    var nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    private fun initUI(){
        mActivity.apply {
            setTheme(R.style.Theme_AppCompat_DayNight)

            R.style.ThemeOverlay_AppCompat
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

                    check(when(nightMode){
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
                        delegate.setLocalNightMode(nightMode)
                    }
                }
            }
        }
    }

}