package com.kiven.sample.vpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity


/**
 * Created by oukobayashi on 2020/6/22.
 */
class AHMyVpn:KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(NestedScrollView(activity).apply {
            addView(flexboxLayout)
        })

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }

//        addTitle("检测与杀死app")

        addView("开启VPN", View.OnClickListener {
            val prepare = VpnService.prepare(mActivity)
            if (prepare == null) {
                onActivityResult(988, Activity.RESULT_OK, null)
            } else {
                mActivity.startActivityForResult(prepare, 988)
            }
        })
        addView("ToyVpn", View.OnClickListener {
            AHToyVpn().startActivity(mActivity)
        })
        addView("", View.OnClickListener {})
        addView("", View.OnClickListener {})
        addView("", View.OnClickListener {})
        addView("", View.OnClickListener {})
        addView("", View.OnClickListener {})
        addView("", View.OnClickListener {})
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == 988) {
            mActivity.startService(Intent(mActivity, MyVPNService::class.java))
        }
    }
}