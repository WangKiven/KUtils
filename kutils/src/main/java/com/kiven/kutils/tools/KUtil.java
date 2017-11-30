package com.kiven.kutils.tools;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;

import com.kiven.kutils.logHelper.KLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Kiven on 2014/12/24.
 */
public class KUtil {

    private static Application app;

    public static void setApp(Application app) {
        KUtil.app = app;
    }

    /**
     * 像素转化
     */
    @Deprecated
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Deprecated
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Deprecated
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    @Deprecated
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dip2px(float dipValue) {
        final float scale = app.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = app.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = app.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = app.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 屏幕宽高
     */
    public static int getScreenWith(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        return metric.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        return metric.heightPixels;
    }

    /**
     * 屏幕密度（0.75 / 1.0 / 1.5）
     */
    public static float getScreenDensity(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        return metric.density;
    }

    /**
     * 屏幕密度DPI（120 / 160 / 240）
     */
    public static int getScreenDensityDpi(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        return metric.densityDpi;
    }

    /**
     * 获取版本号
     */
    public static String getVersion() {
        Context context = KContext.getInstance();

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode() {
        Context context = KContext.getInstance();
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static String uuId = null;

    /**
     * 获取自己组合的UUID
     */
    public static String getUUID() {
        if (uuId == null) {
            SharedPreferences preferences = getSharedPreferences();
            uuId = preferences.getString("Util_My_UUID", null);
            if (uuId == null) {
                uuId = System.currentTimeMillis() + "" +
                        Build.BOARD.length() % 10 +
                        Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 +
                        Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 +
                        Build.HOST.length() % 10 +
                        Build.ID.length() % 10 +
                        Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 +
                        Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 +
                        Build.TYPE.length() % 10 +
                        Build.USER.length() % 10;
                //b785756b5c4cbbce5c2d6c781d5099bd
                Editor editor = preferences.edit();
                editor.putString("Util_My_UUID", uuId);
                editor.commit();
//			uuId = JPushInterface.getUdid(KContext.getInstance());
            }
        }

        return uuId;
    }

    /**
     * 打印设备信息
     */
    public static void printDeviceInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("屏幕密度（0.75 / 1.0 / 1.5）:").append(getScreenDensity(app)).append("\n屏幕密度DPI（120 / 160 / 240）:")
                .append(getScreenDensityDpi(app)).append("\n屏幕宽度(px):").append(getScreenWith(app)).append("\n屏幕高度(px):").append(getScreenHeight(app));
        builder.append("\nProduct Model: ").append(Build.BRAND).append(",").append(Build.MODEL).append(",")
                .append(Build.VERSION.SDK_INT).append(",").append(Build.VERSION.RELEASE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.append("\ncpu_abis = ").append(Arrays.toString(Build.SUPPORTED_ABIS));
        } else {
            builder.append("\ncpu_abis = ").append(Build.CPU_ABI).append(", ").append(Build.CPU_ABI2);
        }

        Properties properties = System.getProperties();
        Set<String> set = System.getProperties().stringPropertyNames(); //获取java虚拟机和系统的信息。

        builder.append("\n>>>>>>>>>>system properties");
        for (String name : set) {
            builder.append("\n").append(name).append(":\t").append(properties.getProperty(name));
        }

        KLog.i(new String(builder));
    }

    /**
     * 应用图片保存路径
     *
     * @return
     */
    public static String getAppPictureFolderPath() {
        return getAppFileFolderPath("SXB_IMAGES");
    }

    /**
     * 应用文件保存路径
     *
     * @return
     */
    public static String getAppFileFolderPath() {
        return getAppFileFolderPath("SXB_FILES");
    }

    /**
     * 文件路径
     *
     * @param packageName
     * @return
     */
    public static String getAppFileFolderPath(String packageName) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + packageName);
        folder.mkdir();
        return folder.getAbsolutePath();
        // return folder;
    }

    /**
     * 读取纯文本文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readFile(String filePath) throws IOException {
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;

        InputStream inputStream = new FileInputStream(new File(filePath));
        inputReader = new InputStreamReader(inputStream);
        bufferReader = new BufferedReader(inputReader);

        String line = null;
        StringBuffer strBuffer = new StringBuffer();
        while ((line = bufferReader.readLine()) != null) {
            strBuffer.append(line);
        }
        bufferReader.close();

        return new String(strBuffer);
    }

    /**
     * 通知相册更新图片
     *
     * @param callBack 跟新成功是否提示, 0:失败，1：成功，2：保存中
     */
    public static void addPicture(String path, MediaScannerConnection.OnScanCompletedListener callBack) {
        MediaScannerConnection.scanFile(KContext.getInstance(), new String[]{path}, new String[]{"image/*"},
                callBack);
    }

    /**
     * 删除文件或文件夹
     *
     * @param filePath
     * @param isAll    如果是一个文件夹，true表示删除文件夹中文件后删除文件夹，false表示仅删除文件夹类容
     */
    public static void deleteFile(String filePath, boolean isAll) {
        if (filePath == null || filePath.length() == 0) {
            return;
        }
        File file = new File(filePath);
        deleteFile(file, isAll);
    }

    public static void deleteFile(File file, boolean isAll) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i], true);
            }
            if (isAll) {
                file.delete();
            }
        }
    }

    /**
     * 安装apk
     */
    private static void installApk(Context context, Uri uri) {
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
     * 卸载apk
     */
    private static void unInstallApk(Context context, Uri uri) {
        Intent mIntent = new Intent(Intent.ACTION_DELETE, uri);
        /*mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        mIntent.setDataAndType(uri, "application/vnd.android.package-archive");*/
        context.startActivity(mIntent);
    }

    /**
     * 卸载apk
     *
     * @param path      文件路径
     * @param authority android 7.0 共享文件请求权限的 authority，需要配置manifests文件
     */
    public static void unInstallApk(Context context, String path, String authority) {
        unInstallApk(context, new File(path), authority);
    }

    public static void unInstallApk(Context context, File file, String authority) {
        Uri apkUri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            apkUri = Uri.fromFile(file);
        } else {
            apkUri = FileProvider.getUriForFile(context, authority, file);
        }
        unInstallApk(context, apkUri);
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

    /**
     * SharedPreferences
     */
    public static SharedPreferences getSharedPreferences() {
        return KContext.getInstance().getSharedPreferences("KContext.ACCOUNT_CONFIG", Context.MODE_PRIVATE);
    }

    public static int getSharedPreferencesIntValue(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    public static void putSharedPreferencesIntValue(String key, int value) {
        Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getSharedPreferencesStringValue(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    public static void putSharedPreferencesStringValue(String key, String value) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean getSharedPreferencesBooleanValue(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    public static void putSharedPreferencesBooleanValue(String key, boolean value) {
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static long getSharedPreferencesLongValue(String key, long defaultValue) {
        return getSharedPreferences().getLong(key, defaultValue);
    }

    public static void putSharedPreferencesLongValue(String key, long value) {
        Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
