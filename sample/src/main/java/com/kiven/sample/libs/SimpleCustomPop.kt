package com.kiven.sample.libs

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flyco.dialog.widget.popup.base.BaseBubblePopup
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding

/**
 * Created by wangk on 2019/5/29.
 */
class SimpleCustomPop(context: Context?) : BaseBubblePopup<SimpleCustomPop>(context) {
    override fun onCreateBubbleView(): View {
        return TextView(context).apply { text = "pop pop opo"; padding = dip(10) }
    }
}