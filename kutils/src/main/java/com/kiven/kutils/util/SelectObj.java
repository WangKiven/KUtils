package com.kiven.kutils.util;

import java.io.Serializable;

/**
 * 对原有对象扩展
 * Created by kiven on 2017/8/21.
 */

public class SelectObj<T> implements Serializable {
    public boolean isChecked;
    public int position;
    public T t;

    public SelectObj(T t) {
        this.t = t;
    }

    public SelectObj(boolean isChecked, T t) {
        this.isChecked = isChecked;
        this.t = t;
    }

    public SelectObj(int position, T t) {
        this.position = position;
        this.t = t;
    }

    public SelectObj(boolean isChecked, int position, T t) {
        this.isChecked = isChecked;
        this.position = position;
        this.t = t;
    }
}
