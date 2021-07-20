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
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textView

/**
 * Created by oukobayashi on 2019-07-29.
 */
class AHEmoji : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        setContentView(NestedScrollView(activity).apply {

            linearLayout {
                orientation = LinearLayout.VERTICAL

                val es = "😀😊🙂😜😝🤑🤓😎🙄 🐛🦅🙊🐸 🌶🥕🥒🍈 🤾‍♀️🤼‍♀️🤾‍♀️⛹ 🚄🚲🏍🚜 📻📺🔋🔌 ❌♒️♑️⛎ 🇻🇬🇧🇷🇧🇧🇧🇦🇧🇴 "

                textView("TextView")
                textView {
                    text = es
                    textSize = 25f
                }

                textView("EmojiconTextView - emojicon库")
                var etv:TextView = EmojiconTextView(activity)
                etv.text = es
                etv.textSize = 25f
                addView(etv)


                textView("AppCompatTextView - 安卓自带")
                etv = AppCompatTextView(activity)
                etv.text = es
                etv.textSize = 25f
                addView(etv)

                textView("EmojiTextView - 安卓自带")
                EmojiCompat.init(BundledEmojiCompatConfig(activity).setReplaceAll(true))
                etv = androidx.emoji.widget.EmojiTextView(activity)
                etv.text = es
                etv.textSize = 25f
                addView(etv)

                textView("EmojiTextView - google")
                EmojiManager.install(GoogleEmojiProvider())
                etv = EmojiTextView(activity)
                etv.text = es
                etv.textSize = 25f
                addView(etv)

                textView("EmojiTextView - ios")
                EmojiManager.install(IosEmojiProvider())
                etv = EmojiTextView(activity)
                etv.text = es
                etv.textSize = 25f
                addView(etv)

                textView("EmojiTextView - twitter")
                EmojiManager.install(TwitterEmojiProvider())
                etv = EmojiTextView(activity)
                etv.text = es
                etv.textSize = 25f
                addView(etv)
            }
        })
    }
}