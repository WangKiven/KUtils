package com.kiven.sample.libs

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.EmojiTextView
import com.vanniktech.emoji.google.GoogleEmojiProvider

/**
 * Created by oukobayashi on 2019-07-29.
 */
class AHEmoji : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        setContentView(NestedScrollView(activity).apply {
            val es = "😀😊🙂😜😝🤑🤓😎🙄 🐛🦅🙊🐸 🌶🥕🥒🍈 🤾‍♀️🤼‍♀️🤾‍♀️⛹ 🚄🚲🏍🚜 📻📺🔋🔌 ❌♒️♑️⛎ 🇻🇬🇧🇷🇧🇧🇧🇦🇧🇴🇨🇳"

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

                addTitle("AppCompatTextView - 安卓自带")
                addTextView(AppCompatTextView(activity))

                addTitle("EmojiTextView - 安卓自带")
                EmojiCompat.init(BundledEmojiCompatConfig(activity).setReplaceAll(true))
                addTextView(androidx.emoji2.widget.EmojiTextView(activity))


                // https://github.com/vanniktech/Emoji

                // GoogleEmojiProvider IosEmojiProvider TwitterEmojiProvider
                addTitle("EmojiTextView - google / ios / twitter")
                EmojiManager.install(GoogleEmojiProvider())
                addTextView(EmojiTextView(activity))


                addTitle("EmojiTextView - 键盘位置")
                val eet = EmojiEditText(activity)
                addView(eet)
                val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(eet)
//                emojiPopup.toggle(); // Toggles visibility of the Popup.
//                emojiPopup.dismiss(); // Dismisses the Popup.
//                emojiPopup.isShowing();
                addView(Button(activity).apply {
                    text = "显示键盘"
                    setOnClickListener { emojiPopup.toggle() }
                })
            })
        })
    }
}