package com.kiven.kutils.tools;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

public class KOpenSetting {

    /**
     * 获取应用自启动设置状态
     *
     * @return -1：未获取到状态，0：不允许自启动，1：允许自启动
     */
    public static int getAutoStartType(@NonNull Context context) {
        String mobileType = Build.MANUFACTURER;

        try {
            if (mobileType.equals("Xiaomi")) {
                @SuppressLint("PrivateApi")
                Method method = Class.forName("android.miui.AppOpsUtils")
                        .getMethod("getApplicationAutoStart", Context.class, String.class);

                int mi = (int) method.invoke(null, context, context.getPackageName());
                if (mi == 0){//0已允许, 1已拒绝
                    return 1;
                }else {
                    return 0;
                }
            } else if (mobileType.equals("HUAWEI")) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 打开自启动设置界面
     */
    public static void openAutoStartSetting(@NonNull Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            String mobileType = Build.MANUFACTURER;
            ComponentName componentName = null;
            if (mobileType.equals("Xiaomi")) { // 红米Note4测试通过
                componentName = new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity");
            } else if (mobileType.equals("Letv")) { // 乐视2测试通过
                intent.setAction("com.letv.android.permissionautoboot");
            } else if (mobileType.equals("samsung")) { // 三星Note5测试通过
                componentName = new ComponentName("com.samsung.android.sm_cn",
                        "com.samsung.android.sm.ui.ram.AutoRunActivity");
            } else if (mobileType.equals("HUAWEI")) { // 华为测试通过
                componentName = new ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity");
            } else if (mobileType.equals("vivo")) { // VIVO测试通过
                componentName = ComponentName.unflattenFromString("com.iqoo.secure" +
                        "/.safeguard.PurviewTabActivity");
            } else if (mobileType.equals("Meizu")) { //万恶的魅族
                // 通过测试，发现魅族是真恶心，也是够了，之前版本还能查看到关于设置自启动这一界面，
                // 系统更新之后，完全找不到了，心里默默Fuck！
                // 针对魅族，我们只能通过魅族内置手机管家去设置自启动，
                // 所以我在这里直接跳转到魅族内置手机管家界面，具体结果请看图
                componentName = ComponentName.unflattenFromString("com.meizu.safe" +
                        "/.permission.PermissionMainActivity");
            } else if (mobileType.equals("OPPO")) { // OPPO R8205测试通过
                componentName = ComponentName.unflattenFromString("com.oppo.safe" +
                        "/.permission.startup.StartupAppListActivity");
                Intent intentOppo = new Intent();
                intentOppo.setClassName("com.oppo.safe/.permission.startup",
                        "StartupAppListActivity");
                if (context.getPackageManager().resolveActivity(intentOppo, 0) == null) {
                    componentName = ComponentName.unflattenFromString("com.coloros.safecenter" +
                            "/.startupapp.StartupAppListActivity");
                }

            } else if (mobileType.equals("ulong")) { // 360手机 未测试
                componentName = new ComponentName("com.yulong.android.coolsafe",
                        ".ui.activity.autorun.AutoRunListActivity");
            } else {
                // 以上只是市面上主流机型，由于公司你懂的，所以很不容易才凑齐以上设备
                // 针对于其他设备，我们只能调整当前系统app查看详情界面
                // 在此根据用户手机当前版本跳转系统设置界面
                if (Build.VERSION.SDK_INT >= 9) {
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("com.android.settings",
                            "com.android.settings.InstalledAppDetails");
                    intent.putExtra("com.android.settings.ApplicationPkgName",
                            context.getPackageName());
                }
            }
            if (componentName != null)
                intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
