package com.kiven.sample.dock

import android.os.Bundle
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.sample.R

/**
 * Created by oukobayashi on 2019-08-09.
 */
class ActivityDock:KActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dock)
    }
}