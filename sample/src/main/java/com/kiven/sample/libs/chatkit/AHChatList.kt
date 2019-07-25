package com.kiven.sample.libs.chatkit

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.util.Const
import com.kiven.sample.xutils.db.entity.User
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.dialogs.DialogsList
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.linearLayout
import java.util.*

class AHChatList : KActivityDebugHelper() {

    val adapter by lazy {
        DialogsListAdapter<DefaultDailog>(ImageLoader { imageView, url, payload ->
            imageView?.let { Glide.with(mActivity).load(url).circleCrop().into(it) }
        })
    }


    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
//        val listView = RecyclerView(activity)
//        setContentView(listView)

        val listView = DialogsList(activity)
        listView.setAdapter(adapter)
//        setContentView(listView)
        activity.linearLayout {
            orientation = LinearLayout.VERTICAL
            toolbar {
                initBackToolbar(this)
            }
            addView(listView as View, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        adapter.addItem(DefaultDailog())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, Menu.FIRST + 1, 0, "一条新对话")
        menu.add(0, Menu.FIRST + 2, 0, "两条新对话")
        menu.add(0, Menu.FIRST + 3, 1, "一条图片对话")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            Menu.FIRST + 1 -> {
                adapter.addItem(DefaultDailog())
            }
            Menu.FIRST + 2 -> {
            }
            Menu.FIRST + 3 -> {
            }
        }
        return true
    }

    class DefaultDailog : IDialog<DefaultMessage> {
        var lastMsg: DefaultMessage? = DefaultMessage("msgId004", Date(), DefaultUser("x33", "34k", ""), "i love u")
        var dialogId = "0"

        override fun getDialogPhoto(): String {
            return Const.randomImage()
        }

        override fun getUnreadCount(): Int {
            return (Math.random() * 100).toInt()
        }

        override fun setLastMessage(message: DefaultMessage?) {
            lastMsg = message
        }

        override fun getId(): String {
            return dialogId
        }

        override fun getUsers(): MutableList<out IUser> {
            return mutableListOf(
                    DefaultUser("123", "Katter", Const.randomImage()),
                    DefaultUser("143", "Katter", Const.randomImage())

            )
        }

        override fun getLastMessage(): DefaultMessage? {
            return lastMsg
        }

        override fun getDialogName(): String {
            return "群名"
        }

    }

    class DefaultMessage(private val msgId: String,
                         private val time: Date,
                         private val who: IUser,
                         private val msgInfo: String) : IMessage {
        override fun getId(): String = msgId

        override fun getCreatedAt(): Date = time

        override fun getUser(): IUser = who

        override fun getText(): String = msgInfo

    }

    class DefaultUser(private val userId: String,
                      private val userName: String,
                      private val header: String) : IUser {

        override fun getAvatar(): String = header

        override fun getName(): String = userName

        override fun getId(): String = userId
    }
}