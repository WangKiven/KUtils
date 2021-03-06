package com.kiven.kutils.tools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import android.widget.Toast;

import com.kiven.kutils.logHelper.KLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wangk on 2018/1/3.
 */

public class KAppTool {

    // TODO: 2018/1/3 --------------------------------------- 安装apk ---------------------------------------

    /**
     * 安装apk
     */
    public static void installApk(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                /*val URI = Uri.parse("package:$packageName")
                app.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, URI))*/

                // 1. 配置Manifest: <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
                // 2. 调用该方法处，检测权限。上边代码为打开权限设置界面
                KToast.ToastMessage(context, "没有安装权限", Toast.LENGTH_LONG);
                KLog.i("没有安装权限");
                return;
            }
        }
        Intent mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        mIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(mIntent);
    }

    /**
     * 安装apk
     *
     * @param path      文件路径
     * @param authority android 7.0 共享文件请求权限的 authority，需要配置manifests文件
     */
    public static void installApk(Context context, String path, String authority) {
        installApk(context, new File(path), authority);
    }

    public static void installApk(Context context, File file, String authority) {
        Uri apkUri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            apkUri = Uri.fromFile(file);
        } else {
            apkUri = FileProvider.getUriForFile(context, authority, file);
        }
        installApk(context, apkUri);
    }

    /**
     * 静默安装apk
     * http://blog.csdn.net/u013598111/article/details/50240249
     *
     * @param path 文件路径
     * @return 返回值0表示安装成功，1表示文件不存在，2表示其他错误。需要更丰富的安装失败信息(内存不足、解析包出错)可直接使用PackageUtils.installSlient。
     */
    public static int installApkSlent(Context context, String path) {
        return installApkSlent(context, new File(path));
    }

    public static int installApkSlent(Context context, File file) {
        if (file == null || !file.exists() || !file.isFile() || file.length() == 0) {
            return 1;
        }

        String[] args = {"pm", "install", "-r", file.getAbsolutePath()};
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result;
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }

            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = 2;
        } catch (Exception e) {
            e.printStackTrace();
            result = 2;
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }

        // TODO should add memory is not enough here
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            result = 0;
        } else {
            result = 2;
        }
        KLog.i("successMsg:" + successMsg + ", ErrorMsg:" + errorMsg);
        return result;
    }

    // TODO: 2018/1/3 --------------------------------------- 启动APP ---------------------------------------


    /**
     * 启动APP
     *
     * @param pkg 包名
     * @param cls 启动界面activity名称
     */
    public static void startApp(@NonNull Context context, @NonNull String pkg, String cls) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pkg, cls));
        // TODO 不加这个,yy启动不了。yy可能检测过任务堆栈的
        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 根据包名启动APP
     *
     * @return 是否启动成功
     */
    public static boolean startApp(@NonNull Context context, @NonNull String packageName) {
        try {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                context.startActivity(launchIntent);
                return true;
            }
        } catch (Exception e) {
            KLog.e(e);
        }

        // 未检测到应用
//        KAlertDialogHelper.Show1BDialog(this, "未检测到应用");
        return false;
    }


    // TODO: 2018/1/3 --------------------------------------- 检测 ---------------------------------------

    /**
     * 获取App信息
     *
     * @return null表示未安装该App，PackageInfo.versionCode：版本号，versionName：版本名称
     */
    public static PackageInfo getAppInfo(@NonNull Context context, @NonNull String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);

        } catch (PackageManager.NameNotFoundException e) {
            KLog.e(e);
            return null;
        }
    }

    // TODO: 2018/1/3 --------------------------------------- 应用市场安装 ---------------------------------------

    /**
     * @param packageName 包名
     * @param webUrl      如果应用市场不可用，使用的下载网页地址。如果传空，则不论是否有应用市场，都去打开应用市场
     */
    public static void openAppMarket(@NonNull Context context, @NonNull String packageName, String webUrl) {

        // TODO 判断应用市场不可用的设备，目前还没确定不可调起应用市场的设备，先乱设置一个
        if (Build.BRAND.equals("8ieu8r4e348t8948")) {
            if (!KString.isBlank(webUrl)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(webUrl));
                context.startActivity(intent);
                return;
            }
        }
        openAppMarket(context, packageName);
    }

    public static void openAppMarket(@NonNull Context context, @NonNull String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(intent);
    }

    // TODO: 2018/1/3 --------------------------------------- 卸载apk ---------------------------------------


    /**
     * 卸载apk
     *
     * @param path      文件路径
     * @param authority android 7.0 共享文件请求权限的 authority，需要配置manifests文件
     */
    public static void unInstallApk(@NonNull Context context, @NonNull String path, String authority) {
        unInstallApk(context, new File(path), authority);
    }

    public static void unInstallApk(@NonNull Context context, File file, String authority) {
        Uri apkUri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            apkUri = Uri.fromFile(file);
        } else {
            apkUri = FileProvider.getUriForFile(context, authority, file);
        }
        unInstallApk(context, apkUri);
    }

    /**
     * 卸载apk
     */
    public static void unInstallApk(@NonNull Context context, Uri uri) {
        Intent mIntent = new Intent(Intent.ACTION_DELETE, uri);
        /*mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        mIntent.setDataAndType(uri, "application/vnd.android.package-archive");*/
        context.startActivity(mIntent);
    }

    /**
     * 卸载apk
     *
     * @param packageName 包名
     */
    public static void unInstallApk(@NonNull Context context, @NonNull String packageName) {
        unInstallApk(context, Uri.fromParts("package", packageName, null));
    }
}
