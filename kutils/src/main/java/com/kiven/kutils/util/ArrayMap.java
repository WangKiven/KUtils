package com.kiven.kutils.util;

import com.kiven.kutils.tools.KString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 自定义有序Map（按照存储顺序），实现Parcelable，可在Activity间传递
 * Created by kiven on 16/8/29.
 */
public class ArrayMap<T extends String, V> implements Serializable {

    private List<Entry> list;

    public ArrayMap() {
        list = new ArrayList<>();
    }

    public ArrayMap(int initialCapacity) {
        if (initialCapacity < 1) {
            list = new ArrayList<>();
        } else {
            list = new ArrayList<>(initialCapacity);
        }
    }

    public void put(T key, V value) {
        for (Entry entry : list) {
            if (KString.equals(key, entry.key)) {
                entry.value = value;
                return;
            }
        }

        list.add(new Entry(key, value));
    }

    public Entry get(int position) {
        if (position >= 0 && position < list.size()) {
            return list.get(position);
        }

        return null;
    }

    public Entry get(String key) {
        for (Entry entry : list) {
            if (KString.equals(key, entry.key)) {
                return entry;
            }
        }

        return null;
    }

    public TreeMap<T, Object> toMap() {
        TreeMap<T, Object> map = new TreeMap<>();
        for (Entry entry : list) {
            map.put(entry.key, entry.value);
        }
        return map;
    }

    public class Entry implements Serializable{
        T key;
        V value;

        public Entry(T key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
