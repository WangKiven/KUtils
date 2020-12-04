package com.kiven.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.util.showListDialog

/**
 * Created by wangk on 2020/12/4.
 */
class AHOpenSetting: BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        addBtn("打开设置", View.OnClickListener {
            //            startActivity(Intent(Settings.ACTION_SETTINGS))
            val fields = Settings::class.java.fields
            val flist = mutableMapOf<String, String>()
            fields.forEach {
                try {
                    if (it.name.startsWith("ACTION_")) {
                        val value = it.get(Settings::class.java)
                        if (value != null && value is String) {
                            flist[it.name] = value
                        }
                    }
                } catch (e: Exception) {

                }
            }
            activity.showListDialog(flist.keys.toList()) { _, key ->
                activity.startActivity(Intent(flist[key]))
            }
        })
        addBtn("打开应用设置", View.OnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(intent)
        })
        addBtn("打开WiFi设置", View.OnClickListener {
            activity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        })
    }
}