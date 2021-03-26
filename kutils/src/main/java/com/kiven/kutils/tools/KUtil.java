package com.kiven.kutils.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.kiven.kutils.logHelper.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Kiven on 2014/12/24.
 */
public class KUtil {

    public static class Config {
        private boolean isDebug = true;
        private String imageDirName = "kUtilsImage";
        private String fileDirName = "kUtilsFile";
        private String tag = "kutils";
        private String CONFIG_SHARED_PREFERENCES = "KContext.ACCOUNT_CONFIG";

        public void setDebug(boolean debug) {
            isDebug = debug;
        }

        public boolean isDebug() {
            return isDebug;
        }

        public void setImageDirName(String imageDirName) {
            this.imageDirName = imageDirName;
        }

        public void setFileDirName(String fileDirName) {
            this.fileDirName = fileDirName;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public void setConfigSharedPreferences(@NonNull String configSharedPreferences) {
            if (!configSharedPreferences.isEmpty()) CONFIG_SHARED_PREFERENCES = configSharedPreferences;
        }
    }

    private static Application app;
    private static Config config = new Config();
    public static void init(@NonNull Application app, Config config) {
        KUtil.app = app;
        if (config != null) KUtil.config = config;
    }

    public static void setApp(Application app) {
        KUtil.app = app;
    }

    public static Application getApp() {
        return app;
    }

    public static Config getConfig() {
        return config;
    }
    /*private static String CONFIG_SHARED_PREFERENCES = "KContext.ACCOUNT_CONFIG";

    public static void setConfigSharedPreferences(@NonNull String configSharedPreferences) {
        CONFIG_SHARED_PREFERENCES = configSharedPreferences;
    }


    private static String imageDirName = "kUtilsImage";

    public static void setImageDirName(@NonNull String dirName) {
        imageDirName = dirName;
    }


    private static String fileDirName = "kUtilsFile";

    public static void setFileDirName(@NonNull String dirName) {
        fileDirName = dirName;
    }

    private static String tag = "kutils";

    public static void setTag(@NonNull String tag) {
        KUtil.tag = tag;
    }*/

    /**
     * 像素转化
     */
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
    public static int getScreenWith() {
        DisplayMetrics metric = app.getResources().getDisplayMetrics();
        return metric.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metric = app.getResources().getDisplayMetrics();
        return metric.heightPixels;
    }

    /**
     * 屏幕密度（0.75 / 1.0 / 1.5）
     */
    public static float getScreenDensity() {
        DisplayMetrics metric = app.getResources().getDisplayMetrics();
        return metric.density;
    }

    /**
     * 屏幕密度DPI（120 / 160 / 240）
     */
    public static int getScreenDensityDpi() {
        DisplayMetrics metric = app.getResources().getDisplayMetrics();
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) info.getLongVersionCode();
            } else return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 是否是模拟器
     * 根据abi判断，并不完整
     */
    public static boolean isAVD() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi: Build.SUPPORTED_ABIS) {
                if (abi.toLowerCase().contains("x86")) return true;
            }
        } else {
            if (Build.CPU_ABI != null && Build.CPU_ABI.toLowerCase().contains("x86")) {
                return true;
            }

            return Build.CPU_ABI2 != null && Build.CPU_ABI2.toLowerCase().contains("x86");
        }

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
                editor.apply();
//			uuId = JPushInterface.getUdid(KContext.getInstance());
            }
        }

        return uuId;
    }

    /**
     * 应用图片保存路径
     */
    public static String getAppPictureFolderPath() {
        return getAppFileFolderPath(config.imageDirName);
    }

    public static Uri createNewAppPictureUri() {
        return createNewAppPictureUri("", config.imageDirName, false);
    }

    public static Uri createNewAppPictureUri(boolean saveToPng) {
        return createNewAppPictureUri("", config.imageDirName, saveToPng);
    }

    public static Uri createNewAppPictureUri(@NonNull String displayName, boolean saveToPng) {
        return createNewAppPictureUri(displayName, config.imageDirName, saveToPng);
    }

    public static Uri createNewAppPictureUri(@NonNull String displayName, @NonNull String folderName, boolean saveToPng) {

        // 1 构建 ContentValues

        ContentValues values = new ContentValues();
        String fullDisplayName = displayName;
        if (displayName.isEmpty()) {
            fullDisplayName = config.tag + System.currentTimeMillis();
        }
        if (saveToPng) {
            fullDisplayName += ".png";
        }else {
            fullDisplayName += ".jpg";
        }

        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fullDisplayName);
        if (saveToPng) {
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        }else {
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        }

        String time = "" + System.currentTimeMillis() / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);

        String dir = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!TextUtils.isEmpty(folderName)) {
                dir = File.separator + folderName;
            }
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + dir);
        } else {
            // android 10 以前使用原来的方式
            if (!TextUtils.isEmpty(folderName)) {
                dir = folderName + File.separator;
            }

            String abPath = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_PICTURES + "/" + dir;
            if (!TextUtils.isEmpty(dir)/* && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M*/) {
                // 有些手机不会自动创建目录，所以要手动创建
                File imgFile = new File(abPath);
                if (!imgFile.exists()) imgFile.mkdir();
            }
            values.put(MediaStore.MediaColumns.DATA, abPath + fullDisplayName);
        }

        // 2 构建uri

        try {
            ContentResolver contentResolver = app.getContentResolver();
            return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            KLog.e(e);
            return null;
        }
    }

    public static Uri saveImage(@NonNull Bitmap bitmap, boolean saveToPng) {
        try {
            Uri outUri = createNewAppPictureUri(saveToPng);
            ParcelFileDescriptor descriptor = app.getContentResolver().openFileDescriptor(outUri, "w");

            if (descriptor != null) {
                FileOutputStream fos = new FileOutputStream(descriptor.getFileDescriptor());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();

                return outUri;
            }
        } catch (Exception e) {
            KLog.e(e);
        }
        return null;
    }

    /**
     * 应用文件保存路径
     */
    public static String getAppFileFolderPath() {
        return getAppFileFolderPath(config.fileDirName);
    }

    /**
     * 文件路径
     */
    public static String getAppFileFolderPath(String packageName) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + packageName);
        folder.mkdirs();
        return folder.getAbsolutePath();
    }

    /**
     * 通知相册更新图片
     *
     * @param callBack 跟新成功是否提示, 0:失败，1：成功，2：保存中
     *                 <p>
     *                 Android Q 访问存储权限变动，该方法可能不再适用。
     *                 可以使用{@link KFile#saveJpgBitmap(Context, Bitmap, String, String)}
     *                 或者{@link KFile#savePngBitmap(Context, Bitmap, String, String)}
     */
    public static void addPicture(String path, MediaScannerConnection.OnScanCompletedListener callBack) {
        // TODO - 在外部存储的应用私有文件夹下面的文件，是没办法放到相册里面的
        // todo - 如果图库中已经存在该图片，如果在次调用该方法通知图库，可能会出现异常。
        // todo 目前遇到的是：华为荣耀10(系统 android 9.1) ,图片在图库中消失，实际还存在，只是在图库中没有
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
        return KContext.getInstance().getSharedPreferences(config.CONFIG_SHARED_PREFERENCES, Context.MODE_PRIVATE);
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

    public static Set<String> getSharedPreferencesStringSet(String key, Set<String> defaultValue) {
        return getSharedPreferences().getStringSet(key, defaultValue);
    }

    public static void putSharedPreferencesStringSet(String key, Set<String> value) {
        Editor editor = getSharedPreferences().edit();
        editor.putStringSet(key, value);
        editor.apply();
    }
}
