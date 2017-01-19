package com.kiven.kutils.callBack;

/**
 * 有一个参数并且返回一个结果
 * Created by kiven on 2017/1/19.
 */

public interface Function<T, V> {
    V callBack(T param);
}
