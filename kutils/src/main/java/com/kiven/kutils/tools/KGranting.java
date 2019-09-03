package com.kiven.kutils.tools;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.widget.Toast;

import com.kiven.kutils.callBack.Consumer;
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
    // todo 全局设置，是否通过fragment请求权限。不要频繁改变该值，否则会出问题。
    //  默认false: 通过activity请求，需要在activity中配置onRequestPermissionsResult().
    //  true: activity中的onRequestPermissionsResult()必须调用super.onRequestPermissionsResult()，否则回调会出问题
    public static boolean useFragmentRequest = false;

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
                    grantName.add(tGrantName[i]);
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

            new AlertDialog.Builder(mActivity)
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions();
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
            requestPermissions();
        }
    }

    private void requestPermissions(){
        if (useFragmentRequest) {
            granting = null;

            if (mActivity instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) mActivity;
                RequestPermissionFragment.requestPermissions(fragmentActivity.getSupportFragmentManager(), waitGrant, new Consumer<Boolean>() {
                    @Override
                    public void callBack(Boolean param) {
                        onResult(param);
                    }
                });
            }

            KLog.e("使用fragment请求权限，请使用FragmentActivity作为activity传入");
        } else
            ActivityCompat.requestPermissions(mActivity, waitGrant,
                    requestCode);
    }

    private void onResult(boolean isSuccess) {
        if (isSuccess) {
            if (callBack != null) {
                callBack.onGrantSuccess(true);
            }
        } else {
            if (!isShowErrorTip) {
                if (callBack != null)
                    callBack.onGrantSuccess(false);
            } else {
                String message = "您未全部授权相关权限，您可以在设置中打开相关权限。";

                new AlertDialog.Builder(mActivity)
                        .setMessage(message)
                        .setPositiveButton("前去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (callBack != null)
                                    callBack.onGrantSuccess(false);
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                                mActivity.startActivity(intent);
                            }
                        })
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (callBack != null)
                                    callBack.onGrantSuccess(false);
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
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

    /**
     * 请求多个授权, 不需描述。
     * 描述不全面，如需跟多权限，需在此添加
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode, @NonNull String[] tGrant, GrantingCallBack callBack) {
        Map<String, String> grants = new TreeMap<>();
        grants.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "内存");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            grants.put(Manifest.permission.READ_EXTERNAL_STORAGE, "内存");
        }
        grants.put(Manifest.permission.BLUETOOTH, "蓝牙");
        grants.put(Manifest.permission.CAMERA, "相机");
        grants.put(Manifest.permission.CALL_PHONE, "拨号");
        grants.put(Manifest.permission.RECORD_AUDIO, "录音");

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
     * 仅检测是否有权限，不做权限申请
     */
    public static Boolean checkPermissions(@NonNull Activity activity, @NonNull String[] tGrant) {
        for (String g : tGrant) {
            if (ContextCompat.checkSelfPermission(activity, g) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 仅检测是否有权限，不做权限申请
     */
    public static Boolean checkPermission(@NonNull Activity activity, @NonNull String tGrant) {
        return ContextCompat.checkSelfPermission(activity, tGrant) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求录音需要的权限
     */
    public static void requestRecordAudioPermissions(@NonNull Activity activity, int requestCode, GrantingCallBack callBack) {
        String[] grant = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(activity, requestCode, grant, callBack);
    }

    /**
     * 请求拍照需要的权限
     */
    public static void requestTakePhotoPermissions(@NonNull Activity activity, int requestCode, GrantingCallBack callBack) {
        String[] grant = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(activity, requestCode, grant, callBack);
    }

    /**
     * 请求访问相册需要的权限
     */
    public static void requestAlbumPermissions(@NonNull Activity activity, int requestCode, GrantingCallBack callBack) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            String[] grant = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(activity, requestCode, grant, callBack);
        }
    }

    /**
     * 处理授权结果
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull final int[] grantResults) {
        if (granting != null) {

            granting.onResult(granting.checkResult(grantResults));
            granting = null;
        } else {
            granting = null;
        }
    }
}
