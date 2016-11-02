package com.kiven.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.kiven.kutils.activityHelper.activity.KRoboActivity;
import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KGranting;
import com.kiven.sample.floatView.ActivityHFloatView;

import roboguice.RoboGuice;

public class LauchActivity extends KRoboActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch);
        RoboGuice.setUseAnnotationDatabases(false);
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
            default:
                new ActivityHTestBase().startActivity(this);
                break;
        }

        KLog.i("{\"name\":\"kiven\", \"hh\":[\"yy\", \"66\"]}");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
