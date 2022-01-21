package com.kiven.sample

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.kutils.tools.*
import com.sxb.kutils_ktx.util.main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay

class LaunchActivity : KActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lauch)

        findViewById<TextView>(R.id.from).text = "from Kiven\n${BuildConfig.dpkTime}"

        GlobalScope.main {
            delay(2500)
            startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
            finish()
        }
    }
}
