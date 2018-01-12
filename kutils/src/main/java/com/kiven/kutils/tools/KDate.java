package com.kiven.kutils.tools;

import android.text.TextUtils;

import com.kiven.kutils.logHelper.KLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KDate {
    /**
     * 两个时间是否是同一天
     */
    public static boolean isSameDay(Date day1, Date day2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ds1 = sdf.format(day1);
        String ds2 = sdf.format(day2);

        return ds1.equals(ds2);
    }

    /**
     * 解析时间
     */
    public static Date parse(String ds) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(ds);
    }

    public static Date parse(String ds, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(ds);
    }

    /**
     * 获取字符串中的时间
     */
    public static Date findDate(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\d");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return parse(text.substring(matcher.start(), matcher.end()));
            } catch (ParseException e) {
                KLog.e(e);
            }
        }
        return null;
    }

    /**
     * 获取字符串中的时间
     * @param patterns 格式化的时间，如：yyyy-MM-dd，yyyy年MM月dd号
     */
    public static Date findDate(String text, String patterns) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        if (TextUtils.isEmpty(patterns)) {
            return findDate(text);
        }

        Pattern pattern = Pattern.compile(patterns.replaceAll("y|M|d", "\\\\d"));
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return parse(text.substring(matcher.start(), matcher.end()), patterns);
            } catch (ParseException e) {
                KLog.e(e);
            }
        }
        return null;
    }
}
