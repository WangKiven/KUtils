package com.kiven.sample.imui;

import com.kiven.kutils.callBack.Function;

public abstract class ImTool {
    public static Function<String, ImCall> call = new Function<String, ImCall>() {
        @Override
        public ImCall callBack(String param) {
            return null;
        }
    };
}
