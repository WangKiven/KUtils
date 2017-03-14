package com.kiven.kutils.util;

import com.kiven.kutils.callBack.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kiven on 2017/3/14.
 */

public class CollectionUtil {
    public static <T> Map<String, List<T>> groupFirshChar(List<T> list, Function<T, String> function) {
        Map<String, List<T>> map = new TreeMap<>();
        for (T item : list) {
            String firstChar = function.callBack(item);

            List<T> values;
            if (map.containsKey(firstChar)) {
                values = map.get(firstChar);
            } else {
                values = new ArrayList<>();
                map.put(firstChar, values);
            }
            values.add(item);
        }
        return map;
    }
}
