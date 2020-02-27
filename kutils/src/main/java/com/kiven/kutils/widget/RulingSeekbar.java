package com.kiven.kutils.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangk on 2018/1/22.
 */

public class RulingSeekbar extends View {
    private float dpScale;
    private float spScale;

    public RulingSeekbar(Context context) {
        this(context, null);
    }

    public RulingSeekbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private int min;
    private int max;

    private OnChangeListener onChange;
    private Formater formater;

    private final List<Node> nodes = new ArrayList<>();

    Paint paint = new Paint();

    private int sp(float f) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (f * fontScale + 0.5f);
    }

    private void init(Context context, AttributeSet attrs) {
        dpScale = context.getResources().getDisplayMetrics().density;
        spScale = context.getResources().getDisplayMetrics().scaledDensity;

        paint.setAntiAlias(true);//抗锯齿
        setLayerType(LAYER_TYPE_SOFTWARE, paint);//关闭硬件加速，否则阴影绘制失败


        barHeigth = dpScale * 5;// 条高度
        cpu();

        barColor = Color.parseColor("#49b5ff");
        barNodeColor = barColor;
        barNodeTextColor = Color.BLACK;
        binColor = Color.WHITE;
        curValueColor = barColor;

        curValueTextSize = sp(13);
        barNodeTextSize = sp(12);

        min = 0;
        max = 100;
        /*addNode(60, 0, true);
        addNode(70, 2, true);
        addNode(77, 1, true);
        addNode(85, 1, false);
        addNode(90, 2, true); */
    }

    public void setScale(int min, int max) {
        if (max < min) {
            return;
        }
        this.min = min;
        this.max = max;

        if (onChange != null) {
            progress = position2Value(binX);
            onChange.onProgressChanged(this, progress, false);
        }
        invalidate();
    }

    /**
     * 当前进度
     */
    private int progress = 0;

    public int getProgress() {
//        return position2Value(binX);
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        binX = value2Position(progress);
        invalidate();
    }

    public void setOnChangeListener(OnChangeListener change) {
        this.onChange = change;
    }

    public void setFormater(Formater formater) {
        this.formater = formater;
    }

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

    float firstPostionX;// 触摸开始的位置
    float startPosion;// 触摸开始时，手柄的位置

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(binX - event.getX()) > barHeigth * 3) {
                    return false;
                }
                // 阻止父控件获得触摸操作，拦截触摸事件，防止与ViewPager等控件发生手势冲突
                getParent().requestDisallowInterceptTouchEvent(true);

                firstPostionX = event.getX();
                startPosion = binX;

                if (onChange != null)
                    onChange.onStartTrackingTouch(this);
                break;
            case MotionEvent.ACTION_MOVE:
                binX = startPosion + (event.getX() - firstPostionX);

                // 当前值的位置
                if (binX < barLeft) {
                    binX = barLeft;
                } else if (binX > barRight) {
                    binX = barRight;
                }
                invalidate();
                if (onChange != null) {
                    progress = position2Value(binX);
                    onChange.onProgressChanged(this, progress, true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (onChange != null) {
                    progress = position2Value(binX);
                    onChange.onStopTrackingTouch(this);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasHeight = h;
        canvasWidth = w;

        cpu();
    }

    private void cpu() {

        binRadius = barHeigth * 2;

        barLeft = binRadius + 2 * dpScale;//条上左右需留空，否则手柄在两端时，显示不完整
        barRound = barHeigth * 0.45f;
        barTop = (canvasHeight - barHeigth) / 2;
        barRight = canvasWidth - barLeft;
        barBottom = barTop + barHeigth;

        barNodeRadius = barHeigth * 0.9f;

        // 当前值的位置
        binX = value2Position(progress);
        if (binX < barLeft) {
            binX = barLeft;
        } else if (binX > barRight) {
            binX = barRight;
        }
    }

    private int canvasHeight = 0;
    private int canvasWidth = 0;
    // 横条
    private float barHeigth = 10;// 条高度
    private float barRound = 5;// 圆角
    private float barLeft = 20;//条上左右需留空，否则手柄在两端时，显示不完整
    private float barTop = (canvasHeight - barHeigth) / 2;
    private float barRight = canvasWidth - barLeft;
    private float barBottom = barTop + barHeigth;
    private int barColor = Color.BLUE;

    // 横条上的节点
    private float barNodeRadius = barHeigth * 0.9f;
    private int barNodeColor = Color.RED;
    private int barNodeTextColor = Color.BLACK;
    private int barNodeTextSize = 16;

    // 手柄
    private float binX = 0;// 手柄位置
    private float binRadius = barHeigth * 2;
    private int binColor = Color.GRAY;

    // 当前值
    private int curValueColor = Color.BLUE;
    private int curValueTextSize = 15;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawColor(Color.WHITE);// 背景，不需要绘制背景，父类super.onDraw(canvas)已经实现了背景绘制

        paint.setColor(barColor);

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
                    paint.setTextSize(barNodeTextSize);
                    float textWidth = paint.measureText(text);
                    float textHeight = paint.getFontSpacing();
                    paint.setColor(barNodeTextColor);
                    float px = vp - textWidth / 2;
                    if (px < 0) {
                        px = 0;
                    } else if (px + textWidth > canvasWidth) {
                        px = canvasWidth - textWidth;
                    }
                    canvas.drawText(text, px, barBottom + textHeight, paint);
                    paint.setColor(barColor);
                }
            }

            // 绘制最后一个线段
            canvas.drawRoundRect(new RectF(left, barTop, barRight, barBottom), barRound, barRound, paint);

            // 绘制node.value = 2的点
            paint.setColor(barNodeColor);
            for (Node node : cirNodes) {
                canvas.drawCircle(value2Position(node.value), canvasHeight / 2, barNodeRadius, paint);
            }
        }

        // 绘制手柄
        paint.setColor(binColor);
        paint.setShadowLayer(2 * dpScale, 1 * dpScale, 1 * dpScale, Color.GRAY);
        canvas.drawCircle(binX, canvasHeight / 2, binRadius, paint);
        paint.clearShadowLayer();

        // 绘制当前值
        paint.setColor(curValueColor);
        paint.setTextSize(curValueTextSize);
        String text = getShow(progress);
        float textWidth = paint.measureText(text);
        float px = binX - textWidth / 2;
        if (px < 0) {
            px = 0;
        } else if (px + textWidth > canvasWidth) {
            px = canvasWidth - textWidth;
        }
        canvas.drawText(text, px, canvasHeight / 2 - binRadius - 6 * dpScale, paint);
    }

    protected String getShow(int value) {
        if (formater == null)
            return "" + value;
        else {
            return formater.formatCurValue(value);
        }
    }

    public void addNode(int node, int type, boolean showText) {
        nodes.add(new Node(node, type, showText));
        invalidate();
    }

    public void addNodes(List<Node> nodes) {
        this.nodes.clear();
        if (nodes == null || nodes.size() == 0) {

        } else {
            this.nodes.addAll(nodes);
        }
        invalidate();
    }

    public void clearNodes() {
        this.nodes.clear();
        invalidate();
    }

    public static class Node {
        int value;
        int type;
        boolean showText;

        public Node(int value, int type, boolean showText) {
            this.value = value;
            this.type = type;
            this.showText = showText;
        }
    }

    public interface OnChangeListener {
        void onProgressChanged(@NonNull RulingSeekbar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(@NonNull RulingSeekbar seekBar);

        void onStopTrackingTouch(@NonNull RulingSeekbar seekBar);
    }

    public interface Formater {
        String formatCurValue(int value);
    }
}
