package com.kiven.sample.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kiven.kutils.tools.KUtil

/**
 * Created by oukobayashi on 2019-12-23.
 */
class PersistentReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        KUtil.startService(PersistentService::class.java)
    }
}