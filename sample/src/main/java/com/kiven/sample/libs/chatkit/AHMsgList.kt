package com.kiven.sample.libs.chatkit

import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import com.kiven.sample.util.Const
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.ah_msg_list.*
import java.util.*

/**
 * Created by oukobayashi on 2019-07-25.
 */
class AHMsgList : KActivityDebugHelper() {
    val myId = "ddddddd"

    val adapter by lazy {
        MessagesListAdapter<DefaultMessage>(myId, ImageLoader { imageView, url, payload ->
            imageView?.let { Glide.with(mActivity).load(url).into(it) }
        })
    }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_msg_list)
        initBackToolbar(R.id.toolbar)

        activity.apply {
            messagesList.setAdapter(adapter)
            adapter.setOnMessageClickListener {
                Snackbar.make(activity.window.decorView, "别点我", Snackbar.LENGTH_LONG).show()
            }

            val mi = DefaultUser(myId, "kee", Const.randomImage())
            val ta = DefaultUser("sss", "kee", Const.randomImage())
            var count = 0
            input.setInputListener {
                val message = DefaultMessage("12", Date(), if (count %2 == 0) mi else ta, it.toString())
                adapter.addToStart(message, true)
                count ++
                return@setInputListener true
            }

            input.setAttachmentsListener {
                val message = DefaultImageMessage("12", Date(), if (count %2 == 0) mi else ta, Const.randomImage())
                adapter.addToStart(message, true)
                count ++
            }
        }
    }
}