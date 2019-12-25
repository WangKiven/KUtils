package com.kiven.kutils.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.kiven.kutils.logHelper.KLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Kiven on 2014/12/24.
 */
public class KUtil {

    private static Application app;
    private static String CONFIG_SHARED_PREFERENCES = "KContext.ACCOUNT_CONFIG";

    public static void setApp(Application app) {
        KUtil.app = app;
    }
    public static Application getApp() {
        return app;
    }

    public static void setConfigSharedPreferences(@NonNull String configSharedPreferences) {
        CONFIG_SHARED_PREFERENCES = configSharedPreferences;
    }


    private static String imageDirName = "SXB_IMAGES";

    public static void setImageDirName(@NonNull String dirName) {
        imageDirName = dirName;
    }


    private static String fileDirName = "SXB_FILES";

    public static void setFileDirName(@NonNull String dirName) {
        fileDirName = dirName;
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
     * 根据 @param context 获取Activity
     */
    public static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof Activity) {
            return (Activity) context;
        } else {
            return context instanceof ContextWrapper ? getActivity(((ContextWrapper) context).getBaseContext()) : null;
        }
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

    /**
     * 是否是模拟器
     *
     * @return
     */
    public static boolean isAVD() {
        /*String serial;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serial = Build.getSerial();
        } else {
            serial = Build.SERIAL;
        }
        return TextUtils.equals(serial, "unknown");*/
        return false;
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
                        Build.BRAND.length() % 10 +//品牌
                        Build.CPU_ABI.length() % 10 +
                        Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 +
                        Build.HOST.length() % 10 +
                        Build.ID.length() % 10 +
                        Build.MANUFACTURER.length() % 10 +//制造商
                        Build.MODEL.length() % 10 +//型号
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
     * 应用图片保存路径
     */
    public static String getAppPictureFolderPath() {
        return getAppFileFolderPath(imageDirName);
    }

    /**
     * 应用文件保存路径
     */
    public static String getAppFileFolderPath() {
        return getAppFileFolderPath(fileDirName);
    }

    /**
     * 文件路径
     */
    public static String getAppFileFolderPath(String packageName) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + packageName);
        folder.mkdirs();
        return folder.getAbsolutePath();
        // return folder;
    }

    /**
     * 读取纯文本文件
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
     *                 <p>
     *                 Android Q 访问存储权限变动，该方法可能不再适用。
     *                 可以使用{@link com.kiven.kutils.file.KFile#saveJpgBitmap(Context, Bitmap, String, String)}
     *                 或者{@link com.kiven.kutils.file.KFile#savePngBitmap(Context, Bitmap, String, String)}
     */
    public static void addPicture(String path, MediaScannerConnection.OnScanCompletedListener callBack) {
        // TODO - 在外部存储的应用私有文件夹下面的文件，是没办法放到相册里面的
        // todo - 如果图库中已经存在该图片，如果在次调用该方法通知图库，可能会出现异常。
        // todo 目前遇到的是：华为荣耀10(系统 android 9.1) ,图片在图库中消失，实际还存在，只是在图库中没有
        /*MediaScannerConnection.scanFile(KContext.getInstance(), new String[]{path}, new String[]{"image/*"},
                callBack);*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            app.sendBroadcast(
                    new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(path))
                    )
            );
            if (callBack != null)
                callBack.onScanCompleted(path, null);
        } else {
            MediaScannerConnection.scanFile(KContext.getInstance(), new String[]{path}, new String[]{"image/*"},
                    callBack);
        }

        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(KContext.getInstance(), new String[]{path}, new String[]{"image/*"},
                    callBack);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            app.sendBroadcast(
                    new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(path))
                    )
            );
            callBack.onScanCompleted(path, null);
        } else {
            try {
                MediaStore.Images.Media.insertImage(app.getContentResolver(), path, new File(path).getName(), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//            ExifInterface

//                    MediaStore.getMediaScannerUri()

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, path);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
            app.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

//            String where=MediaStore.Audio.Media.DATA+" like \""+path+"%"+"\"";
//            app.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, null);







            callBack.onScanCompleted(path, null);
        }*/
    }

    public static void addPicture(@NonNull String[] paths) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            for (String path : paths) {
                app.sendBroadcast(
                        new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(new File(path))
                        )
                );
            }
        } else {
            MediaScannerConnection.scanFile(KContext.getInstance(), paths, new String[]{"image/*"},
                    null);
        }
    }

    public static void addVideo(String path, MediaScannerConnection.OnScanCompletedListener callBack) {
        MediaScannerConnection.scanFile(KContext.getInstance(), new String[]{path}, new String[]{"video/*"},
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

    // todo -------------------------- Overlay ----------------------------
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean canDrawOverlays() {
        return Settings.canDrawOverlays(app);
    }

    /**
     * 设置可显示悬浮界面
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void startOverlaySetting() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + app.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        app.startActivity(intent);
    }

    // todo -------------------------- service ----------------------------

    public static boolean isRun(@NonNull Class serviceClass) {
        String serviceName = serviceClass.getName();

        ActivityManager myManager = (ActivityManager) app
                .getSystemService(Context.ACTIVITY_SERVICE);

        if (myManager != null) {
            ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                    .getRunningServices(30);
            for (int i = 0; i < runningService.size(); i++) {
                if (runningService.get(i).service.getClassName().equals(serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void startService(@NonNull Class serviceClass) {
        Intent ii = new Intent(app, serviceClass);
        app.startService(ii);
    }

    public static void stopService(@NonNull Class serviceClass) {
        Intent ii = new Intent(app, serviceClass);
        app.stopService(ii);
    }

    // todo -------------------------- SharedPreferences ----------------------------

    /**
     * SharedPreferences
     */
    public static SharedPreferences getSharedPreferences() {
        return KContext.getInstance().getSharedPreferences(CONFIG_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void removeSharedPreferencesValue(String key) {
        Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        editor.apply();
    }

    public static int getSharedPreferencesIntValue(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    public static void putSharedPreferencesIntValue(String key, int value) {
        Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getSharedPreferencesStringValue(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    public static void putSharedPreferencesStringValue(String key, String value) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static boolean getSharedPreferencesBooleanValue(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    public static void putSharedPreferencesBooleanValue(String key, boolean value) {
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static long getSharedPreferencesLongValue(String key, long defaultValue) {
        return getSharedPreferences().getLong(key, defaultValue);
    }

    public static void putSharedPreferencesLongValue(String key, long value) {
        Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.apply();
    }
}
