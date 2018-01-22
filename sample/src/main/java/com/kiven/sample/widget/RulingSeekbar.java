package com.kiven.sample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.kiven.kutils.callBack.Consumer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangk on 2018/1/22.
 */

public class RulingSeekbar extends View {
    /*public RulingSeekbar(Context context) {
        super(context);
    }*/

    public RulingSeekbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    int min = 60;
    int max = 100;

    Consumer<Integer> onChange;

    private final List<Node> nodes = new ArrayList<>();

    Paint paint = new Paint();

    private void init(Context context, AttributeSet attrs) {
        addNode(60, 0, true);
        addNode(70, 2, true);
        addNode(77, 1, true);
        addNode(85, 1, false);
        addNode(90, 2, true);
    }

    /*public RulingSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulingSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    private float value2Position(int value) {
        if (min == max) {
            return 0;
        }
        return barLeft + (barRight - barLeft) * ((value - min * 1.0f) / (max - min));
    }

    private int position2Value(float position) {
        if (barRight == barLeft) {
            return 0;
        }

        float v = (max - min) * ((position - barLeft) / (barRight - barLeft));
        int y = (int) v;
        if (v - y >= 0.5) {
            y = y + 1;
        }
        return min + y;
    }

    float curPotionX = 0;// 手柄位置

    float firstPostionX;// 触摸开始的位置
    float startPosion;// 触摸开始时，手柄的位置

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(curPotionX - event.getX()) > 20) {
                    return false;
                }
                firstPostionX = event.getX();
                startPosion = curPotionX;
                break;
            case MotionEvent.ACTION_MOVE:
                curPotionX = startPosion + (event.getX() - firstPostionX);

                // 当前值的位置
                if (curPotionX < barLeft) {
                    curPotionX = barLeft;
                } else if (curPotionX > barRight) {
                    curPotionX = barRight;
                }
                invalidate();
                if (onCha)
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasHeight = h;
        canvasWidth = w;

        barTop = (canvasHeight - barHeigth) / 2;
        barRight = canvasWidth - barLeft;
        barBottom = barTop + barHeigth;

        // 当前值的位置
        if (curPotionX < barLeft) {
            curPotionX = barLeft;
        } else if (curPotionX > barRight) {
            curPotionX = barRight;
        }
    }


    int canvasHeight = 0;
    int canvasWidth = 0;

    float barHeigth = 10;// 条高度
    float barRound = 5;// 圆角
    float barLeft = 20;//条上左右需留空，否则手柄在两端时，显示不完整
    float barTop = (canvasHeight - barHeigth) / 2;
    float barRight = canvasWidth - barLeft;
    float barBottom = barTop + barHeigth;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.CYAN);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);

        // 绘制滑条和分段值
        if (nodes.size() == 0) {
            // 没有分段
            RectF rectF = new RectF(barLeft, barTop, barRight, barBottom);
            canvas.drawRoundRect(rectF, barRound, barRound, paint);
        } else {
            float left = barLeft;

            final List<Node> cirNodes = new ArrayList<>();
            paint.setTextSize(40);
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                float vp = value2Position(node.value);
                switch (node.type) {
                    case 0:
                        break;
                    case 1:
                        if (left == vp) {

                        } else {
                            canvas.drawRoundRect(new RectF(left, barTop, vp, barBottom), barRound, barRound, paint);
                            left = vp;
                        }
                        break;
                    case 2:
                        cirNodes.add(node);
                        break;
                }

                if (node.showText) {
                    String text = "" + node.value;
                    float textWidth = paint.measureText(text);
                    float textHeight = paint.getFontSpacing();
                    canvas.drawText(text, vp - textWidth / 2, barBottom + textHeight, paint);
                }
            }

            // 绘制最后一个线段
            canvas.drawRoundRect(new RectF(left, barTop, barRight, barBottom), barRound, barRound, paint);

            // 绘制node.value = 2的点
            paint.setColor(Color.MAGENTA);
            for (Node node : cirNodes) {
                canvas.drawCircle(value2Position(node.value), canvasHeight / 2, 10, paint);
            }
        }

        // 绘制手柄
        paint.setColor(Color.BLUE);
        canvas.drawCircle(curPotionX, canvasHeight / 2, barHeigth * 2, paint);

        // 绘制当前值
        paint.setColor(Color.YELLOW);
        paint.setTextSize(40);
        String text = "" + position2Value(curPotionX);
        float textWidth = paint.measureText(text);
        canvas.drawText(text, curPotionX - textWidth / 2, barTop - 30, paint);
    }

    public void addNode(int node, int type, boolean showText) {
        nodes.add(new Node(node, type, showText));
    }

    private class Node {
        int value;
        int type;
        boolean showText;

        Node(int value, int type, boolean showText) {
            this.value = value;
            this.type = type;
            this.showText = showText;
        }
    }
}
