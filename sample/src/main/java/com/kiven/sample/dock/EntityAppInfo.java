package com.kiven.sample.dock;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.kiven.kutils.logHelper.KLog;

/**
 * app信息
 * Created by kiven on 16/10/4.
 */

public class EntityAppInfo {
    public final PackageManager packageManager;
    public final ApplicationInfo applicationInfo;

    public CharSequence appTitle;       //APP名称
    //    public String appIcon;              //icon
    public String packageName;          //包名
    public boolean isSystem;            //是否是系统服务
    public boolean canStart;             //可以启动

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int minSdkVersion;
    public int targetSdkVersion;
    public PermissionInfo[] permissions;

    public long firstInstallTime;       //安装时间
    public long lastUpdateTime;         //跟新时间
    public String curVersion;           //当前版本名称
    public int curVersionCode;          //当前版本号

    public EntityAppInfo(PackageManager packageManager, ApplicationInfo applicationInfo) {
        this.packageManager = packageManager;
        this.applicationInfo = applicationInfo;

        appTitle = applicationInfo.loadLabel(packageManager);
        packageName = applicationInfo.packageName;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minSdkVersion = applicationInfo.minSdkVersion;
        }
        targetSdkVersion = applicationInfo.targetSdkVersion;

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

            firstInstallTime = packageInfo.firstInstallTime;
            lastUpdateTime = packageInfo.lastUpdateTime;
            curVersion = packageInfo.versionName;
            curVersionCode = packageInfo.versionCode;
            permissions = packageInfo.permissions;
        } catch (PackageManager.NameNotFoundException e) {
            KLog.e(e);
        }
        isSystem = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;

        // 获取启动Intent, 如果获取到空，表示没有提供启动界面
        canStart = packageManager.getLaunchIntentForPackage(packageName) != null;
    }

    /**
     * app图标
     */
    public Drawable getAppIcon() {
        return packageManager.getApplicationIcon(applicationInfo);
    }
}
