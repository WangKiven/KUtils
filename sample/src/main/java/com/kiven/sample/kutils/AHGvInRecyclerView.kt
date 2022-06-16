package com.kiven.sample.kutils

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.widget.UIGridView
import com.kiven.sample.R
import com.kiven.sample.util.phoneImages

class AHGvInRecyclerView : KActivityHelper() {
    private lateinit var recyclerView: RecyclerView
    private val data = mutableListOf<List<Long>>()
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        recyclerView = RecyclerView(activity).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MyAdapter()
        }
        setContentView(recyclerView)

        loadData()
    }

    private fun loadData() {
        mActivity.phoneImages {
            for (i in 0..1000) {
                val r = 1 + (Math.random() * 100).toInt() % 6
                val ims = List(r) { _ ->
                    it.random()[MediaStore.Images.Media._ID]?.toLong() ?: 0L
                }
                data.add(ims)
            }

            mActivity.runOnUiThread {
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    // 依赖
    private var holderInitCount = 0
    private inner class MyHolder(v: View) : RecyclerView.ViewHolder(v) {
        private lateinit var data: List<Long>
        private val tv1: TextView = v.findViewById(R.id.tv1)
        private val gridView: UIGridView = v.findViewById(R.id.gridView)
        private val checkBox: CheckBox = v.findViewById(R.id.checkbox)
        private val adapter = ImageAdapter()
        private var isChecked = false
        private val initCount = (holderInitCount++)

        init {
            gridView.setAdapter(adapter)
            checkBox.setOnClickListener {
                isChecked = !isChecked
                recyclerView.adapter?.notifyItemChanged(position)
            }
        }

        fun bindData(data: List<Long>) {
            this.data = data
            tv1.text = "position$position/adapterPosition$adapterPosition/layoutPosition$layoutPosition/absoluteAdapterPosition$absoluteAdapterPosition/oldPosition$oldPosition/bindingAdapterPosition$bindingAdapterPosition/holder计数$initCount"

            adapter.changeData(data.map {
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    it
                )
//                KPath.getPath(uri)
                uri
            })
        }
    }

    private inner class MyAdapter : RecyclerView.Adapter<MyHolder>() {
        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.bindData(data[position])
        }

        override fun getItemCount(): Int = data.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder = MyHolder(
            LayoutInflater.from(mActivity).inflate(R.layout.item_test_grid_view, parent, false)
        )
    }
}