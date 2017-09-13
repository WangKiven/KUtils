package com.kiven.kutils.tools;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.text.Html;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiven.kutils.R;
import com.kiven.kutils.logHelper.KLog;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kiven on 2017/5/18.
 */

public class KTextView {

    /**
     * 添加下划线
     */
    public static void addButtomLine(TextView tv) {
        tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * 中划线
     */
    public static void addCenterLine(TextView textView) {
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    /**
     * 设置中划线并加清晰
     */
    public static void addLine(TextView textView) {
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * 加粗
     */
    public static void addBold(TextView textView) {
        TextPaint tp = textView.getPaint();
        if (tp != null) {
            tp.setFakeBoldText(true);
        }
    }

    /**
     * 移除线
     *
     * @param textView
     */
    public static void removeLine(TextView textView) {
        textView.getPaint().setFlags(0);
    }

    /**
     * 抗锯齿
     *
     * @param textView
     */
    public static void addJuChuiLine(TextView textView) {
        textView.getPaint().setAntiAlias(true);
    }

    /**
     * 是否有输入
     *
     * @param textView
     * @return
     */
    public static boolean isNull(TextView textView) {
        String text = textView.getText().toString();
        return text == null || text.trim().length() < 1;
    }

    /**
     * 是否输入的是身份证号, 并且提示错误
     * @param textView
     * @return
     */
    public static boolean isUserCardID(TextView textView) {
        String text = textView.getText().toString();

        if (KString.checkIsIDCard(text)) {
            return true;
        }

        textView.setError("请输入正确的身份证号");
        textView.requestFocus();

        return false;
    }

    /**
     * 获取内容
     *
     * @param textView
     * @return
     */
    public static String getText(TextView textView) {
        String text = textView.getText().toString();
        if (text == null || text.length() < 1)
            return "";
        return text;

    }

    /**
     * 获取去前后空格的内容
     *
     * @param textView
     * @return
     */
    public static String getTrim(TextView textView) {
        String text = getText(textView);
        return text.trim();
    }

    /**
     * 检测TextView值，是否为在min与max中间的值
     *
     * @param textView
     * @param min
     * @param max
     * @return
     */
    public static boolean checkInt(TextView textView, int min, int max) {
        String text = getTrim(textView);
        if (text.length() > 0) {
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return true;
                }
            } catch (Exception e) {
                KLog.e(e);
            }
        }
        return false;
    }

    public static boolean checkInt(TextView textView, int min) {
        return checkInt(textView, min, Integer.MAX_VALUE);
    }

    public static void setText(TextView textView, CharSequence text) {
        if (KString.isBlank(text)) {
            textView.setText("");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text.toString(), Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        } else {
            textView.setText(Html.fromHtml(text.toString()));
        }
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyBoard(Context context, View... mEditTexts) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            for (View editText : mEditTexts) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }
    /**
     * 显示键盘
     */
    public static void showKeyBoard(final Context context, final View mEditText) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, 0);
            }

        }, 300);
    }
}
