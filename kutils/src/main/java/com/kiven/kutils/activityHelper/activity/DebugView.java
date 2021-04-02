package com.kiven.kutils.activityHelper.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.kiven.kutils.R;
import com.kiven.kutils.logHelper.ACheckRes;
import com.kiven.kutils.logHelper.AHFileManager;
import com.kiven.kutils.logHelper.KLog;
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
                    say = "停止方法追踪";
                    Debug.stopMethodTracing();
                } else {
                    hasStartMethodTracing = true;
                    say = "开始方法追踪";
                    //系统将在getExternalFilesDir() 目录下生成 .trace 文件，一般都在 ~/sdcard/Android/data/$packname/files 目录中
                    Debug.startMethodTracing("KUtilsMethodTracing-" + SimpleDateFormat.getDateTimeInstance().format(new Date()));
                }
                Object a;

                Snackbar.make(activity.getWindow().getDecorView(), say, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public DebugView(@NonNull Activity context) {
        // 在这里获取android.R.id.content并持有，会影响状态栏的颜色
//        rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }

//    private final FrameLayout rootView;
    private static FrameLayout getRootView(@NonNull Activity activity) {
        // 不同的时候，android.R.id.content 指向的view不同。
        return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content).getParent().getParent();
//        return rootView;
    }

    private static View findFloatViewById(@NonNull Activity activity, @IdRes int id) {
        FrameLayout rv = getRootView(activity);
        int count = rv.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = rv.getChildAt(i);
            if (v.getId() == id) {
                return v;
            }
        }

        return null;
    }

    /**
     * 渐变颜色条
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void addBreathBtn(@NonNull final Activity activity) {
        View vv = findFloatViewById(activity, R.id.k_bar_view);

        if (vv != null && vv.getTag() != null && vv.getTag() instanceof ValueAnimator) {
            return;
        }

        final float scale = activity.getResources().getDisplayMetrics().density;
        int height = (int) (24 * scale + 0.5f);

        final ImageView barView = new ImageView(activity);
        barView.setImageResource(R.drawable.k_ic_action_bar);
        barView.setScaleType(ImageView.ScaleType.MATRIX);
        barView.setOnTouchListener(new View.OnTouchListener() {
            float oldY;
            float oldY1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldY = event.getRawY();
                        oldY1 = v.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float y = event.getRawY() - oldY;
                        if (Math.abs(y) > 1) {
                            v.setY(oldY1 + y);
                        }
                        break;
                    default:
                        int a = KUtil.dip2px(2);
                        if (Math.abs(v.getY() - oldY1) <= a) {// 认定单点击
                            showFloat(activity);
                        }
                        break;
                }

                return true;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        // 加个动画
//        ObjectAnimator animator = ObjectAnimator.ofFloat(barView, "alpha", 0f, 0f, 0.3f, 1.0f, 0f);
        final int color1 = Color.parseColor("#331111FF");
        int color2 = Color.parseColor("#33FF0000");
        int color3 = Color.TRANSPARENT;

        ValueAnimator barAnimator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            barView.setImageTintList(new ColorStateList(new int[][]{new int[]{}}, new int[]{color1}));

//            animator = ObjectAnimator.ofArgb(barView, "backgroundColor", color1, color2, color2, color3, color3, color3, color1);
            barAnimator = ValueAnimator.ofArgb(color1, color2, color2, color3, color3, color3, color1);
            barAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    barView.setImageTintList(new ColorStateList(new int[][]{new int[]{}}, new int[]{(int) animation.getAnimatedValue()}));
                }
            });
        } else {
            barAnimator = ObjectAnimator.ofFloat(barView, "alpha", 0f, 0f, 0.3f, 1.0f, 0f);
        }
        barAnimator.setDuration(5500);
        barAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        barAnimator.start();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(height/2, height);
        layoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        layoutParams.topMargin = height;
        getRootView(activity).addView(barView, layoutParams);

        //
        barView.setId(R.id.k_bar_view);
        barView.setTag(barAnimator);
    }

    private static LinearLayout.LayoutParams createButtonParam(int childSize, int margin) {
        int size = childSize - 2 * margin;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(margin, margin, margin, margin);
        return params;
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void showFloat(@NonNull final Activity activity) {


        // todo 所有选项，初始化FloatView，装载数据
        final List<DebugEntity> actions = new ArrayList<DebugEntity>();

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
                hideFloat(activity);
            }
        }));







        //获取浮动窗口视图所在布局


        final LinearLayout mFloatLayout;

        View vv = findFloatViewById(activity, R.id.k_float_layout);
        if (vv != null) {
            if (!(vv instanceof LinearLayout)) {
                // 发现了类型不是LinearLayout的view，说明出现冲突了
                KLog.e("R.id.k_float_layout 控件类型不对");
                return;
            }
        }

        if (vv == null) {
            mFloatLayout = new LinearLayout(activity);
            mFloatLayout.setId(R.id.k_float_layout);


            FrameLayout.LayoutParams mFloatLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            mFloatLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            mFloatLayoutParams.topMargin = KUtil.dip2px(35);

            getRootView(activity).addView(mFloatLayout, mFloatLayoutParams);
        } else {
            mFloatLayout = (LinearLayout) vv;

            mFloatLayout.removeAllViews();
            mFloatLayout.setVisibility(View.VISIBLE);
        }

        final int childSize = KUtil.dip2px(35);
        int padding = KUtil.dip2px(2);
        for (DebugEntity action : actions) {
            TextView textView = new TextView(activity);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.BLUE);
            if (action.isIcon()) {
                textView.setText("");
                textView.setBackgroundResource(action.getResId());
            } else {
                textView.setText(action.getText());
                textView.setBackgroundResource(R.mipmap.k_bg_blank_circle);
            }
            mFloatLayout.addView(textView, createButtonParam(childSize, padding));
        }

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //设置监听浮动窗口的触摸移动
        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {

            float oldX, oldY;
            float oldX1, oldY1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getRawX();
                        oldY = event.getRawY();

                        oldX1 = mFloatLayout.getX();
                        oldY1 = mFloatLayout.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = event.getRawX() - oldX;
                        float y = event.getRawY() - oldY;
                        if (Math.abs(x) > 1 && Math.abs(y) > 1) {
                            mFloatLayout.setX(oldX1 + x);
                            mFloatLayout.setY(oldY1 + y);
                        }
                        break;
                    default:
                        int a = KUtil.dip2px(2);
                        if (Math.abs(mFloatLayout.getX() - oldX1) <= a && Math.abs(mFloatLayout.getY() - oldY1) <= a) {// 认定单点击
                            int position = ((int) event.getX()) / childSize;
                            if (position >= 0 && position < actions.size()) {
                                actions.get(position).onClick(activity, mFloatLayout.getChildAt(position));
                            }
                        }
                        break;
                }

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

    public static void hideFloat(@NonNull Activity activity) {
        View vv = findFloatViewById(activity, R.id.k_float_layout);
        if (vv != null) {
            if (vv instanceof LinearLayout) {
                vv.setVisibility(View.GONE);
            }
        }
    }

    public static boolean isShow(@NonNull Activity activity) {
        View vv = findFloatViewById(activity, R.id.k_float_layout);
        if (vv != null) {
            if (vv instanceof LinearLayout) {
                return vv.getVisibility() == View.VISIBLE;
            }
        }

        return false;
    }

    public static void onDestroy(@NonNull Activity activity) {
        // ValueAnimator需要手动停止，否则会内存溢出。ObjectAnimator是自动停止。
        // 高系统用的ValueAnimator，低系统用的ObjectAnimator。所以这里还是手动取消一下。
        View vv = findFloatViewById(activity, R.id.k_bar_view);

        if (vv != null && vv.getTag() != null && vv.getTag() instanceof ValueAnimator) {
            ((ValueAnimator) vv.getTag()).cancel();
        }
    }
}
