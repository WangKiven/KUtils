package com.kiven.kutils.callBack;

/**
 * 消费者：在单个参数上的操作
 * Created by kiven on 2017/1/19.
 */

public interface Consumer<T> {
    void callBack(T param);
}
