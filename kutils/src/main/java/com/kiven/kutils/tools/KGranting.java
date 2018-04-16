package com.kiven.kutils.tools;

import android.Manifest;
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
import java.util.Map;
import java.util.TreeMap;

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

    // 是否显示权限申请失败提示
    private boolean isShowErrorTip = true;

    private KGranting(@NonNull Activity activity, int requestCode, @NonNull String[] tGrant, @NonNull String[] tGrantName, GrantingCallBack callBack) {
        this(activity, requestCode, tGrant, tGrantName, true, callBack);
    }
    private KGranting(@NonNull Activity activity, int requestCode, @NonNull String[] tGrant, @NonNull String[] tGrantName, boolean isShowErrorTip, GrantingCallBack callBack) {
        mActivity = activity;
        this.requestCode = requestCode;
        this.isShowErrorTip = isShowErrorTip;
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
            i++;
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
            String message = "请授权访问 " + grantName.get(0);
            if (grantName.size() > 1) {
                for (int i = 1; i < grantName.size(); i++) {
                    message = message + ", " + grantName.get(i);
                }
                message = message + " 等权限";
            }

//            message = message + ", 请在设置'权限'中打开相关权限.";

            new AlertDialog.Builder(mActivity)
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(mActivity, waitGrant,
                                    requestCode);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String[] tGrant, @NonNull String[] tGrantName, boolean isShowErrorTip, GrantingCallBack callBack) {
        if (granting == null) {
            granting = new KGranting(activity, requestCode, tGrant, tGrantName, isShowErrorTip, callBack);
            granting.startCheck();
        } else {
            granting = null;
            if (KLog.isDebug()) {
                Toast.makeText(activity, "授权请求失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String[] tGrant, @NonNull String[] tGrantName, GrantingCallBack callBack) {
        requestPermissions(activity, requestCode, tGrant, tGrantName, true, callBack);
    }

    /**
     * 请求单个授权
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String tGrant, @NonNull String tGrantName, GrantingCallBack callBack) {
        requestPermissions(activity, requestCode, new String[]{tGrant}, new String[]{tGrantName}, callBack);
    }


    public static final String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String BLUETOOTH = Manifest.permission.BLUETOOTH;
    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String PHONE = Manifest.permission.CALL_PHONE;

    /**
     * 请求多个授权, 不需描述。
     * 描述不全面，如需跟多权限，需在此添加
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String[] tGrant, GrantingCallBack callBack) {
        Map<String, String> grants = new TreeMap<>();
        grants.put(STORAGE, "内存");
        grants.put(BLUETOOTH, "蓝牙");
        grants.put(CAMERA, "相机");
        grants.put(PHONE, "拨号");

        String[] tGrantName = new String[tGrant.length];
        for (int i = 0; i < tGrant.length; i++) {
            tGrantName[i] = grants.get(tGrant[i]);
        }
        requestPermissions(activity, requestCode, tGrant, tGrantName, callBack);
    }

    /**
     * 请求单个授权, 不需描述
     * 描述不全面，如需跟多权限，需在{@link #requestPermissions(Activity, int, String[], GrantingCallBack)}添加
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String tGrant, GrantingCallBack callBack) {
        requestPermissions(activity, requestCode, new String[]{tGrant}, callBack);
    }

    /**
     * 处理授权结果
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull final int[] grantResults) {
        if (granting != null) {
//            granting.callBack.onGrantSuccess(granting.checkResult(grantResults));

            if (granting.checkResult(grantResults)) {
                if (granting.callBack != null)
                    granting.callBack.onGrantSuccess(true);
                granting = null;
            } else {
                if (!granting.isShowErrorTip) {
                    granting.callBack.onGrantSuccess(false);
                    granting = null;
                } else {
                    String message = "您未全部授权相关权限，您可以在设置中打开相关权限。";

                    new AlertDialog.Builder(granting.mActivity)
                            .setMessage(message)
                            .setPositiveButton("前去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (granting.callBack != null)
                                        granting.callBack.onGrantSuccess(false);
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.fromParts("package", granting.mActivity.getPackageName(), null));
                                    granting.mActivity.startActivity(intent);

                                    granting = null;
                                }
                            })
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (granting.callBack != null)
                                        granting.callBack.onGrantSuccess(false);
                                    granting = null;
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            }
        } else {
            granting = null;
        }
    }
}
