package com.kiven.kutils.file;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KContext;
import com.kiven.kutils.tools.KString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件相关工具
 * Created by kiven on 2016/11/4.
 */

public class KFile {
    public static final String defaultDocDirName = "doc";
    public static final String defaultImageDirName = "images";
    public static final String defaultCacheDirName = "cache";
    public static final String defaultDownloadDirName = "download";

    public static String appDirName = null;
    public static String docDirName = null;
    public static String imageDirName = null;
    public static String cacheDirName = null;
    public static String downloadDirName = null;

    /**
     * 创建临时文件
     */
    public static File createTempFile(String prefix, String suffix, File directory) {
        File tmpFile;
        try {
            tmpFile = File.createTempFile(prefix, suffix, directory);
        } catch (IOException e) {
            KLog.e(e);
            tmpFile = getAppFileFolderPath(KString.isBlank(cacheDirName)? defaultCacheDirName: cacheDirName);
        }
        return tmpFile;
    }
    /**
     * 应用图片保存路径
     *
     * @return
     */
    public static File getAppPictureFolderPath() {
        return getAppFileFolderPath(KString.isBlank(imageDirName)? defaultImageDirName: imageDirName);
    }

    /**
     * 应用文件保存路径
     *
     * @return
     */
    public static File getAppDocFolderPath() {
        return getAppFileFolderPath(KString.isBlank(docDirName)? defaultDocDirName: docDirName);
    }

    /**
     * 文件夹路径
     *
     * @param packageName 设置的文件夹名称
     */
    public static File getAppFileFolderPath(String packageName) {

        File folder = new File(Environment.getExternalStorageDirectory() + "/SXB_FILES/" + packageName);

        if ((!folder.exists()) && !folder.mkdirs()) {
            folder = new File(KContext.getInstance().getFilesDir(), packageName);
        }
        
        if ((!folder.exists()) && !folder.mkdirs()) {
            return null;
        }
        return folder;
    }
    public static String[] getStoragePaths(Context cxt) {
        List<String> pathsList = new ArrayList<String>();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
            /*StringBuilder sb = new StringBuilder();
            try {
                pathsList.addAll(new SdCardFetcher().getStoragePaths(new FileReader("/proc/mounts"), sb));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                File externalFolder = Environment.getExternalStorageDirectory();
                if (externalFolder != null) {
                    pathsList.add(externalFolder.getAbsolutePath());
                }
            }*/
        } else {
            StorageManager storageManager = (StorageManager) cxt.getSystemService(Context.STORAGE_SERVICE);
            try {
                Method method = StorageManager.class.getDeclaredMethod("getVolumePaths");
                method.setAccessible(true);
                Object result = method.invoke(storageManager);
                if (result != null && result instanceof String[]) {
                    String[] pathes = (String[]) result;
                    StatFs statFs;
                    for (String path : pathes) {
                        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                            statFs = new StatFs(path);
                            if (statFs.getBlockCount() * statFs.getBlockSize() != 0) {
                                pathsList.add(path);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                File externalFolder = Environment.getExternalStorageDirectory();
                if (externalFolder != null) {
                    pathsList.add(externalFolder.getAbsolutePath());
                }
            }
        }
        return pathsList.toArray(new String[pathsList.size()]);
    }

    // TODO -----------------文件判断-----------------

    public enum FileType{
        UNKNOWN,JPG,PNG,GIF
    }

    /**
     * 获取文件类型
     * 参考文档：
     *      android、java中判断图片文件的格式：http://blog.csdn.net/kehengqun1/article/details/49252549
     *      通过文件头标识判断图片格式：http://zjf30366.blog.163.com/blog/static/41116458201042194542973/
     *      gif 格式图片详细解析：http://blog.csdn.net/wzy198852/article/details/17266507
     *      JPG文件结构分析：http://blog.csdn.net/hnllei/article/details/6972858
     */
    public FileType checkFileType(File file) {
        FileType fileType = FileType.UNKNOWN;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int[] flags = new int[5];
            flags[0] = inputStream.read();
            flags[1] = inputStream.read();
            if (flags[0] == 255 && flags[1] == 216) {// JPG检测
                inputStream.skip(inputStream.available() - 2);
                flags[2] = inputStream.read();
                flags[3] = inputStream.read();
                if (flags[2] == 255 && flags[3] == 217) {
                    fileType = FileType.JPG;
                }
            } else if (flags[0] == 71 && flags[1] == 73){// GIF
                flags[2] = inputStream.read();
                flags[3] = inputStream.read();
                inputStream.skip(inputStream.available() - 1);
                flags[4] = inputStream.read();
                if (flags[2] == 70 && flags[3] == 56 && flags[4] == 0x3B) {
                    fileType = FileType.GIF;
                }
            } else if (flags[0] == 0x89 && flags[1] == 0x50) {// PNG
                flags[2] = inputStream.read();
                flags[3] = inputStream.read();
                flags[4] = inputStream.read();
                flags[5] = inputStream.read();
                flags[6] = inputStream.read();
                flags[7] = inputStream.read();
                if (flags[2] == 0x4e && flags[3] == 0x47 && flags[4] == 0x0d && flags[5] == 0x0a && flags[6] == 0x1a && flags[7] == 0x0a) {
                    fileType = FileType.PNG;
                }
            }

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileType;
    }
}
