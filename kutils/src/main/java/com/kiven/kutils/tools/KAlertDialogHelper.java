package com.kiven.kutils.tools;

import android.content.Context;
import android.view.View;

import com.kiven.kutils.widget.dialog.AlertDialogBuilder;

/**
 *
 * Created by kiven on 2016/10/10.
 */

public class KAlertDialogHelper {
    public static void ShowSingleBtnAlertDialog(Context context, String title, String message, String okTitle, View.OnClickListener listener) {
        AlertDialogBuilder builder = new AlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setOkBtn(okTitle, 0, listener);
        builder.show();
    }
    public static void ShowSingleBtnAlertDialog(Context context, String title, String message, View.OnClickListener listener) {
        ShowSingleBtnAlertDialog(context, title, message, "确定", listener);
    }
    public static void ShowSingleBtnAlertDialog(Context context, String title, String message) {
        ShowSingleBtnAlertDialog(context, title, message, "确定", null);
    }
    public static void ShowSingleBtnAlertDialog(Context context, String message, View.OnClickListener listener) {
        ShowSingleBtnAlertDialog(context, "提示", message, "确定", listener);
    }
    public static void ShowSingleBtnAlertDialog(Context context, String message) {
        ShowSingleBtnAlertDialog(context, "提示", message, "确定", null);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message, String cancelTitle, String okTitle, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        AlertDialogBuilder builder = new AlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancleBtn(cancelTitle, 0, cancelListener);
        builder.setOkBtn(okTitle, 0, okListener);
        builder.show();
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message, String cancelTitle, String okTitle, View.OnClickListener okListener) {
        ShowTwoBtnAlertDialog(context, title, message, cancelTitle, okTitle, null, okListener);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message, String cancelTitle, String okTitle) {
        ShowTwoBtnAlertDialog(context, title, message, cancelTitle, okTitle, null, null);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message, String okTitle, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        ShowTwoBtnAlertDialog(context, title, message, "取消", okTitle, cancelListener, okListener);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message, String okTitle, View.OnClickListener okListener) {
        ShowTwoBtnAlertDialog(context, title, message, "取消", okTitle, null, okListener);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message, String okTitle) {
        ShowTwoBtnAlertDialog(context, title, message, "取消", okTitle, null, null);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        ShowTwoBtnAlertDialog(context, title, message, "取消", "确定", cancelListener, okListener);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message, View.OnClickListener okListener) {
        ShowTwoBtnAlertDialog(context, title, message, "取消", "确定", null, okListener);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String title, String message) {
        ShowTwoBtnAlertDialog(context, title, message, "取消", "确定", null, null);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String message, View.OnClickListener cancelListener, View.OnClickListener okListener) {
        ShowTwoBtnAlertDialog(context, "提示", message, "取消", "确定", cancelListener, okListener);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String message, View.OnClickListener okListener) {
        ShowTwoBtnAlertDialog(context, "提示", message, "取消", "确定", null, okListener);
    }

    public static void ShowTwoBtnAlertDialog(Context context, String message) {
        ShowTwoBtnAlertDialog(context, "提示", message, "取消", "确定", null, null);
    }
}
