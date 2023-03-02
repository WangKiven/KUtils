package com.kiven.kutils.logHelper;

/**
 * Created by wangk on 2017/11/24.
 */

public class KLogInfo {
    public String log;
    public String codePosition;
    public String codePositionStack;
    public Long time;

    public KLogInfo(String log, String codePosition, String codePositionStack) {
        time = System.currentTimeMillis();
        this.log = log;
        this.codePosition = codePosition;
        this.codePositionStack = codePositionStack;
    }
}
