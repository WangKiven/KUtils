package com.kiven.kutils.tools;

import android.content.Context;
import android.view.View;

import com.kiven.kutils.widget.dialog.AlertDialogBuilder;

/**
 *
 * Created by kiven on 2016/10/10.
 */

public class KAlertDialogHelper {
    public static String k_title = "提示";
    public static String k_okTitle = "确定";
    public static String k_cancelTitle = "取消";

    // TODO 单按钮

    public static void Show1BDialog(Context context, String title, String message, String okTitle, View.OnClickListener listener) {
        AlertDialogBuilder builder = new AlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setOkBtn(okTitle, 0, listener);
        builder.show();
    }
    public static void Show1BDialog(Context context, String title, String message, View.OnClickListener listener) {
        Show1BDialog(context, title, message, k_okTitle, listener);
    }
    public static void Show1BDialog(Context context, String title, String message) {
        Show1BDialog(context, title, message, k_okTitle, null);
    }
    public static void Show1BDialog(Context context, String message, View.OnClickListener listener) {
        Show1BDialog(context, k_title, message, k_okTitle, listener);
    }
    public static void Show1BDialog(Context context, String message) {
        Show1BDialog(context, k_title, message, k_okTitle, null);
    }

    // TODO 双按钮

    public static void Show2BDialog(Context context, String title, String message, String cancelTitle, String okTitle, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        AlertDialogBuilder builder = new AlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancleBtn(cancelTitle, 0, cancelListener);
        builder.setOkBtn(okTitle, 0, okListener);
        builder.show();
    }

    public static void Show2BDialog(Context context, String title, String message, String cancelTitle, String okTitle, View.OnClickListener okListener) {
        Show2BDialog(context, title, message, cancelTitle, okTitle, null, okListener);
    }

    public static void Show2BDialog(Context context, String title, String message, String cancelTitle, String okTitle) {
        Show2BDialog(context, title, message, cancelTitle, okTitle, null, null);
    }

    public static void Show2BDialog(Context context, String title, String message, String okTitle, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        Show2BDialog(context, title, message, k_cancelTitle, okTitle, cancelListener, okListener);
    }

    public static void Show2BDialog(Context context, String title, String message, String okTitle, View.OnClickListener okListener) {
        Show2BDialog(context, title, message, k_cancelTitle, okTitle, null, okListener);
    }

    public static void Show2BDialog(Context context, String title, String message, String okTitle) {
        Show2BDialog(context, title, message, k_cancelTitle, okTitle, null, null);
    }

    public static void Show2BDialog(Context context, String title, String message, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        Show2BDialog(context, title, message, k_cancelTitle, k_okTitle, cancelListener, okListener);
    }

    public static void Show2BDialog(Context context, String title, String message, View.OnClickListener okListener) {
        Show2BDialog(context, title, message, k_cancelTitle, k_okTitle, null, okListener);
    }

    public static void Show2BDialog(Context context, String title, String message) {
        Show2BDialog(context, title, message, k_cancelTitle, k_okTitle, null, null);
    }

    public static void Show2BDialog(Context context, String message, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        Show2BDialog(context, k_title, message, k_cancelTitle, k_okTitle, cancelListener, okListener);
    }

    public static void Show2BDialog(Context context, String message, View.OnClickListener okListener) {
        Show2BDialog(context, k_title, message, k_cancelTitle, k_okTitle, null, okListener);
    }

    public static void Show2BDialog(Context context, String message) {
        Show2BDialog(context, k_title, message, k_cancelTitle, k_okTitle, null, null);
    }

    // TODO 双按钮, 默认标题

    public static void Show2BDialogDefaultTitle(Context context, String message, String cancelTitle, String okTitle, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        Show2BDialog(context, k_title, message, cancelTitle, okTitle, cancelListener, okListener);
    }

    public static void Show2BDialogDefaultTitle(Context context, String message, String cancelTitle, String okTitle, View.OnClickListener okListener) {
        Show2BDialog(context, k_title, message, cancelTitle, okTitle, null, okListener);
    }

    public static void Show2BDialogDefaultTitle(Context context, String message, String cancelTitle, String okTitle) {
        Show2BDialog(context, k_title, message, cancelTitle, okTitle, null, null);
    }

    public static void Show2BDialogDefaultTitle(Context context, String message, String okTitle, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        Show2BDialog(context, k_title, message, k_cancelTitle, okTitle, cancelListener, okListener);
    }

    public static void Show2BDialogDefaultTitle(Context context, String message, String okTitle, View.OnClickListener okListener) {
        Show2BDialog(context, k_title, message, k_cancelTitle, okTitle, null, okListener);
    }

    public static void Show2BDialogDefaultTitle(Context context, String message, String okTitle) {
        Show2BDialog(context, k_title, message, k_cancelTitle, okTitle, null, null);
    }
}
