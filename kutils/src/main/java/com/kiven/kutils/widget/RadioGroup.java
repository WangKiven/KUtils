package com.kiven.kutils.widget;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 单选组
 * Created by kiven on 2017/4/21.
 */

public class RadioGroup implements View.OnClickListener {
    final View rootView;
    final List<View> radioViews;
    RadioGroupListener radioGroupListener;
    boolean isCanChange = true;

    // 是否能够取消选中。用户点击已选中tab时触发。因为可以取消，所以结果可能一个都没选中
    boolean canCancel = false;

    public RadioGroup(@NonNull View rootView) {
        this.rootView = rootView;
        radioViews = new ArrayList<>();
    }

    public RadioGroup(@NonNull View rootView, RadioGroupListener listener, @NonNull int... resIds) {
        this(rootView);
        radioGroupListener = listener;
        setRadioButtons(resIds);
    }

    public void setRadioButtons(@NonNull int... resIds) {
        for (int rId : resIds) {
            View view = rootView.findViewById(rId);
            if (view != null) {
                radioViews.add(view);
                view.setOnClickListener(this);
            }
        }
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public List<View> getRadioViews() {
        return radioViews;
    }

    @Override
    public void onClick(View v) {
        onClick(v, false);
    }

    /**
     * @param isForce 是否不考虑isCanChange，强制触发点击
     */
    public void onClick(View v, boolean isForce) {
        if (!isCanChange && !isForce)
            return;
        // 选中项是否有改变
        boolean isChange = true;
        for (View view : radioViews) {
            if (view == v) {
                if (view.isSelected()) {
                    if (canCancel) {
                        view.setSelected(false);
                    }else {
                        // 选中项无改变
                        isChange = false;
                    }
                } else {
                    v.setSelected(true);
                }
            } else {
                if (view.isSelected()) {
                    view.setSelected(false);
                }
            }
        }

        if (isChange && radioGroupListener != null) {
            radioGroupListener.onChangeSelected(v);
        }
    }

    /**
     * 设置是否可以切换tab
     *
     * @param isCanChange
     */
    public void setIsCanChange(boolean isCanChange) {
        this.isCanChange = isCanChange;
    }

    /**
     * 设置选中按钮
     */
    public void setChecked(@NonNull int resId) {
        onClick(rootView.findViewById(resId), true);
    }

    /**
     * 清除选中按钮
     */
    public void clearChecked() {
        for (View view : radioViews) {
            if (view.isSelected()) {
                view.setSelected(false);
            }
        }
    }

    /**
     * 获取选中view的id
     */
    public int getCheckedId() {
        for (View view : radioViews) {
            if (view.isSelected()) {
                return view.getId();
            }
        }

        return 0;
    }

    /**
     * 获取选中view的下标，位置
     */
    public int getCheckedPosition() {
        for (int i = 0; i < radioViews.size(); i++) {
            View view = radioViews.get(i);
            if (view.isSelected()) {
                return i + 1;
            }
        }
        return 0;
    }

    public void onSaveInstanceState(Bundle outSate, String key) {
        if (outSate == null) return;

        int selId = getCheckedId();
        if (selId > 0) {
            outSate.putInt(key + "_radioGroup", selId);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState, String key) {
        if (savedInstanceState == null) return;

        int selId = savedInstanceState.getInt(key + "_radioGroup", 0);
        if (selId > 0)
            setChecked(selId);
    }

    public interface RadioGroupListener {
        void onChangeSelected(View view);
    }
}
