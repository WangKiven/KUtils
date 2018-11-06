package com.kiven.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KUtil
import kotlinx.android.synthetic.main.ah_check_res.view.*
import kotlinx.android.synthetic.main.item_res.view.*
import org.jetbrains.anko.backgroundColor
import java.lang.reflect.Field

/**
 *
 * Created by kiven on 2017/8/28.
 */
class AHCheckRes : KActivityHelper() {
    var dclass: Class<*> = android.R.drawable::class.java
    var types = dclass.fields

    private var resWhere = 0
    private var resType = 0

    private val resAdapter = ResAdapter()

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_check_res)

        val ui = findViewById<View>(R.id.ll_root)
        ui.toolbar.backgroundColor = Color.BLACK

        val toolBar: Toolbar = findViewById(R.id.toolbar)
        mActivity.setSupportActionBar(toolBar)
        val actionBar = mActivity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)

            toolBar.setNavigationOnClickListener { finish() }
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(mActivity, KUtil.getScreenWith(mActivity) / KUtil.dip2px(50f))
        recyclerView.adapter = resAdapter


        findViewById<AppCompatSpinner>(R.id.spinner_where).onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                resWhere = p2
                onChange()
            }

        }

        findViewById<AppCompatSpinner>(R.id.spinner_type).onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                resType = position
                onChange()
            }
        }

        findViewById<TextView?>(R.id.tv_bd)?.setOnClickListener { }
    }

    private fun onChange() {
        dclass = if (resWhere == 0) {
            when (resType) {
                0 -> android.R.drawable::class.java
                else -> android.R.mipmap::class.java
            }

        } else {
            when (resType) {
                0 -> R.drawable::class.java
                else -> R.mipmap::class.java
            }
        }

        types = dclass.fields

        resAdapter.notifyDataSetChanged()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(field: Field, position: Int) {
            with(itemView) {
                try {
                    imageView.setImageResource(field.getInt(dclass))
                } catch (e: Exception) {
                }
                tv_num.text = "$position"
            }
        }
    }

    inner class ResAdapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(mActivity).inflate(R.layout.item_res, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bindData(types[position], position)
        }

        override fun getItemCount(): Int = types.size

    }
}