package com.kiven.sample;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kiven.kutils.activityHelper.activity.KRoboActivity;
import com.kiven.kutils.file.KFile;
import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KAlertDialogHelper;
import com.kiven.kutils.tools.KGranting;
import com.kiven.kutils.tools.KPath;
import com.kiven.kutils.tools.KUtil;
import com.kiven.sample.floatView.ActivityHFloatView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import dalvik.system.PathClassLoader;
import roboguice.RoboGuice;

public class LauchActivity extends KRoboActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch);
        RoboGuice.setUseAnnotationDatabases(false);
        KUtil.printDeviceInfo();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_setings:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
            case R.id.item_phone:
                KGranting.requestPermissions(this, 101, Manifest.permission.CALL_PHONE, "拨号", new KGranting.GrantingCallBack() {
                    @Override
                    public void onGrantSuccess(boolean isSuccess) {
                        String phoneno = "17012347428";
                        if (isSuccess) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneno));
                            startActivityForResult(intent, 1234);

                            // 与拨号并行，检测sim卡状态
                            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Activity.TELEPHONY_SERVICE);
                            int simState = telephonyManager.getSimState();
                            if (simState == TelephonyManager.SIM_STATE_ABSENT
                                    || simState == TelephonyManager.SIM_STATE_UNKNOWN) {
                                Toast.makeText(LauchActivity.this, "未检测到sim卡或当前sim卡不可用，请另行拨号" + phoneno, Toast.LENGTH_LONG);
                            }
                        }
                    }
                });
                break;
            case R.id.item_mp4:
                new VideoSurfaceDemo().startActivity(this);
                break;
            case R.id.item_float:
                new ActivityHFloatView().startActivity(this);
                break;
            case R.id.item_upload_image:
                KGranting.requestPermissions(this, 345, Manifest.permission.READ_EXTERNAL_STORAGE, "存储空间", new KGranting.GrantingCallBack() {
                    @Override
                    public void onGrantSuccess(boolean isSuccess) {
                        if (isSuccess) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//4.4及以上最好使用 ACTION_OPEN_DOCUMENT
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/jpeg");
                            startActivityForResult(intent, 345);
                        }
                    }
                });
                break;
            case R.id.item_path:
                KGranting.requestPermissions(this, 345, Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间", new KGranting.GrantingCallBack() {
                    @Override
                    public void onGrantSuccess(boolean isSuccess) {
                        if (isSuccess) {
                            KLog.i("" + KFile.createFile("tmp", ".img", getDir(Environment.DIRECTORY_PICTURES, 2)).getAbsolutePath());
                            KLog.i("" + KFile.createFile("tmp", ".img", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getAbsolutePath());
                            KLog.i("" + KFile.createFile("tmp", ".img", getDatabasePath("db")).getAbsolutePath());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                KLog.i("" + KFile.createFile("tmp", ".img", getDataDir()).getAbsolutePath());
                            }
                        }
                    }
                });
                break;
            case R.id.item_widget:
                KAlertDialogHelper.Show1BDialog(this, "在系统widget中去选择要显示的widget");


                break;
            default:
                new ActivityHTestBase().startActivity(this);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == 345) {
            final String path = KPath.getPath(this, data.getData());
            KLog.i(path);

            try {
                URL url = new URL("http://www.baidu.com");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

            } catch (IOException e) {
                e.printStackTrace();
            }

            Dialog dialog = new Dialog(this) {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    ImageView imageView = new ImageView(getContext());
                    setContentView(imageView);
                    x.image().bind(imageView, path);

                    setTitle("已选图片");
                }
            };
            dialog.show();


        }
    }

    private void requestNet() {
        RequestParams params = new RequestParams("http://192.168.0.113:8080/index.jsp");//http://localhost:8080/greeting?name=Kiven
//            params.addBodyParameter("file", new File(path));
//            params.addBodyParameter("name", KString.nowDateStr());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                KLog.i("success: " + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                KLog.i("onError");
                KLog.e(new Exception(ex));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                KLog.i("onCancelled");
            }

            @Override
            public void onFinished() {
                KLog.i("onFinished");
            }
        });
    }
}
