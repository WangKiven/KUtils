package com.kiven.kutils.logHelper;

import android.util.Log;

import com.kiven.kutils.tools.KString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

/**
 * 不建议直接使用, 建议在项目中重写
 * Created by Kiven on 2014/12/12.
 */
@Deprecated
public class KLog {

    private static KLog myLog;

    private static boolean showAtLine = true;

    private static final LinkedList<KLogInfo> logs = new LinkedList<KLogInfo>();

    private static boolean isDebug = true;

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setDebug(boolean isDebug) {
        KLog.isDebug = isDebug;
    }

    /**
     * 日志记录操作
     *
     * @param log log
     */
    protected static void addLog(String log) {

        synchronized (logs) {
            if (logs.size() > 200) {
                logs.removeLast();
                logs.removeLast();
            }

            StackTraceElement[] sts = Thread.currentThread().getStackTrace();
            String po = "";
            if (sts != null) {
                int i = 0;
                for (StackTraceElement st : sts) {
                    i++;
                    if (i > 10) {
                        break;
                    }
                    if (i == 1) {
                        po += (st.getClassName() + "." + st.getMethodName() + "("
                                + st.getFileName() + ":" + st.getLineNumber() + ")");
                    } else {
                        po += (", " + st.getClassName() + "." + st.getMethodName() + "("
                                + st.getFileName() + ":" + st.getLineNumber() + ")");
                    }
                }
            }
            logs.addFirst(new KLogInfo(po, log));
        }
    }

    public static LinkedList<KLogInfo> getLogs() {
        return logs;
    }

    /**
     * 单例ULog
     *
     * @return 获取到的单例
     */
    public static KLog getInstans() {

        if (myLog != null) {

            return myLog;
        } else {

            myLog = new KLog();

        }

        return myLog;

    }

    /**
     * 是否显示在哪一行
     *
     * @param b 是否显示
     */
    public static void setShowAtLine(boolean b) {
        showAtLine = b;
    }

    /**
     * 获取代码位置
     *
     * @return 代码位置
     */
    public static String findLog() {

        if (!showAtLine) {
            return "";
        }

        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {

                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {

                continue;
            }
            if (st.getClassName().endsWith(getInstans().getClass().getName())) {

                continue;
            }
            return " at " + st.getClassName() + "." + st.getMethodName() + "("
                    + st.getFileName() + ":" + st.getLineNumber() + ")";
        }
        return "";

    }

    private static String tag = "KLog_default";
    public static void setTag(String newTag){
        if (KString.isBlank(newTag)) return;
        tag = newTag;
    }

    public static void d(String debugInfo) {
        if (isDebug()) {
            Log.d(tag, debugInfo + findLog());
            addLog(debugInfo);
        }
    }

    public static void e(String errorInfo) {
        if (isDebug()) {
            Log.e(tag, errorInfo + findLog());
            addLog(errorInfo);
        }
    }

    public static void v(String msg) {
        if (isDebug()) {
            Log.v(tag, msg + findLog());
            addLog(msg);
        }
    }

    public static void i(String msg) {
        if (isDebug()) {
            Log.i(tag, msg + findLog());
            addLog(msg);
        }
    }

    public static void w(String msg) {
        if (isDebug()) {
            Log.w(tag, msg + findLog());
            addLog(msg);
        }
    }

    //打印异常
    public static void e(Throwable e) {
        if (isDebug()) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                e("\r\n" + sw.toString() + "\r\n");
            } catch (Exception e2) {
                e("fail getErrorInfoFromException");
            }
        }
    }

    /**
     * 打印位置
     */
    public static void printStack() {
        if (isDebug()) {
            StackTraceElement[] sts = Thread.currentThread().getStackTrace();
            StringBuilder po = new StringBuilder();
            if (sts != null) {
                int i = 0;
                for (StackTraceElement st : sts) {
                    i++;
                    if (i > 10) {
                        break;
                    }
                    if (i == 1) {
                        po.append(st.getClassName() + "." + st.getMethodName() + "("
                                + st.getFileName() + ":" + st.getLineNumber() + ")");
                    } else {
                        po.append("\n" + st.getClassName() + "." + st.getMethodName() + "("
                                + st.getFileName() + ":" + st.getLineNumber() + ")");
                    }
                }
            }
            Log.i(tag, new String(po));
        }
    }
}
