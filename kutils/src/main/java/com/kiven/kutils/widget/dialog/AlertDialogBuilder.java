package com.kiven.kutils.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.kiven.kutils.R;

/**
 *
 * Created by kiven on 2016/10/10.
 */

public class AlertDialogBuilder {
    // 使用的布局, 可设置为自己的布局
    public static @LayoutRes int k_layout = R.layout.k_alert_dialog;
    public static @StyleRes int k_style = R.style.KAlertDialog;

    private Context context;
    private Dialog dialog;

    private View.OnClickListener cancleListener = null;
    private View.OnClickListener okListener = null;

    public AlertDialogBuilder(Context context) {
        initView(context, k_layout, k_style);
    }

    public AlertDialogBuilder(Context context, int layout, int style) {
        initView(context, layout, style);
    }

    private void initView(Context context, int layout, int style) {
        this.context = context;
        dialog = new Dialog(context, style);
        dialog.setContentView(layout);
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
        // TODO cancleListener == null时，需创建一个。否则点到界面外时，查询会调用到okListener
        if (listener == null) {
            cancleListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            };
        }
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
