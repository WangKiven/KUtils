package com.kiven.kutils.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.kiven.kutils.R;
import com.kiven.kutils.tools.KUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiven on 2016/10/12.
 */

public class UIGridView extends ViewGroup {

    /**
     * 水平单元格间距
     */
    private int dividWith = 0;
    private int dividHeigth = 0;
    private Adapter gridViewAdapter;

    public UIGridView(Context context) {
        super(context);
        init(context);
    }

    public UIGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public UIGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UIGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context) {

    }

    private void init(Context context, AttributeSet attrs) {
        init(context);
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UIGridView);
        // 水平单元格间距
        if (typedArray.hasValue(R.styleable.UIGridView_gv_dividWith)) {
            dividWith = typedArray.getInt(R.styleable.UIGridView_gv_dividWith, 0);
        }
        typedArray.recycle();
    }

    public void setDividWith(int width) {
        dividWith = width;
    }

    public void setDividHeigth(int dividHeigth) {
        this.dividHeigth = dividHeigth;
    }

    public void setAdapter(Adapter gridViewAdapter) {
        this.gridViewAdapter = gridViewAdapter;
        gridViewAdapter.bindGridView(this);

        removeAllViews();
        reloadView();
    }

    public void reloadView() {
        if (gridViewAdapter == null) {
            return;
        }
        gridViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (isInEditMode() && getChildCount() == 0) {
            setAdapter(new Adapter());
        }
    }

    //存储所有子View
    private List<List<View>> mAllChildViews = new ArrayList<>();
    //每一行的高度
    private List<Integer> mLineHeight = new ArrayList<>();

    /**
     * 精确模式（MeasureSpec.EXACTLY）:在这种模式下，尺寸的值是多少，那么这个组件的长或宽就是多少。对应布局参数（match_parent，具体值）
     * 最大模式（MeasureSpec.AT_MOST）:这个也就是父组件，能够给出的最大的空间，当前组件的长或宽最大只能为这么大，当然也可以比这个小。对应布局参数（wrap_content）
     * 未指定模式（MeasureSpec.UNSPECIFIED）:这个就是说，当前组件，可以随便用空间，不受限制。
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //父控件传进来的宽度和高度以及对应的测量模式
        int sizeWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = View.MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = View.MeasureSpec.getMode(heightMeasureSpec);

        //如果当前ViewGroup的宽高为wrap_content的情况
        int width = 0;//自己测量的 宽度
        int height = 0;//自己测量的高度
        //记录每一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        //获取子view的个数
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //测量子View的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //得到LayoutParams
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            //子View占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //子View占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //换行时候
            if (lineWidth + childWidth + dividWith > sizeWidth - getPaddingRight() - getPaddingLeft()) {
                //对比得到最大的宽度
                width = Math.max(width, lineWidth);
                //重置lineWidth
                lineWidth = childWidth;
                //记录行高
                height += lineHeight + dividHeigth;
                lineHeight = childHeight;
            } else {//不换行情况
                //叠加行宽
                lineWidth += (childWidth + dividWith);
                //得到最大行高
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //处理最后一个子View的情况
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        //wrap_content
        ViewGroup.MarginLayoutParams pp = (ViewGroup.MarginLayoutParams) getLayoutParams();
        setMeasuredDimension(modeWidth == View.MeasureSpec.EXACTLY ? sizeWidth : width,
                modeHeight == View.MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom());
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        mAllChildViews.clear();
        mLineHeight.clear();
        //获取当前ViewGroup的宽度
        int width = getWidth();

        int lineWidth = getPaddingLeft();
        int lineHeight = 0;
        //记录当前行的view
        List<View> lineViews = new ArrayList<View>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
//            LayoutParams params = child.getLayoutParams();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //如果需要换行
            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin + dividWith > width - getPaddingLeft() - getPaddingRight()) {
                //记录LineHeight
                mLineHeight.add(lineHeight);
                //记录当前行的Views
                mAllChildViews.add(lineViews);
                //重置行的宽高
                lineWidth = getPaddingLeft();
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                //重置view的集合
                lineViews = new ArrayList();
            }
            if (lineViews.size() > 0) {
                lineWidth += childWidth + lp.leftMargin + lp.rightMargin + dividWith;
            } else {
                lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            }
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }
        //处理最后一行
        mLineHeight.add(lineHeight);
        mAllChildViews.add(lineViews);


        //设置子View的位置
        int left = getPaddingLeft();
        int top = getPaddingTop();
        //获取行数
        int lineCount = mAllChildViews.size();

        for (int i = 0; i < lineCount; i++) {
            //当前行的views和高度
            lineViews = mAllChildViews.get(i);
            lineHeight = mLineHeight.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                //判断是否显示
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                int cLeft = left + lp.leftMargin + (j == 0 ? 0 : dividWith);
                int cTop = top + lp.topMargin;
                int cRight = cLeft + child.getMeasuredWidth();
                int cBottom = cTop + child.getMeasuredHeight();
                //进行子View进行布局
                child.layout(cLeft, cTop, cRight, cBottom);
                left = cRight + lp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight + dividHeigth;
        }
    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        if (p == null) {
            return generateDefaultLayoutParams();
        }

        if (p instanceof MarginLayoutParams) {
            return p;
        } else {
            return new MarginLayoutParams(p);
        }
    }

    public static class Adapter {

        //childView间的间距
//        protected int childMargin;
        protected int childMarginLeft;
        protected int childMarginTop;
        protected int childMarginRight;
        protected int childMarginBottom;
        /**
         * 是否是单列，默认不是。如果不是单列，UIGridView将根据itemView宽度自动计算进行流布局显示
         */
        protected boolean isSingleCol = false;

        private UIGridView mGridView;

        public void setChildMargin(int childMargin) {
            childMarginLeft = childMargin;
            childMarginTop = childMargin;
            childMarginRight = childMargin;
            childMarginBottom = childMargin;
        }

        private void bindGridView(UIGridView gridView) {
            mGridView = gridView;
        }

        public int getGridViewItemCount() {
            return 3;
        }

        public View getItemView(Context context, View itemView, ViewGroup parentView, int position) {
            if (itemView == null) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.k_default_image);

                itemView = imageView;
            }

            return itemView;
        }

        final Handler ch = new Handler(msg -> {
            synchronized (this) {
                addAllItemView();
            }
            return true;
        });
        public void notifyDataSetChanged() {
            if (mGridView != null) {
                new Thread() {
                    @Override
                    public void run() {
                        ch.sendEmptyMessage(0);
                    }
                }.start();
            }
        }

        private void addAllItemView() {
            /*List<View> itemVies = new ArrayList<>();
            for (int i = 0; i < mGridView.getChildCount(); i++) {
                itemVies.add(mGridView.getChildAt(i));
            }

            mGridView.removeAllViews();

            Context context = mGridView.getContext();
            if (context == null) {
                return;
            }
            for (int i = 0; i < getGridViewItemCount(); i++) {

                View itemView = getItemView(mGridView.getContext(), i < itemVies.size() ? itemVies.remove(i) : null, mGridView, i);

                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(0, 0);
                params.width = isSingleCol ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.setMargins(childMarginLeft, childMarginTop, childMarginRight, childMarginBottom);

                if (mGridView.indexOfChild(itemView) < 0) {
                    mGridView.addView(itemView, params);
                } else {

                }
            }*/

            final int oldChildCount = mGridView.getChildCount();
            final int newChildCount = getGridViewItemCount();
            for (int i = 0; i < newChildCount; i++) {
                final boolean useOld = i + 1 <= oldChildCount;
                View itemView = getItemView(mGridView.getContext(), useOld ? mGridView.getChildAt(i) : null, mGridView, i);

                if (!useOld) {
                    ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(0, 0);
                    params.width = isSingleCol ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.setMargins(childMarginLeft, childMarginTop, childMarginRight, childMarginBottom);

                    mGridView.addView(itemView, params);
                }
            }

            if (newChildCount < oldChildCount) {
                for (int i = 0; i < oldChildCount - newChildCount; i++) {
                    mGridView.removeViewAt(oldChildCount - i - 1);
                }
            }
        }
    }

    public static class BaseAdapter extends Adapter {
    }

    @Deprecated
    public static class UIGridViewAdapter extends Adapter {

        public UIGridViewAdapter(Context context) {
            setChildMargin(KUtil.dip2px(5));
        }
    }
}
