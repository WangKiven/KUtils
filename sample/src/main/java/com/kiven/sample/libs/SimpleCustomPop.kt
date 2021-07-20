package com.kiven.sample.libs

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flyco.dialog.widget.popup.base.BaseBubblePopup
import com.kiven.kutils.tools.KUtil

/**
 * Created by wangk on 2019/5/29.
 */
class SimpleCustomPop(context: Context?) : BaseBubblePopup<SimpleCustomPop>(context) {
    override fun onCreateBubbleView(): View {
        return TextView(context).apply {
            text = "pop pop opo"

            val p = KUtil.dip2px(10f)
            setPadding(p, p, p, p)
        }
    }
}