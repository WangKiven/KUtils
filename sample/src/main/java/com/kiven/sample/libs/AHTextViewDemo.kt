package com.kiven.sample.libs

import android.os.Bundle
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KNetwork
import com.kiven.kutils.tools.KString
import com.kiven.sample.R
import kotlinx.android.synthetic.main.ah_textview_demo.*
import me.grantland.widget.AutofitHelper

/**
 * Created by wangk on 2020/12/6.
 */
class AHTextViewDemo : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_textview_demo)


        activity.apply {
            AutofitHelper.create(et_auto)
            et_auto.setText(KNetwork.getIPAddress() ?: "")
            textView2.text = KString.fromHtml(getString(R.string.text_test, 5, 9))
        }
    }
}