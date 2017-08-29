package com.kiven.sample

import android.os.Bundle
import android.view.View
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity

/**
 * Created by kiven on 2017/8/28.
 */
public class AHLoadActivityTest: KActivityHelper() {
    override fun onCreate(activity: KHelperActivity?, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.activity_lauch)

        var ui:View = findViewById(R.id.item_load_activity)
    }
}