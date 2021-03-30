package com.kiven.kutils.activityHelper.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.snackbar.Snackbar;
import com.kiven.kutils.R;
import com.kiven.kutils.logHelper.ACheckRes;
import com.kiven.kutils.logHelper.AHFileManager;
import com.kiven.kutils.logHelper.KShowLog;
import com.kiven.kutils.tools.KUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 学习文档：TODO http://blog.csdn.net/stevenhu_223/article/details/8504058
 * <p>
 * Created by kiven on 2016/11/1.
 */

public class DebugView {
    // todo 自定义选项
    protected final static List<DebugEntity> customAction = new ArrayList<DebugEntity>();

    public static void addAction(@DrawableRes int resId, DebugViewListener callBack) {
        customAction.add(new DebugEntity(resId, callBack));
    }

    public static void addAction(@NonNull String text, DebugViewListener callBack) {
        customAction.add(new DebugEntity(text, callBack));
    }

    static {
        // todo 固定选项：日志，Profiler
        addAction(R.mipmap.k_ic_text_log, new DebugViewListener() {
            @Override
            public void onClick(Activity activity, View view, DebugEntity entity) {
                new KShowLog().startActivity(activity);
            }
        });
        addAction(R.mipmap.k_ic_source, new DebugViewListener() {
            @Override
            public void onClick(Activity activity, View view, DebugEntity entity) {
                new ACheckRes().startActivity(activity);
            }
        });
        addAction(R.mipmap.k_ic_dir, new DebugViewListener() {
            @Override
            public void onClick(Activity activity, View view, DebugEntity entity) {
                new AHFileManager().startActivity(activity);
            }
        });
        addAction(R.mipmap.k_ic_night, new DebugViewListener() {
            @Override
            public void onClick(Activity activity, View view, DebugEntity entity) {
                new AlertDialog.Builder(activity).setItems(new String[]{"跟随系统", "根据节电模式", "白天", "黑夜", "不指定"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int nm = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                        switch (which) {
                            case 1:
                                nm = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;// 节能模式时是黑暗主题，否则是白亮主题。
                                break;
                            case 2:
                                nm = AppCompatDelegate.MODE_NIGHT_NO;
                                break;
                            case 3:
                                nm = AppCompatDelegate.MODE_NIGHT_YES;
                                break;
                            case 4:
                                nm = AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;// 设置为全局为不指定，应该会使用系统的夜间模式。
                                break;
                        }

                        AppCompatDelegate.setDefaultNightMode(nm); // 设置全局的夜间模式
                    }
                }).setTitle("设置夜间模式").show();
            }
        });
        addAction(R.mipmap.k_ic_info, new DebugViewListener() {
            @Override
            public void onClick(Activity activity, View view, DebugEntity entity) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                activity.startActivity(intent);
            }
        });
        addAction(R.mipmap.k_ic_profiler_open, new DebugViewListener() {
            private boolean hasStartMethodTracing = false;

            @Override
            public void onClick(Activity activity, View view, DebugEntity entity) {
                String say;
                if (hasStartMethodTracing) {
                    hasStartMethodTracing = false;
                    say = "Stop method tracing";
                    Debug.stopMethodTracing();
                } else {
                    hasStartMethodTracing = true;
                    say = "Start method tracing";
                    //系统将在getExternalFilesDir() 目录下生成 .trace 文件，一般都在 ~/sdcard/Android/data/$packname/files 目录中
                    Debug.startMethodTracing("KUtilsMethodTracing-" + SimpleDateFormat.getDateTimeInstance().format(new Date()));
                }
                Object a;

                Snackbar.make(activity.getWindow().getDecorView(), say, Snackbar.LENGTH_LONG).show();
            }
        });
    }


    // todo 所有选项，初始化FloatView，装载数据
    private List<DebugEntity> actions = new ArrayList<DebugEntity>();

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    LinearLayout barLayout;
    WindowManager.LayoutParams barParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

//    Button mFloatView;

    Activity activity;
    private static final String TAG = "FloatView";

    public DebugView(@NonNull Activity context) {
        this.activity = context;
        mWindowManager = context.getWindowManager();

        // todo 装载自定义选项
//        actions.addAll(customAction);

        List<DebugEntity> showActions = DebugConst.getQuickActions();
        if (showActions.isEmpty()) {
            if (customAction.size() > DebugConst.maxQuickShow) {
                actions.addAll(customAction.subList(0, DebugConst.maxQuickShow));
            } else {
                actions.addAll(customAction);
            }
        } else {
            actions.addAll(showActions);
        }

        // todo 更多按钮
        actions.add(new DebugEntity(R.mipmap.k_ic_more, new DebugViewListener() {
            @Override
            public void onClick(Activity activity, View view, DebugEntity entity) {
                new KAHDebugActionEdit().startActivity(activity);
            }
        }));
        // todo 关闭按钮
        actions.add(new DebugEntity(R.drawable.k_ic_close, new DebugViewListener() {
            @Override
            public void onClick(Activity activity, View view, DebugEntity entity) {
                hideFloat();
            }
        }));

        wmParams = createParams();
        createFloatView();

        addDropDownBar();
    }

    /**
     * 渐变颜色条
     */
    private void addDropDownBar() {
        final float scale = activity.getResources().getDisplayMetrics().density;
        int height = (int) (5 * scale + 0.5f);

        View barView = new View(activity);
        int color1 = Color.parseColor("#11111111");
        barView.setBackgroundColor(color1);
        barView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showFloat();
                return true;
            }
        });

        // 加个动画
//        ObjectAnimator animator = ObjectAnimator.ofFloat(barView, "alpha", 0f, 0f, 0.3f, 1.0f, 0f);
        int color2 = Color.parseColor("#11FF0000");
        int color3 = Color.TRANSPARENT;
        ObjectAnimator animator;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animator = ObjectAnimator.ofArgb(barView, "backgroundColor", color1, color2, color2, color3, color3, color3, color1);
        } else {
            animator = ObjectAnimator.ofFloat(barView, "alpha", 0f, 0f, 0.3f, 1.0f, 0f);
        }
        animator.setDuration(5500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();

        barLayout = new LinearLayout(activity);
        barLayout.addView(barView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

        barParams = createParams();
        barParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        barParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowManager.addView(barLayout, barParams);
    }

    private WindowManager.LayoutParams createParams() {
        WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
//        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wlp.type = WindowManager.LayoutParams.TYPE_APPLICATION;// TYPE_APPLICATION 才是activity内
        //设置图片格式，效果为背景透明
        wlp.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wlp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
//        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wlp.gravity = Gravity.CENTER;

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wlp.x = 0;
        wlp.y = 0 - KUtil.getScreenHeight() / 2;

        //设置悬浮窗口长宽数据
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
         /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/

        return wlp;
    }

    private LinearLayout.LayoutParams createButtonParam(int childSize, int margin) {
        int size = childSize - 2 * margin;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(margin, margin, margin, margin);
        return params;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFloatView() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        //获取浮动窗口视图所在布局
//        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mFloatLayout = new LinearLayout(activity);
        //浮动窗口按钮
//        mFloatView = (Button)mFloatLayout.findViewById(R.id.float_id);

        final int childSize = KUtil.dip2px(35);
        int padding = KUtil.dip2px(2);
        for (DebugEntity action : actions) {
            if (action.isIcon()) {
                ImageView imageView = new ImageView(activity);
                imageView.setImageResource(action.getResId());
                mFloatLayout.addView(imageView, createButtonParam(childSize, padding));
            } else {
                TextView textView = new TextView(activity);
                textView.setText(action.getText());
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.BLUE);
                textView.setBackgroundResource(R.mipmap.k_bg_blank_circle);
                mFloatLayout.addView(textView, createButtonParam(childSize, padding));
            }
        }

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //设置监听浮动窗口的触摸移动
        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {

            float oldX, oldY;
            int oldX1, oldY1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getRawX();
                        oldY = event.getRawY();

                        oldX1 = wmParams.x;
                        oldY1 = wmParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = event.getRawX() - oldX;
                        float y = event.getRawY() - oldY;
                        if (Math.abs(x) > 1 && Math.abs(y) > 1) {
                            wmParams.x = (int) (oldX1 + x);
                            wmParams.y = (int) (oldY1 + y);
                        }
                        break;
                    default:
                        int a = KUtil.dip2px(2);
                        if (Math.abs(wmParams.x - oldX1) <= a && Math.abs(wmParams.y - oldY1) <= a) {// 认定单点击
                            int position = ((int) event.getX()) / childSize;
                            if (position >= 0 && position < actions.size()) {
                                actions.get(position).onClick(activity, mFloatLayout.getChildAt(position));
                            }
                        }
                        break;
                }

                //刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);

                return true;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        mFloatLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(activity, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean isShow = false;

    public void showFloat() {
        if (!isShow) {
            //添加mFloatLayout
            mWindowManager.addView(mFloatLayout, wmParams);
            isShow = true;
        }
    }

    public void hideFloat() {
        if (isShow) {
            // removeViewImmediate 通知View立刻调用View.onDetachWindow(), 但是不能再调用addView
            // 当前界面动态刷新的时候，removeView没有立刻调用View.onDetachWindow()，就会出现异常
            // 出现问题的点：切换黑夜模式的时候，由于老的view没有解绑，mWindowManager.addView会打印错误，不过不会崩溃。
            mWindowManager.removeView(mFloatLayout);
            isShow = false;
        }
    }

    public void onResume() {
        if (barLayout.getParent() == null) {
            mWindowManager.addView(barLayout, barParams);
        }
    }

    public void onPause() {
        if (barLayout.getParent() != null) {
            // removeViewImmediate 通知View立刻调用View.onDetachWindow(), 但是不能再调用addView
            // 当前界面动态刷新的时候，removeView没有立刻调用View.onDetachWindow()，就会出现异常
            // 出现问题的点：切换黑夜模式的时候，由于老的view没有解绑，mWindowManager.addView会打印错误，不过不会崩溃。
            mWindowManager.removeView(barLayout);
        }
        hideFloat();
    }

    public boolean isShow() {
        return isShow;
    }
}
