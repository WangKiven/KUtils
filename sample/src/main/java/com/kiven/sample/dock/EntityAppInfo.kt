package com.kiven.sample.dock

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi

import com.kiven.kutils.logHelper.KLog

/**
 * app信息
 * Created by kiven on 16/10/4.
 */

class EntityAppInfo(private val packageManager: PackageManager, private val applicationInfo: ApplicationInfo) {

    var appTitle: CharSequence = ""      //APP名称
    //    public String appIcon;              //icon
    var packageName: String          //包名
    var isSystem: Boolean = false            //是否是系统服务
    var canStart: Boolean = false             //可以启动

    @RequiresApi(api = Build.VERSION_CODES.N)
    var minSdkVersion: Int = 0
    var targetSdkVersion: Int = 0
    var permissions: Array<PermissionInfo> = arrayOf()

    var firstInstallTime: Long = 0       //安装时间
    var lastUpdateTime: Long = 0         //跟新时间
    var curVersion: String = ""           //当前版本名称
    var curVersionCode: Int = 0          //当前版本号

    /**
     * app图标
     */
    val appIcon: Drawable
        get() = packageManager.getApplicationIcon(applicationInfo)

    init {

        appTitle = applicationInfo.loadLabel(packageManager)
        packageName = applicationInfo.packageName

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minSdkVersion = applicationInfo.minSdkVersion
        }
        targetSdkVersion = applicationInfo.targetSdkVersion

        try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)

            firstInstallTime = packageInfo.firstInstallTime
            lastUpdateTime = packageInfo.lastUpdateTime
            curVersion = packageInfo.versionName
            curVersionCode = packageInfo.versionCode
            permissions = packageInfo.permissions ?: arrayOf()
        } catch (e: PackageManager.NameNotFoundException) {
            KLog.e(e)
        }

        isSystem = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == ApplicationInfo.FLAG_SYSTEM

        // 获取启动Intent, 如果获取到空，表示没有提供启动界面
        canStart = packageManager.getLaunchIntentForPackage(packageName) != null
    }
}
