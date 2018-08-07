package com.kiven.sample.floatView;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kiven.kutils.tools.KUtil;

/**
 * 学习文档：TODO http://blog.csdn.net/stevenhu_223/article/details/8504058
 *
 * Created by kiven on 2016/11/1.
 */

public class FloatView {
    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

//    Button mFloatView;

    Context context;
    private static final String TAG = "FloatView";

    public FloatView(Context context, WindowManager windowManager) {
        this.context = context;
        mWindowManager = windowManager;

        createParams();
        createFloatView();
    }

    private void createParams() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
//        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;// TYPE_APPLICATION 才是activity内
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
//        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.gravity = Gravity.CENTER;

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
         /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/
    }

    private void createFloatView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        //获取浮动窗口视图所在布局
//        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mFloatLayout = new LinearLayout(context);
        //浮动窗口按钮
//        mFloatView = (Button)mFloatLayout.findViewById(R.id.float_id);
        Button mFloatView = new Button(context);
        mFloatView.setText("省");

        mFloatLayout.addView(mFloatView, new LinearLayout.LayoutParams(KUtil.dip2px(context, 50), KUtil.dip2px(context, 50)));

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {

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
                        int b = KUtil.dip2px(context, 1) / 2;
                        float x = event.getRawX() - oldX;
                        float y = event.getRawY() - oldY;
                        if (Math.abs(x) > 1 && Math.abs(y) > 1) {
                            wmParams.x = (int) (oldX1 + x);
                            wmParams.y = (int) (oldY1 + y);
                        }
                        break;
                    default:
                        int a = KUtil.dip2px(context, 2);
                        if (Math.abs(wmParams.x - oldX1) <= a && Math.abs(wmParams.y - oldY1) <= a) {// 认定单点击
                            Toast.makeText(context, "onClickss", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                //刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);

                return true;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        mFloatView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "onClick", Toast.LENGTH_SHORT).show();
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
            mWindowManager.removeView(mFloatLayout);
            isShow = false;
        }
    }

    public boolean isShow() {
        return isShow;
    }
}
