package com.kiven.kutils.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiven.kutils.R;
import com.kiven.kutils.tools.KImage;

/**
 *
 * Created by kiven on 2016/10/11.
 */

public class KNormalItemView extends LinearLayout{
    // TODO 默认布局, 可自定义
    public static @LayoutRes int defaultLayout = R.layout.k_item_normal;

    public TextView textViewName;
    public TextView textViewInfo;
    
    public KNormalItemView(Context context) {
        super(context);
        init(null);
    }

    public KNormalItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public KNormalItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KNormalItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    protected void init(AttributeSet attrs) {
        TypedArray typedArray = null;

        if (attrs != null) {
            typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.KNormalItemView);
            if (typedArray.hasValue(R.styleable.KNormalItemView_item_layout)) {
                int rId = typedArray.getResourceId(R.styleable.KNormalItemView_item_layout, getLayoutId());
                LayoutInflater.from(getContext()).inflate(rId, this);
            } else {
                initContext();
            }
        } else {
            initContext();
        }

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewInfo = (TextView) findViewById(R.id.textViewInfo);

        if (attrs != null) {
            if (typedArray == null) {
                typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.KNormalItemView);
            }

            View root = findViewById(R.id.root);
            if (root != null) {
                /*if (typedArray.hasValue(R.styleable.KNormalItemView_item_background)) {
                    TImage.setBackgroundDrawable(root, typedArray.getDrawable(R.styleable.KNormalItemView_item_background));
                } else {

                }*/
                if (!typedArray.getBoolean(R.styleable.KNormalItemView_item_show_default_background, true)) {
                    KImage.setBackgroundDrawable(root, typedArray.getDrawable(R.styleable.KNormalItemView_item_background));
                }
            }

            if (textViewName != null) {
                Drawable drawableLeft = typedArray.getDrawable(R.styleable.KNormalItemView_item_drawableLeft);
                if (drawableLeft != null) {
                    textViewName.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
                }

                if (typedArray.getBoolean(R.styleable.KNormalItemView_item_title_bold, false)) {
                    textViewName.setTypeface(textViewName.getTypeface(), Typeface.BOLD);
                }

                textViewName.setText(typedArray.getText(R.styleable.KNormalItemView_item_title));
                if (typedArray.hasValue(R.styleable.KNormalItemView_item_title_color)) {
                    textViewName.setTextColor(typedArray.getColor(R.styleable.KNormalItemView_item_title_color, Color.BLACK));
                }
            }

            if (textViewInfo != null) {
                textViewInfo.setText(typedArray.getText(R.styleable.KNormalItemView_item_detail));
                if (typedArray.hasValue(R.styleable.KNormalItemView_item_detail_color)) {
                    textViewInfo.setTextColor(typedArray.getColor(R.styleable.KNormalItemView_item_detail_color, Color.BLACK));
                }

                textViewInfo.setHint(typedArray.getText(R.styleable.KNormalItemView_item_detail_hint));
                if (typedArray.hasValue(R.styleable.KNormalItemView_item_detail_show)) {
                    boolean show = typedArray.getBoolean(R.styleable.KNormalItemView_item_detail_show, true);
                    showArrow(show);
                }

                if (typedArray.hasValue(R.styleable.KNormalItemView_item_detail_arrow)) {
                    textViewInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, typedArray.getDrawable(R.styleable.KNormalItemView_item_detail_arrow), null);
                }
            }

            typedArray.recycle();
        }
    }

    protected void initContext() {
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
    }


    private int getLayoutId() {
        return defaultLayout;
    }

    public void setTextName(String name) {
        textViewName.setText(name == null? "": name);
    }

    public void setTextName(@StringRes int name) {
        textViewName.setText(name);
    }

    public void setTextInfo(String info) {
        textViewInfo.setText(info == null? "": info);
    }

    public void setTextInfo(@StringRes int info) {
        textViewInfo.setText(info);
    }

    /**
     * 设置是否显示箭头
     * @param show
     */
    public void showArrow(boolean show) {
        textViewInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, show? R.drawable.k_right_arrow: 0, 0);
    }
}
