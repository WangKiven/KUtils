package com.kiven.kutils.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.kiven.kutils.R;

/**
 *
 * Created by kiven on 2016/10/10.
 */

public class AlertDialogBuilder {
    private Context context;
    private Dialog dialog;

    private View.OnClickListener cancleListener = null;
    private View.OnClickListener okListener = null;

    public AlertDialogBuilder(Context context) {
        this.context = context;
        dialog = new Dialog(context/*, R.style.KAlertDialog*/);
        dialog.setContentView(R.layout.k_alert_dialog);
//        dialog.setCancelable(false);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    dialog.dismiss();
                    if (cancleListener != null) {
                        cancleListener.onClick(null);
                    } else if (okListener != null) {
                        okListener.onClick(null);
                    }
                    return true;
                }
                return false;
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (cancleListener != null) {
                    cancleListener.onClick(null);
                } else if (okListener != null) {
                    okListener.onClick(null);
                }
            }
        });
    }

    public void setTitle(String text) {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(text);
    }

    public void setMessage(String text) {
        TextView tv_message = (TextView) findViewById(R.id.tv_message);
        tv_message.setText(text);
    }

    public void setCancleBtn(String text, int color, View.OnClickListener listener) {
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setText(text);
        if (color > 0) {
            tv_cancel.setTextColor(color);
        }
        tv_cancel.setVisibility(View.VISIBLE);

        cancleListener = listener;
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancleListener != null) {
                    cancleListener.onClick(v);
                }
                dialog.dismiss();
            }
        });
    }

    public void setOkBtn(String text, int color, View.OnClickListener listener) {
        TextView tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_ok.setText(text);
        if (color > 0) {
            tv_ok.setTextColor(color);
        }

        okListener = listener;
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okListener != null) {
                    okListener.onClick(v);
                }
                dialog.dismiss();
            }
        });
    }

    private View findViewById(@IdRes int resId) {
        return dialog.findViewById(resId);
    }

    public void show() {
        dialog.show();
    }
}
