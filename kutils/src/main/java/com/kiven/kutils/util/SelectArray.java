package com.kiven.kutils.util;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayList优化，添加选中功能
 * Created by kiven on 2017/8/21.
 */

public class SelectArray<T> extends ArrayList<SelectObj<T>> {
    public SelectArray() {
    }

    public SelectArray(T[] a) {
        if (a != null && a.length > 0) {
            for (T t : a) {
                add(new SelectObj<T>(t));
            }
        }
    }

    public SelectArray(List<T> a) {
        if (a != null && a.size() > 0) {
            for (T t : a) {
                add(new SelectObj<T>(t));
            }
        }
    }

    // 是否是单选
    private boolean isSingleSel;

    public boolean isSingleSel() {
        return isSingleSel;
    }

    public void setSingleSel(boolean singleSel) {
        isSingleSel = singleSel;
    }


    // 选中某项
    public void select(int position) {
        if (position >= size() || position < 0) {
            return;
        }

        select(get(position));
    }

    // 选中某项
    public void select(SelectObj obj) {
        if (obj == null) {
            return;
        }

        // 清除原有单选
        if (isSingleSel) {
            clearSingleSel();
        }
        // 选中
        obj.isChecked = true;
    }

    // 获取选中的计数
    public int getSelCount() {
        int count = 0;
        for (SelectObj obj : this) {
            if (obj.isChecked) {
                count++;
            }
        }
        return count;
    }

    // 获取所有选项
    public @NonNull
    ArrayList<T> getAllSel() {
        ArrayList<T> array = new ArrayList<T>();
        for (SelectObj<T> obj : this) {
            if (obj.isChecked) {
                array.add(obj.t);
            }
        }

        return array;
    }
    // 获取所有选项
    public @NonNull
    SelectArray<T> getAllSel2() {
        SelectArray<T> array = new SelectArray<T>();
        for (SelectObj<T> obj : this) {
            if (obj.isChecked) {
                array.add(obj);
            }
        }

        return array;
    }

    // 获取单选项或第一个被选中项
    public SelectObj<T> getFirstSel() {
        for (SelectObj<T> obj : this) {
            if (obj.isChecked) {
                return obj;
            }
        }

        return null;
    }

    // 选择所有
    public void selectAll() {
        for (SelectObj obj : this) {
            obj.isChecked = true;
        }
    }

    // 清除选择
    public void clearSel() {
        if (isSingleSel) {
            clearSingleSel();
        } else {
            for (SelectObj obj : this) {
                obj.isChecked = false;
            }
        }
    }

    /**
     * 单选情况下，用于清除选中
     */
    private void clearSingleSel() {
        SelectObj obj = getFirstSel();
        if (obj != null) {
            obj.isChecked = false;
        }
    }
}
