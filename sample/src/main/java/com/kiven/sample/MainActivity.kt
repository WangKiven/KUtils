package com.kiven.sample

import android.os.Bundle
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.sample.util.addTitle
import kotlinx.android.synthetic.main.main_activity.*

/**
 * Created by wangk on 2020/12/2.
 */
class MainActivity : KActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        loadUI()
    }

    private fun loadUI() {
        flexbox.apply {
            addTitle("控件")
            addTitle("三方库")
            addTitle("其他")
        }
    }
}