package com.kiven.sample.libs.chatkit

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity

class AHChatList:KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val listView = RecyclerView(activity)
        setContentView(listView)

    }
}