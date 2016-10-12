package com.kiven.kutils.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kiven.kutils.R;
import com.kiven.kutils.tools.KUtil;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by kiven on 2016/10/12.
 */

public class UIGridView extends ViewGroup {

    private UIGridViewAdapter gridViewAdapter;

    public UIGridView(Context context) {
        super(context);
        init(context);
    }

    public UIGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public UIGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UIGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            setAdapter(new UIGridViewAdapter(context));
        }
    }

    public void setAdapter(UIGridViewAdapter gridViewAdapter) {
        this.gridViewAdapter = gridViewAdapter;
        gridViewAdapter.bindGridView(this);

        removeAllViews();
        reloadView();
    }

    public void reloadView() {
        if (gridViewAdapter == null) {
            return;
        }
        gridViewAdapter.addAllItemView();
    }

    //存储所有子View
    private List<List<View>> mAllChildViews = new ArrayList<>();
    //每一行的高度
    private List<Integer> mLineHeight = new ArrayList<>();

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
        for(int i = 0;i < childCount; i ++){
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
            if(lineWidth + childWidth > sizeWidth - getPaddingRight() - getPaddingLeft()){
                //对比得到最大的宽度
                width = Math.max(width, lineWidth);
                //重置lineWidth
                lineWidth = childWidth;
                //记录行高
                height += lineHeight;
                lineHeight = childHeight;
            }else{//不换行情况
                //叠加行宽
                lineWidth += childWidth;
                //得到最大行高
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //处理最后一个子View的情况
            if(i == childCount -1){
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
        for(int i = 0;i < childCount; i ++){
            View child = getChildAt(i);
//            LayoutParams params = child.getLayoutParams();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //如果需要换行
            if(childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()){
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
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
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

        for(int i = 0; i < lineCount; i ++){
            //当前行的views和高度
            lineViews = mAllChildViews.get(i);
            lineHeight = mLineHeight.get(i);
            for(int j = 0; j < lineViews.size(); j ++){
                View child = lineViews.get(j);
                //判断是否显示
                if(child.getVisibility() == View.GONE){
                    continue;
                }
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                int cLeft = left + lp.leftMargin;
                int cTop = top + lp.topMargin;
                int cRight = cLeft + child.getMeasuredWidth();
                int cBottom = cTop + child.getMeasuredHeight();
                //进行子View进行布局
                child.layout(cLeft, cTop, cRight, cBottom);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }
    }
    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    public static class UIGridViewAdapter implements View.OnClickListener {

        //childView间的间距
        protected int childMargin;
        /**
         * 是否是单列，默认是。如果不是单列，UIGridView将根据itemView宽度自动计算进行流布局显示
         */
        protected boolean isSingleCol = false;

        private UIGridView mGridView;

        public UIGridViewAdapter(Context context) {
            childMargin = KUtil.dip2px(context, 5);
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

        @Override
        public void onClick(View v) {

        }

        public void notifyDataSetChanged() {
            if (mGridView != null) {
                x.task().run(new Runnable() {
                    @Override
                    public void run() {
                        x.task().post(new Runnable() {
                            @Override
                            public void run() {
                                addAllItemView();
                            }
                        });
                    }
                });
            }
        }

        private void addAllItemView() {
            List<View> itemVies = new ArrayList<>();
            for (int i = 0; i < mGridView.getChildCount(); i++) {
                itemVies.add(mGridView.getChildAt(i));
            }

            mGridView.removeAllViews();

            for (int i = 0; i < getGridViewItemCount(); i++) {

                View itemView = getItemView(mGridView.getContext(), i < itemVies.size()? itemVies.remove(i): null, mGridView, i);

                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(0, 0);
                params.width = isSingleCol? ViewGroup.LayoutParams.MATCH_PARENT: ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(isSingleCol? ViewGroup.LayoutParams.MATCH_PARENT: ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(childMargin, childMargin, childMargin, childMargin);

                if (mGridView.indexOfChild(itemView) < 0) {
                    mGridView.addView(itemView, params);
                } else {

                }
            }
        }
    }
}
