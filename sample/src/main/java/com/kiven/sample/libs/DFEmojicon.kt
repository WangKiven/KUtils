package com.kiven.sample.libs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import io.github.rockerhieu.emojicon.EmojiconGridFragment
import io.github.rockerhieu.emojicon.EmojiconTextView
import io.github.rockerhieu.emojicon.EmojiconsFragment
import io.github.rockerhieu.emojicon.emoji.Emojicon
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp

/**
 * Created by oukobayashi on 2019-07-29.
 */
class DFEmojicon : DialogFragment(), EmojiconsFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener {
    private val textView by lazy { EmojiconTextView(activity) }

    override fun onEmojiconBackspaceClicked(v: View?) {
        textView.text = ""
    }

    override fun onEmojiconClicked(emojicon: Emojicon?) {
        textView.text = "Click:" + emojicon?.emoji
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return activity?.run {
            val vv = LinearLayout(this)
            vv.apply {
                orientation = LinearLayout.VERTICAL

//                textView.textSize = sp(20).toFloat()
                addView(textView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

                val fram = FrameLayout(this@run)
                fram.id = 101
                addView(fram, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip(250)))

                childFragmentManager.beginTransaction().add(101, EmojiconsFragment()).commit()
            }

            vv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            vv
        }
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}