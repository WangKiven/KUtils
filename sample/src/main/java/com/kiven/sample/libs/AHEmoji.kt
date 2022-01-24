package com.kiven.sample.libs

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiTextView
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.vanniktech.emoji.ios.IosEmojiProvider
import com.vanniktech.emoji.twitter.TwitterEmojiProvider
import io.github.rockerhieu.emojicon.EmojiconTextView

/**
 * Created by oukobayashi on 2019-07-29.
 */
class AHEmoji : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        setContentView(NestedScrollView(activity).apply {
            val es = "😀😊🙂😜😝🤑🤓😎🙄 🐛🦅🙊🐸 🌶🥕🥒🍈 🤾‍♀️🤼‍♀️🤾‍♀️⛹ 🚄🚲🏍🚜 📻📺🔋🔌 ❌♒️♑️⛎ 🇻🇬🇧🇷🇧🇧🇧🇦🇧🇴"

            addView(LinearLayout(activity).apply {
                orientation = LinearLayout.VERTICAL

                val addTitle = fun (txt: String) {
                    addView(TextView(activity).apply { text = txt })
                }
                val addTextView = fun(textView: TextView) {
                    textView.text = es
                    textView.textSize = 25f
                    addView(textView)
                }

                addTitle("TextView")
                addTextView(TextView(activity))

                addTitle("EmojiconTextView - emojicon库")
                addTextView(EmojiconTextView(activity))

                addTitle("AppCompatTextView - 安卓自带")
                addTextView(AppCompatTextView(activity))

                addTitle("EmojiTextView - 安卓自带")
                EmojiCompat.init(BundledEmojiCompatConfig(activity).setReplaceAll(true))
                addTextView(androidx.emoji.widget.EmojiTextView(activity))

                addTitle("EmojiTextView - google")
                EmojiManager.install(GoogleEmojiProvider())
                addTextView(EmojiTextView(activity))

                addTitle("EmojiTextView - ios")
                EmojiManager.install(IosEmojiProvider())
                addTextView(EmojiTextView(activity))

                addTitle("EmojiTextView - twitter")
                EmojiManager.install(TwitterEmojiProvider())
                addTextView(EmojiTextView(activity))

            })

//            linearLayout {
//                orientation = LinearLayout.VERTICAL


//                textView("TextView")
//                textView {
//                    text = es
//                    textSize = 25f
//                }
//
//                textView("EmojiconTextView - emojicon库")
//                var etv:TextView = EmojiconTextView(activity)
//                etv.text = es
//                etv.textSize = 25f
//                addView(etv)


//                textView("AppCompatTextView - 安卓自带")
//                etv = AppCompatTextView(activity)
//                etv.text = es
//                etv.textSize = 25f
//                addView(etv)

//                textView("EmojiTextView - 安卓自带")
//                EmojiCompat.init(BundledEmojiCompatConfig(activity).setReplaceAll(true))
//                etv = androidx.emoji.widget.EmojiTextView(activity)
//                etv.text = es
//                etv.textSize = 25f
//                addView(etv)

//                textView("EmojiTextView - google")
//                EmojiManager.install(GoogleEmojiProvider())
//                etv = EmojiTextView(activity)
//                etv.text = es
//                etv.textSize = 25f
//                addView(etv)

//                textView("EmojiTextView - ios")
//                EmojiManager.install(IosEmojiProvider())
//                etv = EmojiTextView(activity)
//                etv.text = es
//                etv.textSize = 25f
//                addView(etv)

//                textView("EmojiTextView - twitter")
//                EmojiManager.install(TwitterEmojiProvider())
//                etv = EmojiTextView(activity)
//                etv.text = es
//                etv.textSize = 25f
//                addView(etv)
//            }
        })
    }
}