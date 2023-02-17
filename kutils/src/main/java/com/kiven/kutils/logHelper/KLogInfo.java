package com.kiven.kutils.logHelper;

/**
 * Created by wangk on 2017/11/24.
 */

public class KLogInfo {
    public String codePosition;
    public String log;
    public Long time;

    public KLogInfo(String codePosition, String log) {
        time = System.currentTimeMillis();
        this.codePosition = codePosition;
        this.log = log;
    }
}
