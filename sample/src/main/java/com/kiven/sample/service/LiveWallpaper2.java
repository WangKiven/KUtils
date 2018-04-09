package com.kiven.sample.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.kiven.sample.R;

import java.util.Random;

/**
 * Created by wangk on 2018/4/9.
 */

public class LiveWallpaper2 extends WallpaperService {
    private Bitmap bitmap;
    //  实现动态壁纸必须要实现的抽象方法
    @Override
    public Engine onCreateEngine() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        return new MyEngine();
    }
    class MyEngine extends Engine{
        private boolean mVisible;
        //  记录当前用户动作发生的位置
        private float mTouchX = -1;
        private float mTouchY = -1;
        //  记录要绘制的矩形的数量
        private int count = 1;
        //  记录第一个矩形所需坐标变换的X、Y坐标的偏移
        private int originX = 50,originY = 50;
        //  定义画笔
        private Paint mPaint = new Paint();
        Handler mHandler = new Handler();
        private final Runnable drawTarget = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            //  初始化画笔
            mPaint.setARGB(76,0,0,255);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            //  设置壁纸的触碰事件为true
            setTouchEventsEnabled(true);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            mVisible = visible;
            if(visible){
                drawFrame();
            }else{
                //  如果界面不可见，删除回调
                mHandler.removeCallbacks(drawTarget);
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            drawFrame();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            //  删除回调
            mHandler.removeCallbacks(drawTarget);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            //   检测到滑动操作
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                mTouchX = event.getX();
                mTouchY = event.getY();
            }else{
                mTouchX = -1;
                mTouchY = -1;
            }
            super.onTouchEvent(event);
        }

        private void drawFrame(){
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try{
                c = holder.lockCanvas();
                if(c != null){
                    c.drawColor(0xffffffff);
                    //  在触碰点绘制图像
                    drawTouchPoint(c);
                    mPaint.setAlpha(76);
                    c.translate(originX,originY);
                    //  采用循环绘制count个图形
                    for(int i = 0; i < count; i++){
                        c.translate(80,0);
                        c.scale(0.95f,0.95f);
                        c.rotate(20f);
                        c.drawRect(0,0,150,75,mPaint);
                    }
                }
            }finally {
                if(c != null){
                    holder.unlockCanvasAndPost(c);
                }
            }
            //  调度下一次重绘
            mHandler.removeCallbacks(drawTarget);
            if(mVisible){
                count++;
                if(count >= 50){
                    Random rand = new Random();
                    count  = 1;
                    originX += (rand.nextInt(60)-30);
                    originY += (rand.nextInt(60)-30);
                    try{
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //  每隔0.1秒执行drawTarget一次
                mHandler.postDelayed(drawTarget,100);
            }
        }
        private void drawTouchPoint(Canvas c){
            if(mTouchX >= 0 && mTouchY >= 0){
                //  设置画笔的透明度
                mPaint.setAlpha(255);
                c.drawBitmap(bitmap,mTouchX,mTouchY,mPaint);
            }
        }
    }
}
