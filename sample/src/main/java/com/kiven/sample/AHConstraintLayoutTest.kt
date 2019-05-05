package com.kiven.sample

import android.os.Bundle
import android.support.constraint.Placeholder
import android.view.View
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity

/**
 * Created by wangk on 2019/5/5.
 */
class AHConstraintLayoutTest : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_constraint_layout_test)
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.button4 -> {
                findViewById<Placeholder>(R.id.placeholder).setContentId(R.id.textView)
            }
        }
    }
}