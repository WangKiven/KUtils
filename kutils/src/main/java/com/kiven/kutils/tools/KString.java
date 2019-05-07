package com.kiven.kutils.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Xml;

import com.kiven.kutils.callBack.Function;
import com.kiven.kutils.logHelper.KLog;

import org.xmlpull.v1.XmlPullParser;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @author Administrator
 */
public class KString {

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 根据时区转化，服务器提供的时间为北京时间，转化为系统默认时区时间
     *
     * @param sdate
     * @return
     */
    public static Date toLocalDate(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return time;
        }

        //根据时区转化，服务器提供的时间为北京时间，转化为系统默认时区时间
        TimeZone srcTimeZone = TimeZone.getDefault();
        TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");
        int cc = srcTimeZone.getRawOffset() - destTimeZone.getRawOffset();
        if (cc != 0) {
            long tarTime = time.getTime() + cc;
            time = new Date(tarTime);
        }

        return time;
    }

    /**
     * 获取当前时间的字符串形式
     *
     * @return
     */
    public static String nowDateStr() {
        return dateFormater.get().format(new Date());
    }

    public static String formatDate(long date) {
        return dateFormater.get().format(new Date(date));
    }


    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm");
        }
    };


    /**
     * 以友好的方式显示时间
     */
    public static String friendlyTime(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        return friendlyTime(time);
    }

    /**
     * 以友好的方式显示时间
     */
    public static String friendlyTime(@NonNull Date time) {

        //根据时区转化，服务器提供的时间为北京时间，转化为系统默认时区时间
        TimeZone srcTimeZone = TimeZone.getDefault();
        TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");
        int cc = srcTimeZone.getRawOffset() - destTimeZone.getRawOffset();
        if (cc != 0) {
            long tarTime = time.getTime() + cc;
            time = new Date(tarTime);
        }


        String ftime = "";
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        long min = cal.getTimeInMillis();

        cal.setTime(time);
        int syear = cal.get(Calendar.YEAR);
        int sday = cal.get(Calendar.DAY_OF_YEAR);

        int days = 0;
        if (year > syear) {
            days = 2;
        } else {
            days = day - sday;
        }

        if (days == 0) {
            int minute = (int) ((min - time.getTime()) / 60000);
            if (minute < 2) {
                ftime = "刚刚";
            } else if (minute < 10) {
                ftime = "几分钟前";
            } else {
                ftime = "今天 " + dateFormater2.get().format(time) + "";
            }
        } else if (days == 1) {
            ftime = "昨天 " + dateFormater2.get().format(time) + "";
        } else if (days > 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            ftime = DateFormat.format("yyyy-MM-dd", time) + "";
        }
        return ftime;
    }


    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @deprecated {@link #isBlank(CharSequence)}
     */
    @Deprecated
    public static boolean isEmpty(String input) {
        return isBlank(input);
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isBlank(CharSequence input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若input为null或空字符串，返回nullResult
     */
    public static String checkBlank(String input, String nullResult) {
        return isBlank(input) ? nullResult : input;
    }

    /**
     * 判断字符串是否相等
     *
     * @deprecated use {@link android.text.TextUtils#equals(CharSequence, CharSequence)}
     */
    @Deprecated
    public static boolean equals(String s1, String s2) {
        /*if (s1 == null) {
            return s2 == null;
        } else {
            return s1.equals(s2);
        }*/

        return TextUtils.equals(s1, s2);
    }

    /**
     * s1是否包含s2
     */
    public static boolean contains(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        } else {
            return s1.contains(s2);
        }
    }

    /**
     * 字符串转整数
     */
    public static int toInt(String str, int defValue) {
        if (str != null && str.length() > 0) {
            try {
                return new BigDecimal(str).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            } catch (Exception e) {
                KLog.e(e);
            }
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        return toInt(obj.toString(), 0);
    }

    /**
     * 字符串转double
     *
     * @param str
     * @param defValue
     * @return
     */
    public static double toDouble(String str, double defValue) {
        if (str != null && str.length() > 0) {
            try {
                return Double.valueOf(str);
            } catch (Exception e) {
                KLog.e(e);
            }
        }
        return defValue;
    }

    /**
     * 解析nameColor
     *
     * @param nameColor
     * @return
     */
    public static int[] pariseNameColor(String nameColor) {
        int[] colors = new int[1];

        if (isEmpty(nameColor)) {
            return colors;
        }

        Pattern p = Pattern.compile(",");
        String[] items = p.split(nameColor);

        colors = new int[items.length];
        int i = 0;
        for (String string : items) {
            colors[i] = Integer.parseInt(string);
            i++;
        }

        return colors;
    }

    /**
     * 检测是否有emoji字符
     *
     * @param source
     * @return 一旦含有就抛出
     */
    public static boolean containsEmoji(CharSequence source) {
        if (source == null || source.length() == 0) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 将text放到剪贴板
     *
     * @param activity
     * @param text
     */
    @SuppressLint("NewApi")
    @SuppressWarnings({"static-access", "deprecation"})
    public static void setClipText(Activity activity, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(activity.CLIPBOARD_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            clipboardManager.setText(text);
        } else {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text));
        }
    }

    /**
     * 加密md5
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

    /**
     * md5Hex
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5Hex(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bits = md.digest(data.getBytes(Charset.forName("UTF-8")));
        char[] toDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int l = bits.length;
        char[] out = new char[l << 1];
        int i = 0;

        for(int var5 = 0; i < l; ++i) {
            out[var5++] = toDigits[(240 & bits[i]) >>> 4];
            out[var5++] = toDigits[15 & bits[i]];
        }

        return new String(out);
    }

    /**
     * 加密哈希
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String sha1(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(data.getBytes("UTF-8"));
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

    //-----------价格转化-----------

    /**
     * 字符串转int
     */
    @Deprecated
    public static int fromIntString(String s) {
        int value = 0;

        if (s != null && s.length() > 0) {
            try {
                value = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                KLog.e(e);
            }
        }

        return value;
    }

    /**
     * 字符串转float
     *
     * @param s
     * @return
     */
    public static float fromFloatString(String s) {
        float value = 0f;

        if (s != null && s.length() > 0) {
            try {
                value = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                KLog.e(e);
            }
        }
        return value;
    }

    private static DecimalFormat formater = null;

    private static void initDecimalFormat() {
        if (formater == null) {
            //			formater = new DecimalFormat("0.00");
            formater = new DecimalFormat("#.##");//按固定格式进行输出:"00.000" "##.###",按百分比进行输出:"##.##%"
            //	        formater.setMaximumFractionDigits(2);//设置小数点后最大位数为2
            formater.setGroupingSize(0);//利用逗号进行分组时每个分组的大小,这里不分组
//            formater.setGroupingUsed(false);//当为false时上述设置的分组大小无效，为true时才能进行分组
//            	        formater.setRoundingMode(RoundingMode.FLOOR);// http://blog.csdn.net/alanzyy/article/details/8465098
            formater.setRoundingMode(RoundingMode.DOWN);//舍去多余的位数
        }
    }

    /**
     * 创建BigDecimal
     */
    public static BigDecimal newDecimal(String num) {
        if (num == null || num.length() == 0) {
            return new BigDecimal(0);
        }
        try {
            return new BigDecimal(num);
        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal(0);
        }
    }

    /**
     * 11.2000  ->  11.2, 11.230 -> 11.23, 11.236 -> 11.23
     */
    public static String formaterFloat(double value) {
        initDecimalFormat();
        return formater.format(value);
    }

    /**
     * 过时，建议使用以下方法
     * {@link #formaterReal(BigDecimal)}
     */
    @Deprecated
    public static String formaterFloat(BigDecimal value) {
        return formaterReal(value);
    }

    public static String formaterReal(BigDecimal value) {
        initDecimalFormat();
        return formater.format(value);
    }

    /**
     * 格式化金额 23450 -> 2.34
     */
    public static String formaterWan(int value) {
        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.divide(new BigDecimal(10000));
        return formaterReal(decimal);
    }

    /**
     * 格式化金额 "23450" -> 2.34
     */
    public static String formaterWan(String value) {
        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.divide(new BigDecimal(10000));
        return formaterReal(decimal);
    }

    /**
     * 简化的万单位转化为个位 2.78 —> 27800
     */
    public static int fromWan(String s) {
        if (isBlank(s)) {
            return 0;
        }
        try {
            return new BigDecimal(s).multiply(new BigDecimal(10000)).intValue();
        } catch (Exception e) {
            KLog.e(e);
            return 0;
        }
    }

    /**
     * 简化的万单位转化为个位 2.78 —> 27800
     */
    @Deprecated
    public static int fromWanString(String s) {
        /*if (isBlank(s)) {
            return 0;
        }
        return new BigDecimal(s).multiply(new BigDecimal(10000)).intValue();*/
        return fromWan(s);
    }

    /**
     * int字符串解析
     */
    @Deprecated
    public static int parseInt(String intStr) {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*@Deprecated
    public static String favorableMoney(int money) {
        if (money > 0) {
            return "加" + money + "元";
        } else {
            return "优惠" + Math.abs(money) + "元";
        }
    }*/

    /**
     * xml 解析
     *
     * @param xmlStr
     * @return
     */
    public static TreeMap<String, Object> parserXml(String xmlStr) {
        TreeMap<String, Object> map = new TreeMap<String, Object>();
        try {
            XmlPullParser xrp = Xml.newPullParser();
            Reader reader = new StringReader(xmlStr);
            xrp.setInput(reader);
            // 直到文档的结尾处
//			return parserXml(xrp.getName(), xrp);

            ArrayList<String> items = new ArrayList<String>();
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {

                switch (xrp.getEventType()) {
                    case XmlResourceParser.START_TAG:
//						ULog.i("tag = " + xrp.getName());
                        items.add(xrp.getName());
                        break;
                    case XmlResourceParser.TEXT:
//						ULog.i("text = " + xrp.getText());
                        map.put(items.get(items.size() - 1), xrp.getText());
                        break;
                    case XmlResourceParser.END_TAG:
//						ULog.i("tag = " + xrp.getName());
                        items.remove(items.size() - 1);
                        break;
                    case XmlResourceParser.END_DOCUMENT:
//						ULog.i("end doc");
                        break;
                }
                xrp.next();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            KLog.e(e);
        }

        return map;
    }

    /**
     * 验证密码是否符合要求,建议仅在设置密码时使用,以防导致用户原有密码不能使用
     * 8~16位字母与数字的混合密码
     *
     * @param pw
     * @return
     */
    public static boolean verifyPassword(String pw) {
        if (!isEmpty(pw) && pw.length() > 5) {
            String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pw);

            return matcher.matches();
        }
        return false;
    }

    /**
     * 检查是否是电话号码
     * 正则：手机号（精确）
     * <p>移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188</p>
     * <p>联通：130、131、132、145、155、156、166、175、176、185、186</p>
     * <p>电信：133、153、171、173、177、180、181、189、19+</p>
     * <p>全球星：1349</p>
     * <p>虚拟运营商：170</p>
     * @return 是否是电话号码
     */
    public static boolean checkIsPhoneNumber(CharSequence num) {
        if (TextUtils.isEmpty(num)) {
            return false;
        }
        String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(166)|(17[0,1,3,5-8])|(18[0-9])|(19[0-9])|(147))\\d{8}$";
        Pattern pattern = Pattern.compile(REGEX_MOBILE_EXACT);
        return pattern.matcher(num).matches();
    }

    /**
     * 检查是否是身份证号
     *
     * @return 是否是身份证号
     */
    public static boolean checkIsIDCard(String num) {
        if (TextUtils.isEmpty(num)) {
            return false;
        }
        String REGEX_ID_CARD_EXACT = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
        Pattern pattern = Pattern.compile(REGEX_ID_CARD_EXACT);
        return pattern.matcher(num).matches();
    }

    /**
     * 检查是否是正确的车架号,车架号可以为空
     *
     * @param vehicleFrameNO 车架号
     * @return 是否是正确的车架号
     */
    public static boolean checkIsVehicleFrameNO(String vehicleFrameNO) {
        if (TextUtils.isEmpty(vehicleFrameNO)) {
            return true;
        }
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        return pattern.matcher(vehicleFrameNO).matches();
    }

    /**
     * 格式化数组
     * @param function 用于提取对象的字符串，默认使用toString
     */
    public static <T> String formateWithComma(List<T> objs, Function<T, String> function) {
        if (objs == null || objs.size() == 0) {
            return "";
        }
        // 如果function=null，则调用对象的toString()
        if (function == null) {
            function = new Function<T, String>() {
                @Override
                public String callBack(T param) {
                    return param.toString();
                }
            };
        }

        StringBuilder builder = new StringBuilder(function.callBack(objs.get(0)));
        if (objs.size() > 1) {
            for (int i = 1; i < objs.size(); i++) {
                builder.append(",");
                builder.append(function.callBack(objs.get(i)));
            }
        }
        return new String(builder);
    }
    /**
     * 格式化数组
     * @param function 用于提取对象的字符串，默认使用toString
     */
    public static <T> String formateWithComma(T[] objs, Function<T, String> function) {
        if (objs == null || objs.length == 0) {
            return "";
        }
        // 如果function=null，则调用对象的toString()
        if (function == null) {
            function = new Function<T, String>() {
                @Override
                public String callBack(T param) {
                    return param.toString();
                }
            };
        }

        StringBuilder builder = new StringBuilder(function.callBack(objs[0]));
        if (objs.length > 1) {
            for (int i = 1; i < objs.length; i++) {
                builder.append(",");
                builder.append(function.callBack(objs[i]));
            }
        }
        return new String(builder);
    }
    /**
     * 格式化数组
     * @param function 用于提取对象的字符串，默认使用toString
     */
    public static <T> String formateWithComma(Function<T, String> function, T... objs) {
        return formateWithComma(objs, function);
    }

    /**
     * html处理
     */
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
    public static Spanned fromHtml(Context context, @StringRes int sourceId) {
        return fromHtml(context.getString(sourceId));
    }
    public static Spanned fromHtml(@StringRes int sourceId) {
        return fromHtml(KContext.getInstance().getString(sourceId));
    }

    /**
     * 比较两个实数
     */
    public static int compareNumber(@NonNull String num1, @NonNull String num2) {
        BigDecimal decimal1 = new BigDecimal(num1);
        BigDecimal decimal2 = new BigDecimal(num2);
        return decimal1.compareTo(decimal2);
    }

    /**
     * 获取text中适配正则表达式（patterns）的第一个子串
     */
    public static String substring(String text, String patterns) {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(patterns)) {
            return null;
        }

        Pattern pattern = Pattern.compile(patterns);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return text.substring(matcher.start(), matcher.end());
        }
        return null;
    }

    /**
     * 格式化银行卡号
     *
     * 身份证号可能位数 16 18 19
     * 16 19位每4位一组
     * 18位每3位一组
     */
    public static StringBuilder formatBankCode(@NonNull String bankCardNo, boolean hide) {
        int length = bankCardNo.length();
        int groupLength = length==18? 3:4;

        int groupCount = ((length - 1)/groupLength) + 1;

        StringBuilder sb = new StringBuilder(bankCardNo.substring(0, groupLength));
        for (int i=1;i < groupCount - 1;i++) {
            if (groupLength == 3) {
                sb.append(" ").append(hide ? "***" : bankCardNo.substring(i * 3, i * 3 + 3));
            } else {
                sb.append(" ").append(hide ? "****" : bankCardNo.substring(i * 4, i * 4 + 4));
            }
        }
        sb.append(" ").append(bankCardNo.substring((groupCount - 1) * groupLength, length));

        return sb;
    }

    /**
     * 空格补齐字符串,注：两个空格占一个字位。如：“123” -> "     123"
     * @param a 补齐到多少位
     */
    public static String polishing(String s, int a) {
        if (a < 1) return "";

        StringBuilder sb = new StringBuilder(a);
        for (int i=0; i<a;i++) {
            if (i < s.length()) {
                sb.append(s.charAt(i));
            } else {
                sb.append("  ");
            }
        }
        return sb.toString();
    }
}
