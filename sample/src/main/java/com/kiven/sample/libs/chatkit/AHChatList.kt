package com.kiven.sample.libs.chatkit

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsList
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.linearLayout

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
                title = "聊天列表"
            }
            addView(listView as View, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        adapter.addItem(DefaultDailog())
        adapter.setOnDialogClickListener {
            AHMsgList().startActivity(activity)
        }
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
}