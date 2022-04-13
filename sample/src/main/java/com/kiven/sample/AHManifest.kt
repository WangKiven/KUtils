package com.kiven.sample

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.util.showListDialog
import com.kiven.sample.util.showTip
import com.kiven.sample.util.showToast

class AHManifest: BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        addTitle("获取Manifest文件里面的配置信息")

        //https://blog.csdn.net/ccpat/article/details/51559168
        addBtn("uses-feature") {
            showTip("uses-feature 安装这个app的硬件要求，仅在应用市场安装App时，判断是否能安装在某个设备上。但是不通过应用市场安装，uses-feature可能就没有用了。")
            activity.packageManager.systemAvailableFeatures.forEach {
                showTip(it.toString())
            }
        }
        addBtn("getApplicationInfo") {
            val s1: String
            val s2: String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                s1 = "MATCH_DISABLED_UNTIL_USED_COMPONENTS"
                s2 = "MATCH_UNINSTALLED_PACKAGES"
            } else {
                s1 = "GET_DISABLED_UNTIL_USED_COMPONENTS"
                s2 = "GET_UNINSTALLED_PACKAGES"
            }
            activity.showListDialog(arrayOf("GET_META_DATA", "GET_SHARED_LIBRARY_FILES", s2, "MATCH_SYSTEM_ONLY",
                "MATCH_DISABLED_COMPONENTS", s1, "MATCH_APEX")) { _, s ->
                if (s == "MATCH_SYSTEM_ONLY" || s == "MATCH_DISABLED_COMPONENTS") {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                        return@showListDialog showToast("当前系统不支持 $s")
                }

                if (s == "MATCH_APEX") {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                        return@showListDialog showToast("当前系统不支持 $s")
                }

                try {
                    val pmClass = PackageManager::class.java
                    val field = pmClass.getDeclaredField(s)
                    val value = field.getInt(pmClass)

                    val info = activity.packageManager.getApplicationInfo(activity.packageName, value)
                        .metaData ?: return@showListDialog showToast("$s 为 null")

                    showTip("以下是 $s 配置数据")
                    info.keySet().forEach {
                        showTip("$it = ${info[it]}")
                    }
                } catch (t: Throwable) {
                    showToast("出现异常 $s")
                    KLog.e(t)
                }
            }
        }
        addBtn("") {}
        addBtn("") {}
        addBtn("") {}
        addBtn("") {}
        addBtn("") {}
        addBtn("") {}
        addBtn("") {}
    }
}