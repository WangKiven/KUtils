package com.kiven.kutils.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kiven.kutils.logHelper.KLog;

/**
 * 字体操作
 * Created by kiven on 16/7/28.
 */
public class KFont {

    // 为null时, 表示用系统的字体
    private static Typeface typeface = null;


    public static void setDefaultFont() {
        typeface = null;
    }
    public static void setDefaultFont(Typeface tf) {
        typeface = tf;
    }

    /**
     * 设置默认字体通过asset
     * @return 字体设置是否成功
     */
    public static boolean setDefaultFontFromAsset(Context context, String fontPath) {
        if (fontPath != null) {
            Typeface nTypeface = null;
            try {
                nTypeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            } catch (Exception e) {
                KLog.e(e);
            }
            if (nTypeface != null) {
                typeface = nTypeface;
                return true;
            }
        }

        return false;
    }

    /**
     * 设置默认字体通过文件路径
     * @return 字体设置是否成功
     */
    public static boolean setDefaultFontFromFile(Context context, String fontPath) {
        if (fontPath != null) {
            Typeface nTypeface = null;
            try {
                nTypeface = Typeface.createFromFile(fontPath);
            } catch (Exception e) {
                KLog.e(e);
            }
            if (nTypeface != null) {
                typeface = nTypeface;
                return true;
            }
        }

        return false;
    }

    /**
     * 设置字体
     *
     * @param root 遍历root下所有TextView系，并设置默认字体
     */
    public static void applyFont(Context context, View root) {
        if (typeface == null || context == null || root == null
                || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            return;
        }
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    applyFont(context, viewGroup.getChildAt(i));
            } else if (root instanceof TextView) {
                /*TextView mTextView = (TextView) root;
                Typeface mTypeface = typeface;
                Typeface tTypeface = mTextView.getTypeface();
                if (mTypeface != null) {
                    if (tTypeface != null && tTypeface.isBold()) {
                        mTextView.setTypeface(mTypeface, Typeface.BOLD);
                    } else {
                        mTextView.setTypeface(mTypeface);
                    }
                }*/
                setFont((TextView) root);
            }
        } catch (Exception e) {
            KLog.e(e);
        }
    }

    /**
     * 设置字体
     *
     * @param textViews 要设置字体的TextView
     */
    public static void applyFont(TextView... textViews) {
        if (typeface == null || textViews == null || textViews.length < 1
                || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            return;
        }

        try {
            for (TextView textView : textViews) {
                /*Typeface mTypeface = typeface;
                Typeface tTypeface = textView.getTypeface();
                if (mTypeface != null) {
                    if (tTypeface != null && tTypeface.isBold()) {
                        textView.setTypeface(mTypeface, Typeface.BOLD);
                    } else {
                        textView.setTypeface(mTypeface);
                    }
                }*/
                setFont(textView);
            }
        } catch (Exception e) {
            KLog.e(e);
        }
    }

    /**
     * 需保证 typeface != null, textView != null 才能调用该方法
     */
    private static void setFont(TextView textView) {
        Typeface mTypeface = typeface;
        Typeface tTypeface = textView.getTypeface();
        if (mTypeface != null) {
            if (tTypeface != null && tTypeface.isBold()) {
                textView.setTypeface(mTypeface, Typeface.BOLD);
            } else {
                textView.setTypeface(mTypeface);
            }
        }
    }

    /**
     * 获取字体
     */
    /*private static Typeface getTypeface(Context context) throws Exception {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/FZLanTingHeiS-L-GB.ttf");
        }
        return typeface;
    }*/
}
