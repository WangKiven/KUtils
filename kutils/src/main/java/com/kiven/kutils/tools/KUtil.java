package com.kiven.kutils.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.kiven.kutils.callBack.Consumer;
import com.kiven.kutils.logHelper.KLog;

import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * Created by Kiven on 2014/12/24.
 */
public class KUtil {
	/**
	 * 像素转化
	 *
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 屏幕宽高
	 *
	 * @param context
	 * @return
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
	 *
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		return metric.density;
	}

	/**
	 * 屏幕密度DPI（120 / 160 / 240）
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenDensityDpi(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		return metric.densityDpi;
	}

	/**
	 * 获取版本号
	 *
	 * @return
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
	 *
	 * @return
	 */
	public static String getUUID() {
		if (uuId == null) {
			 SharedPreferences preferences = getSharedPreferences();
			 uuId = preferences.getString("Util_My_UUID", null);
			 if (uuId == null) {
			 uuId = System.currentTimeMillis() + "" +
			 Build.BOARD.length()%10 +
			 Build.BRAND.length()%10 +
			 Build.CPU_ABI.length()%10 +
			 Build.DEVICE.length()%10 +
			 Build.DISPLAY.length()%10 +
			 Build.HOST.length()%10 +
			 Build.ID.length()%10 +
			 Build.MANUFACTURER.length()%10 +
			 Build.MODEL.length()%10 +
			 Build.PRODUCT.length()%10 +
			 Build.TAGS.length()%10 +
			 Build.TYPE.length()%10 +
			 Build.USER.length()%10 ;
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
		KLog.i("Product Model: " + Build.MODEL + "," + Build.VERSION.SDK_INT + ","
				+ Build.VERSION.RELEASE);
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
	 * @param callBack
	 *            跟新成功是否提示, 0:失败，1：成功，2：保存中
	 */
	public static void addPicture(String path, final Consumer<Integer> callBack) {
		/*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {*/
			MediaScannerConnection.scanFile(KContext.getInstance(), new String[] { path }, new String[] { "image/*" },
					new OnScanCompletedListener() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							if (callBack != null) {
								x.task().post(new Runnable() {
									@Override
									public void run() {
										callBack.callBack(1);
									}
								});
							}
						}

					});
			if (callBack != null) {
				callBack.callBack(2);
			}
		/*} else {
			KContext.getInstance()
					.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));

			if (callBack != null) {
				callBack.callBack(1);
			}
		}*/
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param filePath
	 * @param isAll
	 *            如果是一个文件夹，true表示删除文件夹中文件后删除文件夹，false表示仅删除文件夹类容
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
	 * SharedPreferences
	 * 
	 * @return
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
	
	public static String getSharedPreferencesStringValue(String key, String defaultValue){
		return getSharedPreferences().getString(key, defaultValue);
	}
	
	public static void putSharedPreferencesStringValue(String key, String value){
		Editor editor = getSharedPreferences().edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static boolean getSharedPreferencesBooleanValue(String key, boolean defaultValue) {
		return getSharedPreferences().getBoolean(key, defaultValue);
	}
	
	public static void putSharedPreferencesBooleanValue(String key, boolean value){
		Editor editor = getSharedPreferences().edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}
