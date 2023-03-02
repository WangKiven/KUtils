package com.kiven.kutils.logHelper;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.util.Log;

import com.kiven.kutils.callBack.Supplier;
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
import java.util.Map;

/**
 * 不建议直接使用, 建议在项目中重写
 * Created by Kiven on 2014/12/12.
 */
public class KLog {

    private static boolean showAtLine = true;

    private static final LinkedList<KLogInfo> logs = new LinkedList<KLogInfo>();

    public static boolean isDebug() {
        return KUtil.getConfig().isDebug();
    }

    /**
     * 日志记录操作
     *
     * @param log log
     * @return
     */
    protected static KLogInfo addLog(String log) {

        synchronized (logs) {
            if (logs.size() > 500) {
                logs.removeLast();
                logs.removeLast();
            }

            StackTraceElement[] sts = Thread.currentThread().getStackTrace();

            String codePosition = null;
            StringBuilder codePositionStack = new StringBuilder();
            if (sts != null) {
                int i = 0;
                for (StackTraceElement st : sts) {
                    i++;
                    if (i > 10) {
                        break;
                    }

                    String s = (st.getClassName() + "." + st.getMethodName() + "("
                            + st.getFileName() + ":" + st.getLineNumber() + ")");
                    if (i == 1) {
                        codePositionStack.append(s);
                    } else {
                        codePositionStack.append(", ").append(s);
                    }

                    if (codePosition == null) {
                        if (st.isNativeMethod()) {continue;}
                        if (st.getClassName().equals(Thread.class.getName())) {continue;}
                        if (st.getClassName().equals(KLog.class.getName())) {continue;}

                        codePosition = " at " + st.getClassName() + "." + st.getMethodName() + "("
                                + st.getFileName() + ":" + st.getLineNumber() + ")";
                    }
                }
            }
            KLogInfo info = new KLogInfo(log, codePosition, codePositionStack.toString());
            logs.addFirst(info);
            return info;
        }
    }

    public static LinkedList<KLogInfo> getLogs() {
        return logs;
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
    /*public static String findLog() {

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
            return " at " + st.getClassName() + "." + st.getMethodName() + "("
                    + st.getFileName() + ":" + st.getLineNumber() + ")";
        }
        return "";

    }*/

    private static String tag = "KLog_default";

    public static void setTag(String newTag) {
        if (KString.isBlank(newTag)) return;
        tag = newTag;
    }

    public static String getTag() {
        return tag;
    }

    public static void d(String debugInfo) {
        if (isDebug()) {
            KLogInfo info = addLog(debugInfo);
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.d(tag, burst);
            }
        }
    }
    public static void d(Supplier<String> call) {
        if (isDebug()) {
            KLogInfo info = addLog(call.callBack());
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.d(tag, burst);
            }
        }
    }

    public static void e(String errorInfo) {
        if (isDebug()) {
            KLogInfo info = addLog(errorInfo);
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.e(tag, burst);
            }
        }
    }
    public static void e(Supplier<String> call) {
        if (isDebug()) {
            KLogInfo info = addLog(call.callBack());
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.e(tag, burst);
            }
        }
    }

    public static void v(String msg) {
        if (isDebug()) {
            KLogInfo info = addLog(msg);
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.v(tag, burst);
            }
        }
    }
    public static void v(Supplier<String> call) {
        if (isDebug()) {
            KLogInfo info = addLog(call.callBack());
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.v(tag, burst);
            }
        }
    }

    public static void i(String msg) {
        if (isDebug()) {
            KLogInfo info = addLog(msg);
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.i(tag, burst);
            }
        }
    }
    public static void i(Supplier<String> call) {
        if (isDebug()) {
            KLogInfo info = addLog(call.callBack());
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.i(tag, burst);
            }
        }
    }

    public static void w(String msg) {
        if (isDebug()) {
            KLogInfo info = addLog(msg);
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.w(tag, burst);
            }
        }
    }
    public static void w(Supplier<String> call) {
        if (isDebug()) {
            KLogInfo info = addLog(call.callBack());
            for (String burst : burstLog(info.log + info.codePosition)) {
                Log.w(tag, burst);
            }
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
                pw.close();
                sw.close();
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
            String ss = new String(sb);
            Log.i(tag, ss);
            addLog(ss);
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
                    sb.append(" = ").append(obj2Str(field.get(cla)));
                } catch (Exception e) {
                    e.printStackTrace();
                    sb.append(" 值获取异常");
                }
            } else {
                if (obj == null) {
                    sb.append("不是静态属性");
                } else {
                    try {
                        sb.append(" = ").append(obj2Str(field.get(obj)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        sb.append(" 值获取异常");
                    }
                }
            }
        }

        /*String msg = new String(sb);
        for (String burst : burstLog(msg + findLog())) {
            Log.i(tag, burst);
        }
        addLog(msg);*/
        KLogInfo info = addLog(new String(sb));
        for (String burst : burstLog(info.log + info.codePosition)) {
            Log.i(tag, burst);
        }
    }

    public static String obj2Str(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return (String) obj;
        if (obj instanceof Number) return obj.toString();

        if (obj.getClass().isArray()) {
            Class subType = obj.getClass().getComponentType();
            if (subType.isPrimitive()) {
                if (subType == int.class) return Arrays.toString((int[]) obj);
                if (subType == boolean.class) return Arrays.toString((boolean[]) obj);
                if (subType == byte.class) return Arrays.toString((byte[]) obj);
                if (subType == char.class) return Arrays.toString((char[]) obj);
                if (subType == double.class) return Arrays.toString((double[]) obj);
                if (subType == float.class) return Arrays.toString((float[]) obj);
                if (subType == long.class) return Arrays.toString((long[]) obj);
                if (subType == short.class) return Arrays.toString((short[]) obj);
                return obj.toString();
            }
            Object[] a = (Object[]) obj;
            StringBuilder sb = new StringBuilder("[ ");
            for (int i = 0; i < a.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(obj2Str(a[i]));
            }
            sb.append(" ]");
            return sb.toString();
        }

        return obj.toString();
    }

    /**
     * 打印设备信息
     */
    public static void printDeviceInfo() {
        if (!KLog.isDebug()) return;

        Application app = KUtil.getApp();

        StringBuilder builder = new StringBuilder();
        builder.append("printDeviceInfo：\n屏幕密度（0.75 / 1.0 / 1.5）:").append(KUtil.getScreenDensity())
                .append("\n屏幕密度DPI（120 / 160 / 240）:").append(KUtil.getScreenDensityDpi()).append("  每英寸多少像素")
                .append("\n屏幕宽度(px):").append(KUtil.getScreenWith())
                .append("\n屏幕高度(px):").append(KUtil.getScreenHeight())
                .append("\n屏幕宽度(dp):").append(KUtil.getScreenWith() / KUtil.getScreenDensity())
                .append("\n屏幕高度(dp):").append(KUtil.getScreenHeight() / KUtil.getScreenDensity())
                .append("\n屏幕宽度(英寸):").append(KUtil.getScreenWith() * 1f / KUtil.getScreenDensityDpi())
                .append("\n屏幕高度(英寸):").append(KUtil.getScreenHeight() * 1f / KUtil.getScreenDensityDpi())
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


        builder.append("\n\n>>>>>>>>>>system properties");
        //获取java虚拟机和系统的信息。

        /*Properties properties = System.getProperties();
        Set<String> set = System.getProperties().stringPropertyNames();

        for (String name : set) {
            builder.append("\n").append(name).append(":\t").append(properties.getProperty(name));
        }*/

        for (Map.Entry entry : System.getProperties().entrySet()) {
            builder.append("\n").append(entry.getKey()).append(":\t").append(entry.getValue());
        }


        builder.append("\n\n>>>>>>>>>>system env");
        for (Map.Entry entry : System.getenv().entrySet()) {
            builder.append("\n").append(entry.getKey()).append(":\t").append(entry.getValue());
        }


        /*String msg = new String(builder);
        for (String burst : burstLog(msg + findLog())) {
            Log.i(tag, burst);
        }
        addLog(msg);*/

        KLogInfo info = addLog(new String(builder));
        for (String burst : burstLog(info.log + info.codePosition)) {
            Log.i(tag, burst);
        }
    }
}
