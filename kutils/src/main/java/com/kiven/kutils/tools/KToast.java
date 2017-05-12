package com.kiven.kutils.tools;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * Created by kiven on 2016/11/2.
 */

public class KToast {
    private static Toast mToast;
    public static void ToastMessage(Context context, String msg, int time) {
        if (!KString.isBlank(msg)) {

            if (mToast == null) {
                mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            }
            mToast.setText(msg);
            mToast.setDuration(time);

            mToast.show();
        }
    }

    public static void ToastMessage(String msg, int time) {
        Context context = KContext.getInstance();
        if (context != null) {

            /*if (KContext.getInstance().isOnForeground()) {
                // TODO app在后台，不弹出提示
                return;
            }*/

            ToastMessage(context, msg, time);
        }
    }
    public static void ToastMessage(String msg) {
        ToastMessage(msg, Toast.LENGTH_LONG);
    }
}
