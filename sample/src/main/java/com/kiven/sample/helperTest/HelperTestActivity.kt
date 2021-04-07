package com.kiven.sample.helperTest

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class HelperTestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(TextView(this).apply {
            text = "这是个 Activity，是继承的 android.app.Activity。" +
                    "KActivityHelper使用的是继承自androidx.appcompat.app.AppCompatActivity的Activity，" +
                    "为了看DebugView是否正常显示，所以有了这个测试。"
        })
    }
}