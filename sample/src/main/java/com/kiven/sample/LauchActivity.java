package com.kiven.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.appcompat.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kiven.kutils.activityHelper.KFragmentActivity;
import com.kiven.kutils.activityHelper.activity.KActivity;
import com.kiven.kutils.callBack.CallBack;
import com.kiven.kutils.file.KFile;
import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KAlertDialogHelper;
import com.kiven.kutils.tools.KGranting;
import com.kiven.kutils.tools.KPath;
import com.kiven.kutils.tools.KUtil;
import com.kiven.kutils.tools.KView;
import com.kiven.kutils.widget.KNormalItemView;
import com.kiven.sample.floatView.ActivityHFloatView;
import com.kiven.sample.gl.AHGL;
import com.kiven.sample.libs.AHLibs;
import com.kiven.sample.media.AHMediaList;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import me.grantland.widget.AutofitHelper;

@ContentView(R.layout.activity_lauch)
public class LauchActivity extends KActivity {

    private static final String FILEPROVIDER_AUTHORITY = "com.kiven.sample.fileprovider";
    private static final String IMAGE_DIR = "KUtilSampleFile" + File.separator + "testImage";

    @ViewInject(R.id.iv_test)
    private ImageView iv_test;
    @ViewInject(R.id.et_auto)
    private EditText et_auto;
    @ViewInject(R.id.textView2)
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        KUtil.printDeviceInfo();

        setupWindowAnimations();

        AutofitHelper.create(et_auto);


        iv_test.setOnClickListener(new View.OnClickListener() {
            int count = 0;

            @Override
            public void onClick(View v) {
                String url = "http://b.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513db777cf78376d55fbb3fbd9b3.jpg";
                String url2 = "http://file5.gucn.com/file2/ShopLogoFile/20120413/Gucn_20120413327888131819Logo.jpg";
                ImageOptions options = new ImageOptions.Builder()
                        .setCircular(true)
                        .setAutoRotate(true).setFadeIn(true).build();
                x.image().bind(iv_test, count % 2 == 0 ? url : url2, options);
                count++;
            }
        });

        textView2.setText(Html.fromHtml(getString(R.string.text_test, 5, 9)));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private void setupWindowAnimations() {
        // Re-enter transition is executed when returning to this activity
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide();
            slideTransition.setSlideEdge(Gravity.LEFT);
            slideTransition.setDuration(500);
            getWindow().setReenterTransition(slideTransition);
            getWindow().setExitTransition(slideTransition);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_small_action:
                new AHSmallAction().startActivity(this);
                break;
            case R.id.item_libs:
                new AHLibs().startActivity(this);
                break;
            case R.id.item_cpu:
                new AHCPUMemory().startActivity(this);
                break;
            case R.id.item_opengl:
                new AHGL().startActivity(this);
                break;
            case R.id.item_live_data:
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {

                        KView.runUI(LauchActivity.this, new CallBack() {
                            @Override
                            public void callBack() {
                                KAlertDialogHelper.Show1BDialog(LauchActivity.this, "LiveData 行不行？");
                            }
                        });
                        return true;
                    }
                });
                handler.sendEmptyMessageDelayed(0, 5000);
                break;
            case R.id.item_load_activity:
                new AHCheckRes().startActivity(this);
//                new ACheckRes().startActivity(this);
                break;
            case R.id.item_fragment_proxy:
                Intent fproxyIntent = new Intent(this, KFragmentActivity.class);
                fproxyIntent.putExtra("fragment_name", FragmentApple.class.getName());
                startActivity(fproxyIntent);
                break;
            case R.id.item_setings:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
            case R.id.item_phone:
                KGranting.requestPermissions(this, 101, Manifest.permission.CALL_PHONE, "拨号", new KGranting.GrantingCallBack() {
                    @SuppressLint("MissingPermission")
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
            case R.id.item_media:
                new AHMediaList().startActivity(this);
                break;
            case R.id.item_float:
                new ActivityHFloatView().startActivity(this);
                break;
            case R.id.item_path:
                KGranting.requestPermissions(this, 345, Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间", new KGranting.GrantingCallBack() {
                    @Override
                    public void onGrantSuccess(boolean isSuccess) {
                        if (isSuccess) {
                            KLog.i("" + KFile.createFile("tmp", ".img", getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE)).getAbsolutePath());
                            KLog.i("" + KFile.createFile("tmp", ".img", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getAbsolutePath());
                            KLog.i("" + KFile.createFile("tmp", ".img", getDatabasePath("db")).getAbsolutePath());
                            KLog.i("" + KFile.createFile("tmp", ".img", getCacheDir()).getAbsolutePath());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                KLog.i("" + KFile.createFile("tmp", ".img", getDataDir()).getAbsolutePath());
                            }
                        }
                    }
                });
                break;
            case R.id.item_widget:
                KAlertDialogHelper.Show1BDialog(this, "在系统widget中去选择要显示的widget", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        manager.killBackgroundProcesses(getPackageName());*/

                        KLog.i("a = " + a);
                        KLog.i("b = " + b);

                        /*Intent intent = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent); // 1秒钟后重启应用*/

                        a = "66666";
                        b = 7777;

                        Process.killProcess(Process.myPid());
                    }
                });
                break;
            case R.id.item_data_binding:
//                startActivity(new Intent(this, ActivityDataBinding.class));
                KNormalItemView itemView = (KNormalItemView) view;
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, new Pair(itemView.textViewName, "text_transition_name"));
                ActivityCompat.startActivity(this, new Intent(this, ActivityDataBinding.class), optionsCompat.toBundle());

                break;

            case R.id.item_recycler_view:
                new ActivityCustomRecyclerView().startActivity(this);
                break;
            case R.id.item_flyco_dialog:
                TextView textView = new TextView(this);
                textView.setText("Hello World!");
                Dialog dialog = new AlertDialog.Builder(this, R.style.Dialog_Nobackground)
                        .setView(textView).create();
                dialog.show();
                break;

            case R.id.item_volley:
                // https://developer.android.google.cn/training/volley/simple.html
                // https://github.com/google/volley
                volley("https://github.com/google/volley");
                volley("http://blog.csdn.net/linmiansheng/article/details/21646753");
                break;
            case R.id.item_helper_test:
                new ActivityHTestBase().startActivity(this);
                break;
            default:
                new ActivityHTestBase().startActivity(this);
                break;
        }
    }

    RequestQueue queue;

    private void volley(final String http) {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        StringRequest request = new StringRequest(Request.Method.GET, http, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("ULog_default", http + DateFormat.getTimeInstance().format(new Date()));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ULog_default", http + DateFormat.getTimeInstance().format(new Date()));
            }
        });
        queue.add(request);
    }

    public static String a;
    public static int b;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == 345) {
            final String path = KPath.getPath(this, data.getData());
            KLog.i(path);

            if (path.endsWith(".mp4")) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(path, new HashMap<String, String>());
                    Bitmap bmp = retriever.getFrameAtTime(1);
                    ImageView iv_test = findViewById(R.id.iv_test);
                    iv_test.setImageDrawable(new BitmapDrawable(bmp));
                } catch (Exception e) {
                    e.printStackTrace();
                    KLog.e(e);
                }
            } else
                showImage(path);
        }
    }

    protected void showImage(final String imagePath) {
        Dialog dialog = new Dialog(this) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                ImageView imageView = new ImageView(getContext());
                setContentView(imageView);
                if (imagePath.endsWith(".mp4")) {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    imageView.setImageBitmap(bitmap);
                } else
                    x.image().bind(imageView, imagePath);

                setTitle("已选图片");
            }
        };
        dialog.show();
    }*/
}
