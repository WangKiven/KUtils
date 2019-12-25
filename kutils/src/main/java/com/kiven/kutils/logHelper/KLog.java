package com.kiven.kutils.logHelper;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.util.Log;

import com.kiven.kutils.tools.KString;
import com.kiven.kutils.tools.KUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/**
 * 不建议直接使用, 建议在项目中重写
 * Created by Kiven on 2014/12/12.
 */
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

    public static void setTag(String newTag) {
        if (KString.isBlank(newTag)) return;
        tag = newTag;
    }

    public static void d(String debugInfo) {
        if (isDebug()) {
            for (String burst : burstLog(debugInfo + findLog())) {
                Log.d(tag, burst);
            }
            addLog(debugInfo);
        }
    }

    public static void e(String errorInfo) {
        if (isDebug()) {
            for (String burst : burstLog(errorInfo + findLog())) {
                Log.e(tag, burst);
            }
            addLog(errorInfo);
        }
    }

    public static void v(String msg) {
        if (isDebug()) {
            for (String burst : burstLog(msg + findLog())) {
                Log.v(tag, burst);
            }
            addLog(msg);
        }
    }

    public static void i(String msg) {
        if (isDebug()) {
            for (String burst : burstLog(msg + findLog())) {
                Log.i(tag, burst);
            }
            addLog(msg);
        }
    }

    public static void w(String msg) {
        if (isDebug()) {
            for (String burst : burstLog(msg + findLog())) {
                Log.w(tag, burst);
            }
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
     * 分段log, 由于Log输出有长度限制
     */
    private static ArrayList<String> burstLog(String log) {
        ArrayList<String> bl = new ArrayList<>();

        int logLength = log.length();
        int maxLength = 3 * 1024;
        if (logLength > maxLength) {
            for (int i = 0; i < logLength; i += maxLength) {
                int endPosition = Math.min(i + maxLength, logLength);
                bl.add(log.substring(i, endPosition));
            }
        } else {
            bl.add(log);
        }


        return bl;
    }

    /**
     * 打印位置
     */
    public static void printStackTrace() {
        if (isDebug()) {
            StringBuilder sb = new StringBuilder("当前堆栈：");
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : elements) {
                sb.append("\nclassName: ").append(element.getClassName()).append(" , methodName: ")
                        .append(element.getMethodName()).append("(isNativeMethod:")
                        .append(element.isNativeMethod()).append("), lineNumber: ")
                        .append(element.getLineNumber()).append(", fileName: ")
                        .append(element.getFileName());
            }
            Log.i(tag, new String(sb));
        }
    }

    /**
     * 打印属性
     * obj, cla 传一个就行了。仅有cla就仅获取静态属性值。两个都有则cla = obj.getClass();
     *
     * @param isDeclared getFields() or getDeclaredFields()
     */
    public static void printClassField(Object obj, Class cla, boolean isDeclared) {
        if (!isDebug()) return;

        if (obj == null && cla == null) {
            Log.i(tag, "对象实例皆为null");
            return;
        }
        int objCode = 0;
        if (obj != null) {
            objCode = obj.hashCode();
            cla = obj.getClass();
        }

        // getFields()：获得某个类的所有的公共（public）的字段，包括父类中的字段。
        // getDeclaredFields()：获得某个类的所有声明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
        Field[] fields;
        if (isDeclared) fields = cla.getDeclaredFields();
        else fields = cla.getFields();

        StringBuilder sb = new StringBuilder("对象/实例 " + cla.getName() + "(" + objCode + ")" + "的属性有(" + fields.length + ")：");

        for (Field field : fields) {
            sb.append("\n").append(field.getName()).append(": ").append(field.getType().getSimpleName());

            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers)) field.setAccessible(true);

            if (Modifier.isStatic(modifiers)) {
                try {
                    Object value = field.get(cla);
                    if (value == null) {
                        sb.append(" = null");
                    } else
                        sb.append(" = ").append(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    sb.append(" 值获取异常");
                }
            } else {
                if (obj == null) {
                    sb.append("不是静态属性");
                } else {
                    try {
                        Object value = field.get(obj);
                        if (value == null) {
                            sb.append(" = null");
                        } else
                            sb.append(" = ").append(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sb.append(" 值获取异常");
                    }
                }
            }
        }
        Log.i(tag, new String(sb));
    }

    /**
     * 打印设备信息
     */
    public static void printDeviceInfo() {
        if (!KLog.isDebug()) return;

        Application app = KUtil.getApp();

        StringBuilder builder = new StringBuilder();
        builder.append("\n屏幕密度（0.75 / 1.0 / 1.5）:").append(KUtil.getScreenDensity(app))
                .append("\n屏幕密度DPI（120 / 160 / 240）:").append(KUtil.getScreenDensityDpi(app)).append("  每英寸多少像素")
                .append("\n屏幕宽度(px):").append(KUtil.getScreenWith(app))
                .append("\n屏幕高度(px):").append(KUtil.getScreenHeight(app))
                .append("\n屏幕宽度(dp):").append(KUtil.getScreenWith(app) / KUtil.getScreenDensity(app))
                .append("\n屏幕高度(dp):").append(KUtil.getScreenHeight(app) / KUtil.getScreenDensity(app))
                .append("\n屏幕宽度(英寸):").append(KUtil.getScreenWith(app) * 1f / KUtil.getScreenDensityDpi(app))
                .append("\n屏幕高度(英寸):").append(KUtil.getScreenHeight(app) * 1f / KUtil.getScreenDensityDpi(app))
                .append("\nProduct Model: ").append(Build.BRAND).append(",").append(Build.MODEL).append(",")
                .append(Build.VERSION.SDK_INT).append(",").append(Build.VERSION.RELEASE);

        Locale ll;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ll = app.getResources().getConfiguration().getLocales().get(0);
        } else {
            ll = app.getResources().getConfiguration().locale;
        }
        if (ll != null) {
            builder.append("\nLocal：").append(ll)
                    .append(", 语言：").append(ll.getLanguage())
                    .append(", variant:").append(ll.getVariant())
                    .append(", country:").append(ll.getCountry());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.append(", unicodeLocaleKeys: ").append(ll.getUnicodeLocaleKeys());
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.append("\ncpu_abis = ").append(Arrays.toString(Build.SUPPORTED_ABIS));
        } else {
            builder.append("\ncpu_abis = ").append(Build.CPU_ABI).append(", ").append(Build.CPU_ABI2);
        }

        ActivityManager am = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            builder.append(String.format("\ngles = %x", info.reqGlEsVersion));
        }

        builder.append("\n\n>>>>>>>>>>IP");
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface intf = nets.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();

                builder.append("\n网络(").append(intf.getDisplayName()).append("):");
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    builder.append("\n----").append(inetAddress.getHostAddress())
                            .append("(isLoopbackAddress=").append(inetAddress.isLoopbackAddress())
                            .append(",isIPV6=").append(inetAddress instanceof Inet6Address).append(")");
                }
            }
        } catch (Exception e) {
            builder.append("\n获取IP异常或没有网络");
        }

        builder.append("\n\n>>>>>>>>>>Build properties");
        Field[] buildFields = Build.class.getFields();
        for (Field field : buildFields) {
            try {
                Object value = field.get(Build.class);

                if (value != null && value.getClass() == Class.forName("[Ljava.lang.String;")) {
                    String as = "\n" + field.getName() + ": " + Arrays.toString((Object[]) value);
                    builder.append(as);
                } else {
                    String as = "\n" + field.getName() + ": " + value;
                    builder.append(as);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Properties properties = System.getProperties();
        Set<String> set = System.getProperties().stringPropertyNames(); //获取java虚拟机和系统的信息。

        builder.append("\n\n>>>>>>>>>>system properties");
        for (String name : set) {
            builder.append("\n").append(name).append(":\t").append(properties.getProperty(name));
        }

        KLog.i(new String(builder));
    }
}
