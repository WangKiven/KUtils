package com.kiven.sample.noti

import android.app.NotificationChannelGroup
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import kotlinx.android.synthetic.main.item_noti_child.view.*
import kotlinx.android.synthetic.main.item_noti_group.view.*

/**
 * Created by wangk on 2019/5/13.
 */
class AHAllNotis : KActivityDebugHelper() {

    val groups = mutableListOf<NotificationChannelGroup>()

    val recyclerView: RecyclerView by lazy {
        RecyclerView(mActivity)
    }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)


        val adapterManager = RecyclerViewExpandableItemManager(savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(mActivity)
        recyclerView.adapter = adapterManager.createWrappedAdapter(MyAdapter())
        setContentView(recyclerView)

        adapterManager.attachRecyclerView(recyclerView)
        adapterManager.expandAll()
    }

    override fun onResume() {
        super.onResume()

        val notiManager = NotificationManagerCompat.from(mActivity)

        groups.clear()
        groups.addAll(notiManager.notificationChannelGroups)

        recyclerView.adapter?.notifyDataSetChanged()
    }

    private inner class MyAdapter : AbstractExpandableItemAdapter<RecyclerView.ViewHolder, RecyclerView.ViewHolder>() {
        init {
            setHasStableIds(true)
        }

        override fun getChildCount(groupPosition: Int): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            groups[groupPosition].channels.size
        } else {
            0
        }

        override fun onCheckCanExpandOrCollapseGroup(p0: RecyclerView.ViewHolder?, p1: Int, p2: Int, p3: Int, p4: Boolean): Boolean {
            return true
        }

        override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_noti_group, parent, false)) {}
        }

        override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_noti_child, parent, false)) {}
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun onBindChildViewHolder(p0: RecyclerView.ViewHolder?, p1: Int, p2: Int, p3: Int) {
            p0?.itemView?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = groups[p1].channels[p2]

                    tv_message.text = "${channel.id}: ${channel.name}"
                }

            }
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun getGroupCount(): Int = groups.size

        override fun onBindGroupViewHolder(p0: RecyclerView.ViewHolder?, p1: Int, p2: Int) {
            p0?.itemView?.apply {
                val group = groups[p1]
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv_title.text = "${group.id}: ${group.name}"
                }
            }
        }
    }
}