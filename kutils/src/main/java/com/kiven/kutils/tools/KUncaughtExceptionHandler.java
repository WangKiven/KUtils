package com.kiven.kutils.tools;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kiven.kutils.file.KFile;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

class KUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final KUncaughtExceptionHandler ourInstance = new KUncaughtExceptionHandler();

    static KUncaughtExceptionHandler getInstance() {
        return ourInstance;
    }

    Thread.UncaughtExceptionHandler oldUEHandler;

    public void register() {

        Thread.UncaughtExceptionHandler t = Thread.getDefaultUncaughtExceptionHandler();
        if (t == this) return;// 已经设置好，不用继续下去了

        oldUEHandler = t;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private boolean hasRun = false;
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        if (hasRun) return;// 由于KContext中次调用register(),防止导致循环运行该方法。因为其他程序有可能也要拦截崩溃异常
        hasRun = true;

        Application app = KUtil.getApp();
        if (app == null) {
            Log.e("KLog_default", "Application 获取失败");
            return;
        }

        String text = "程序出现异常：" + t.getName() + " - " + e.getMessage();
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            text = text + "\r\n" + sw.toString() + "\r\n";
            pw.close();
            sw.close();
        } catch (Exception e2) {
            Log.e("KLog_default", text + ", 解析异常：" + e2.getMessage());
        }

        File f = KFile.createNameFile("Kutils记录的崩溃异常"+ DateFormat.getDateTimeInstance().format(new Date()) + ".txt", app.getCacheDir());
        if (f != null) {
            KFile.saveFile(f, text.getBytes());
        }


        if (oldUEHandler != null) {
            Log.e("KLog_default", oldUEHandler.getClass().getName());
            oldUEHandler.uncaughtException(t, e);
        }
    }
}
