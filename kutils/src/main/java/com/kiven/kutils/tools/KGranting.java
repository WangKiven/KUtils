package com.kiven.kutils.tools;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.kiven.kutils.logHelper.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限管理
 * Created by kiven on 16/3/31.
 */
public class KGranting {
    private Activity mActivity;
    private int requestCode;
    private String[] waitGrant;// 待授权数组
    private List<String> grantName;// 授权名称
    private GrantingCallBack callBack;

    private KGranting(@NonNull Activity activity, int requestCode, @NonNull String[] tGrant, @NonNull String[] tGrantName, GrantingCallBack callBack) {
        mActivity = activity;
        this.requestCode = requestCode;
        this.callBack = callBack;

        // 获取待授权list, 已授权的权限就不再重新请求授权
        List<String> mGrant = new ArrayList<>(3);
        grantName = new ArrayList<>(3);
        int i = 0;
        for (String g : tGrant) {
            if (ContextCompat.checkSelfPermission(mActivity, g) != PackageManager.PERMISSION_GRANTED) {
                if (!mGrant.contains(g)) {
                    mGrant.add(g);

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, g)) {
                        grantName.add(tGrantName[i]);
                    }
                }
            }
            i ++;
        }

        // 将待授权list转化为待授权数组
        waitGrant = new String[mGrant.size()];
        for (i = 0; i < mGrant.size(); i++) {
            waitGrant[i] = mGrant.get(i);
        }
    }

    private void startCheck() {

        // TODO 如果待授权数组为空,则所有请求已授权
        if (waitGrant == null || waitGrant.length == 0) {
            if (callBack != null) {
                callBack.onGrantSuccess(true);
            }
            granting = null;
            return;
        }

        // TODO 请求授权
        if (grantName.size() > 0) {
            String message = "你需要授权访问 " + grantName.get(0);
            if (grantName.size() > 1) {
                for (int i = 1; i < grantName.size(); i++) {
                    message = message + ", " + grantName.get(i);
                }
                message = message + " 等功能";
            }

            message = message + ", 请在设置'权限'中打开相关权限.";

            new AlertDialog.Builder(mActivity)
                    .setMessage(message)
                    .setPositiveButton("前去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /*ActivityCompat.requestPermissions(mActivity, waitGrant,
                                    requestCode);*/
                            if (callBack != null) {
                                callBack.onGrantSuccess(false);
                            }
                            granting = null;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                            mActivity.startActivity(intent);
                        }
                    })
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (callBack != null) {
                                callBack.onGrantSuccess(false);
                            }
                            granting = null;
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(mActivity, waitGrant,
                    requestCode);
        }
    }

    private boolean checkResult(@NonNull int[] grantResults) {
        if (grantResults == null || grantResults.length < 1) {
            return false;
        }

        for (int i : grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public interface GrantingCallBack {
        void onGrantSuccess(boolean isSuccess);
    }

    private static KGranting granting = null;

    /**
     * 请求多个授权
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String[] tGrant, @NonNull String[] tGrantName, GrantingCallBack callBack) {
        if (granting == null) {
            granting = new KGranting(activity, requestCode, tGrant, tGrantName, callBack);
            granting.startCheck();
        } else {
            if (KLog.isDebug()) {
                Toast.makeText(activity, "授权请求失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 请求单个授权
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String tGrant, @NonNull String tGrantName, GrantingCallBack callBack) {
        requestPermissions(activity, requestCode, new String[]{tGrant}, new String[]{tGrantName}, callBack);
    }

    /**
     * 处理授权结果
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (granting != null && granting.callBack != null) {
            granting.callBack.onGrantSuccess(granting.checkResult(grantResults));
        }

        granting = null;
    }
}
