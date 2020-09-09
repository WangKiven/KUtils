package com.kiven.kutils.logHelper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.kiven.kutils.R;
import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;

import java.io.RandomAccessFile;

import static android.content.Context.BATTERY_SERVICE;

public class KCPUMem extends KActivityHelper {
    private TextView showText;

    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        activity.setTheme(R.style.Theme_AppCompat_NoActionBar);

        LinearLayout ui = new LinearLayout(activity);
        ui.setOrientation(LinearLayout.VERTICAL);
        setContentView(ui);

        Toolbar toolbar = new Toolbar(activity);
        toolbar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ui.addView(toolbar);

        initBackToolbar(toolbar);

        showText = new TextView(activity);
        ui.addView(showText);

        Button runtimeGc = new Button(activity);
        runtimeGc.setText("Runtime.gc()");
        runtimeGc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runtime.getRuntime().gc();
            }
        });
        ui.addView(runtimeGc);

        Button systemGc = new Button(activity);
        systemGc.setText("System.gc()");
        systemGc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.gc();
            }
        });
        ui.addView(systemGc);

        /*final Button refresh = new Button(activity);
        refresh.setText("刷新");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        ui.addView(refresh);*/

        start();
    }

    private void start() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                int batterLevel = getBatteryLevel();
                while (!isExit) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("电量 = ").append(batterLevel).append("%\n");
                    sb.append("cpu = ").append(cpu).append("%(Permission denied)\n");


                    // 内存
                    ActivityManager activityManager = (ActivityManager) mActivity.getSystemService(Activity.ACTIVITY_SERVICE);

                    Debug.MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
                    if (memInfo.length > 0) {
                        int totalPss = memInfo[0].getTotalPss();
                        if (totalPss >= 0) {
                            sb.append("mem = ").append(totalPss / 1024.0).append(" M (总体使用内存，含非java)\n");
                        }
                    }

                    sb.append("最大分配内存").append(activityManager.getMemoryClass()).append("M(第一种获取方法)\n");
                    sb.append("最大分配内存").append(activityManager.getLargeMemoryClass()).append("M(第一种获取方法, 开启largeHeap时)\n");

                    // Runtime 获取的是jVM里的内存情况
                    Runtime runtime = Runtime.getRuntime();
                    sb.append("最大分配内存").append(toM(runtime.maxMemory())).append("M(第2种获取方法)\n");

                    sb.append("已分配内存").append(toM(runtime.totalMemory())).append("M\n");
                    sb.append("未使用内存").append(toM(runtime.freeMemory())).append("M\n");
                    sb.append("已使用内存").append(toM(runtime.totalMemory() - runtime.freeMemory())).append("M\n");

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showText.setText(sb.toString());
                        }
                    });
                }
            }
        }.start();
    }

    private double toM(long length) {
        return (length * 1.0) / (1024 * 1024);
    }

    private int getBatteryLevel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) mActivity.getSystemService(BATTERY_SERVICE);
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent = mActivity.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            return (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
                    intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
    }

    private double cpu = 0;
    private boolean isExit = false;

    private void cpu() {
        RandomAccessFile procStatFile = null;
        try {
            procStatFile = new RandomAccessFile("/proc/stat", "r");
        } catch (Exception ignored) {
        }
        RandomAccessFile appStatFile = null;
        try {
            appStatFile = new RandomAccessFile("/proc/" + Process.myPid() + "/stat", "r");
        } catch (Exception ignored) {
        }

        if (procStatFile != null && appStatFile != null) {

            double lastCpuTime = 0;
            double lastAppCpuTime = 0;
            while (!isExit) {
                try {
                    procStatFile.seek(0);
                    appStatFile.seek(0);

                    String procStatString = procStatFile.readLine();
                    String appStatString = appStatFile.readLine();
                    String[] procStats = procStatString.split(" ");
                    String[] appStats = appStatString.split(" ");
                    long cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3]) + Long.parseLong(procStats[4])
                            + Long.parseLong(procStats[5]) + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                            + Long.parseLong(procStats[8]);

                    long appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
                    cpu = (appTime - lastAppCpuTime) / (cpuTime - lastCpuTime) * 100;
                    lastCpuTime = cpuTime;
                    lastAppCpuTime = appTime;
                } catch (Exception ignored) {
                }
            }


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isExit = true;
    }
}
