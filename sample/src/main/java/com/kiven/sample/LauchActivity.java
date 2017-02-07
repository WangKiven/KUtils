package com.kiven.sample;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kiven.kutils.activityHelper.activity.KRoboActivity;
import com.kiven.kutils.callBack.Consumer;
import com.kiven.kutils.file.KFile;
import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KAlertDialogHelper;
import com.kiven.kutils.tools.KContext;
import com.kiven.kutils.tools.KGranting;
import com.kiven.kutils.tools.KPath;
import com.kiven.kutils.tools.KUtil;
import com.kiven.sample.floatView.ActivityHFloatView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import roboguice.RoboGuice;

public class LauchActivity extends KRoboActivity {

    private static final String FILEPROVIDER_AUTHORITY = "com.kiven.sample.fileprovider";
    private static final String IMAGE_DIR = "KUtilSample" + File.separator + "testImage";
    String cameraPath = null;
    String cropPath = null;

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
            case R.id.item_take_camera:
                String permissions[] = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                String permissionInfos[] = {"存储空间", "相机"};
                KGranting.requestPermissions(this, 345, permissions, permissionInfos, new KGranting.GrantingCallBack() {
                    @Override
                    public void onGrantSuccess(boolean isSuccess) {
                        if (isSuccess) {
                            File dir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIR);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            File file = new File(dir, System.currentTimeMillis() + ".jpg");
                            cameraPath = file.getAbsolutePath();

                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT < 24) {
                                camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            } else {
                                camera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getBaseContext(), FILEPROVIDER_AUTHORITY, file));
                            }
                            startActivityForResult(camera, 346);
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

            showImage(path);
        }

        if (requestCode == 346) {
            /*showImage(cameraPath);
            KUtil.addPicture(cameraPath, new Consumer<Integer>() {
                @Override
                public void callBack(Integer param) {
                    KLog.i("param = " + param);
                    Toast.makeText(getBaseContext(), "save " + param, Toast.LENGTH_LONG).show();
                }
            });*/

            /*KUtil.addPicture(cameraPath, new Consumer<Integer>() {
                @Override
                public void callBack(Integer param) {
                    KLog.i("param = " + param);
                    Toast.makeText(getBaseContext(), "save " + param, Toast.LENGTH_LONG).show();
                    cropImage(cameraPath);
                }
            });*/

            /*MediaScannerConnection.scanFile(KContext.getInstance(), new String[] { cameraPath }, new String[] { "image*//*" },
                    new MediaScannerConnection.OnScanCompletedListener() {

                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            cropImage(cameraPath);
                        }

                    });*/
            KUtil.addPicture(cameraPath, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    cropImage(cameraPath);
                }
            });
        }

        if (requestCode == 347) {
            showImage(cropPath);
        }
    }

    private void cropImage(String cameraPath) {
        File dir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, System.currentTimeMillis() + ".jpg");
        cropPath = file.getAbsolutePath();

        Intent in = new Intent("com.android.camera.action.CROP");

        in.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 需要裁减的图片格式
        if (Build.VERSION.SDK_INT >= 24) {
            in.setDataAndType(getImageContentUri(this, new File(cameraPath)), "image/*");
        } else {
            in.setDataAndType(Uri.fromFile(new File(cameraPath)), "image/*");
        }
        // 允许裁减
        in.putExtra("crop", "true");
        // 剪裁后ImageView显时图片的宽
        in.putExtra("outputX", 200);
        // 剪裁后ImageView显时图片的高
        in.putExtra("outputY", 200);
        // 设置剪裁框的宽高比例
        in.putExtra("aspectX", 1);
        in.putExtra("aspectY", 1);
        in.putExtra("return-data", false);
        in.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(cropPath)));
        in.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        in.putExtra("noFaceDetection", true);

        startActivityForResult(in, 347);
    }

    /**
     *
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    protected void showImage(final String imagePath) {
        Dialog dialog = new Dialog(this) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                ImageView imageView = new ImageView(getContext());
                setContentView(imageView);
                x.image().bind(imageView, imagePath);

                setTitle("已选图片");
            }
        };
        dialog.show();
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
