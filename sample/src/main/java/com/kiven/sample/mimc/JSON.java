package com.kiven.sample.mimc;

import com.google.gson.Gson;

class JSON {
    public static String toJSONString(Object msg) {
        return new Gson().toJson(msg);
    }

    public static <T> T parseObject(String s, Class<T> msgClass) {
        return new Gson().fromJson(s, msgClass);
    }
}
