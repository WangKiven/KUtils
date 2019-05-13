package com.kiven.sample.noti

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textView

class ClickNotiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        linearLayout {
            textView { text = "你点击了通知" }
        }
    }


}
