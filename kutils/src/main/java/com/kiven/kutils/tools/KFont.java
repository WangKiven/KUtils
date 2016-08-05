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

    private static Typeface typeface = null;
    /**
     * 字体
     *
     * @param context
     * @param root
     *            遍历root下所有TextView系，并设置默认字体
     */
    public static void applyFont(Context context, View root) {
        if (context == null || root == null
                || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            return;
        }
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    applyFont(context, viewGroup.getChildAt(i));
            } else if (root instanceof TextView) {
                TextView mTextView = (TextView) root;
                Typeface mTypeface = getTypeface(context);
                Typeface tTypeface = mTextView.getTypeface();
                if (mTypeface != null) {
                    if (tTypeface != null && tTypeface.isBold()) {
                        mTextView.setTypeface(mTypeface, Typeface.BOLD);
                    } else {
                        mTextView.setTypeface(mTypeface);
                    }
                }
            }
        } catch (Exception e) {
            KLog.e(e);
        }
    }

    public static void applyFont(Context context, TextView... textViews) {
        if (context == null || textViews == null || textViews.length < 1
                || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            return;
        }

        try {
            for (TextView textView : textViews) {
                Typeface mTypeface = getTypeface(context);
                Typeface tTypeface = textView.getTypeface();
                if (mTypeface != null) {
                    if (tTypeface != null && tTypeface.isBold()) {
                        textView.setTypeface(mTypeface, Typeface.BOLD);
                    } else {
                        textView.setTypeface(mTypeface);
                    }
                }
            }
        } catch (Exception e) {
            KLog.e(e);
        }
    }

    private static Typeface getTypeface(Context context) throws Exception {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/FZLanTingHeiS-L-GB.ttf");
        }
        return typeface;
    }
}
