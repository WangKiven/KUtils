package com.kiven.kutils.file;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kiven.kutils.tools.KString;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件相关工具
 * Created by kiven on 2016/11/4.
 */

public class KFile {

    private static long frontTime = 0;
    private static int timeCount = 0;

    /**
     * 通过时间获取唯一标识
     */
    private static String getTimeTag() {
        long currTime = System.currentTimeMillis();
        if (currTime == frontTime) {
            timeCount++;
            return String.format("%d_%d", currTime, timeCount);
        } else {
            frontTime = currTime;
            return String.valueOf(currTime);
        }
    }

    /**
     * 外部目录是否可用
     */
    public static boolean isExternalStorageRemovable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable();
    }

    /**
     * 创建文件
     *
     * @param directory 在此目录下创建文件
     */
    public static File createFile(@NonNull File directory) {
        if ((!directory.exists()) && (!directory.mkdirs())) {
            return null;
        }
        return new File(directory, getTimeTag());
    }

    public static File createFile(@NonNull String suffix, @NonNull File directory) {
        if ((!directory.exists()) && (!directory.mkdirs())) {
            return null;
        }
        return new File(directory, getTimeTag() + suffix);
    }

    public static File createFile(@NonNull String prefix, @NonNull String suffix, @NonNull File directory) {
        if ((!directory.exists()) && (!directory.mkdirs())) {
            return null;
        }
        return new File(directory, prefix + "-" + getTimeTag() + suffix);
    }

    /**
     * 获取外部存储和sd卡路径
     */
    public static String[] getStoragePaths(@NonNull Context cxt) {
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

    public enum FileType {
        UNKNOWN, JPG, PNG, GIF
    }

    /**
     * 获取文件类型
     * 参考文档：
     * android、java中判断图片文件的格式：http://blog.csdn.net/kehengqun1/article/details/49252549
     * 通过文件头标识判断图片格式：http://zjf30366.blog.163.com/blog/static/41116458201042194542973/
     * gif 格式图片详细解析：http://blog.csdn.net/wzy198852/article/details/17266507
     * JPG文件结构分析：http://blog.csdn.net/hnllei/article/details/6972858
     */
    public static FileType checkFileType(@NonNull File file) {
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
            } else if (flags[0] == 71 && flags[1] == 73) {// GIF
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

    /**
     * 获取或创建文件后缀名
     */
    public static String getPrefix(String path) {
        if (KString.isBlank(path)) {
            return "";
        }
        File file = new File(path);
        String fileName = file.getName();
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            FileType fileType = checkFileType(file);
            switch (fileType) {
                case GIF:
                    return "gif";
                case JPG:
                    return "jpg";
                case PNG:
                    return "png";
                case UNKNOWN:
                default:
                    return "";
            }
        }
    }

    /**
     * Get the Mime Type from a File
     *
     * @param fileName 文件名
     * @return 返回MIME类型
     * thx https://www.oschina.net/question/571282_223549
     */
    private static String getMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(fileName);
        return type;
    }

    /**
     * 根据文件后缀名判断 文件是否是视频文件
     *
     * @param fileName 文件名
     * @return 是否是视频文件
     */
    public static boolean isVedioFile(String fileName) {
        String mimeType = getMimeType(fileName);
        if (!TextUtils.isEmpty(fileName) && mimeType.contains("video/")) {
            return true;
        }
        return false;
    }
}
