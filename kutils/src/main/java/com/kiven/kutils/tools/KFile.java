package com.kiven.kutils.tools;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kiven.kutils.logHelper.KLog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    public static File createNameFile(@NonNull String fileName, @NonNull File directory) {
        if ((!directory.exists()) && (!directory.mkdirs())) {
            return null;
        }
        return new File(directory, fileName);
    }

    /**
     * 获取外部存储和sd卡路径
     */
    public static String[] getStoragePaths(@NonNull Context cxt) {
        List<String> pathsList = new ArrayList<String>();

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
            KLog.e(e);
            File externalFolder = Environment.getExternalStorageDirectory();
            if (externalFolder != null) {
                pathsList.add(externalFolder.getAbsolutePath());
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
            int[] flags = new int[8];
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

//            StringBuilder sb = new StringBuilder(file.getName() + ":");
//            for(int i: flags) {
//                sb.append(" ").append(i);
//            }
//            KLog.i(sb.toString());

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
    @Nullable
    private static String getMimeType(String fileName) {
        return URLConnection.getFileNameMap().getContentTypeFor(fileName);
    }

    /**
     * 根据文件后缀名判断 文件是否是视频文件
     *
     * @param fileName 文件名
     * @return 是否是视频文件
     */
    public static boolean isVedioFile(String fileName) {
        String mimeType = getMimeType(fileName);
        return !TextUtils.isEmpty(fileName) && mimeType != null && mimeType.contains("video/");
    }

    /**
     * 保存文件
     */
    public static boolean saveFile(@NonNull File file, @NonNull byte[] data) {
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
            fileOutputStream.close();
            /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();*/
        } catch (Exception e) {
            KLog.e(e);
            return false;
        }

        return true;
    }

    /**
     * 名为保存，实际是复制
     *
     * @param file 保存的目标地址
     * @param data KUtil.getApp().contentResolver.openFileDescriptor(uri, "r")!!.fileDescriptor
     * @return 是否保存成功
     */
    public static boolean saveFile(@NonNull File file, @NonNull FileDescriptor data) {
        if (file.exists()) {
            file.delete();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(data);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buff = new byte[1024];

            int readLength = fileInputStream.read(buff);
            while (readLength > -1) {
                fileOutputStream.write(buff, 0, readLength);
                readLength = fileInputStream.read(buff);
            }

            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            KLog.e(e);
            return false;
        }

        return true;
    }

    /**
     * 读取文件
     */
    public static byte[] readFileByte(@NonNull File file) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buf = new byte[fileInputStream.available()];
        fileInputStream.read(buf);
        fileInputStream.close();
        return buf;
    }

    /**
     * 读取纯文本文件
     */
    public static String readFile(String filePath) {
        return readFile(new File(filePath));
    }

    public static String readFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return readFile(file, StandardCharsets.UTF_8);
        } else {
            return readFile(file, Charset.forName("UTF-8"));
        }
    }

    public static String readFile(String filePath, Charset charset) {
        return readFile(new File(filePath), charset);
    }

    public static String readFile(File file, Charset charset) {
        /*InputStreamReader inputReader = null;
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

        return new String(strBuffer);*/
        if (file.exists() && file.isFile() && file.length() > 0) {
            try {
                FileInputStream inputStream = new FileInputStream(file);

                int l = inputStream.available();
                byte[] bytes = new byte[l];

                int r = inputStream.read(bytes);
                KLog.i("文件长度：" + file.length() + ", 数据流长度：" + l + ", 读取数据返回值：" + r);
                inputStream.close();

                return new String(bytes, charset);
            } catch (Exception e) {
                KLog.e(e);
            }
        }
        return "";
    }

    /**
     * 保存后，如果想在图库中查看，需要通知图库刷新，调用方法：{@link com.kiven.kutils.tools.KUtil#addPicture(String, MediaScannerConnection.OnScanCompletedListener)}
     * 或者使用方法将图片插入图库中：{@link MediaStore.Images.Media#insertImage(ContentResolver, String, String, String)}
     */
    public static boolean saveJpgBitmap(@NonNull File file, @NonNull Bitmap bitmap) {
        if (file.exists()) {
            file.delete();
        }

        boolean b = false;
        try {
            FileOutputStream outputStream = new FileOutputStream(file);

            b = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            KLog.e(e);
        }

        return b;
    }

    /**
     * 保存图片到图库, 一般在手机存储卡根目录下Pictures文件内。
     * 保存后，可以直接打开相册查看。不用通知图库刷新
     *
     * @deprecated 建议使用{@link KUtil#saveImage(Bitmap, boolean)}，保存到图库下的默认文件夹。默认文件夹可以使用{@link KUtil#setImageDirName(String)}设置
     */
    @Deprecated
    public static String saveJpgBitmap(@NonNull Context context, @NonNull Bitmap bitmap, String title, String detail) {
        String fileName = KString.isBlank(title) ? getTimeTag() : title;
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    bitmap, fileName, detail);
        } catch (Exception e) {
            KLog.e(e);
            return null;
        }

        return fileName;
    }

    @Deprecated
    public static String savePngBitmap(@NonNull Context context, @NonNull Bitmap source, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri url = null;
        String stringUrl = null;
        ContentResolver cr = context.getContentResolver();

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            OutputStream imageOut = cr.openOutputStream(url);
            try {
                source.compress(Bitmap.CompressFormat.PNG, 50, imageOut);
            } finally {
                imageOut.close();
            }
        } catch (Exception e) {
//            Log.e(TAG, "Failed to insert image", e);
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }
}
