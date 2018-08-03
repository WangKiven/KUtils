package com.kiven.kutils.util;

import android.os.Handler;
import android.os.Message;

import com.kiven.kutils.callBack.Consumer;

/**
 * 倒计时器
 */
public class CountHandler extends Handler {
    private int downTime;
    private Consumer<Integer> consumer;

    public CountHandler(int downTime, Consumer<Integer> consumer) {
        super();
        this.downTime = downTime;
        this.consumer = consumer;
    }

    @Override
    public void handleMessage(Message msg) {
        if (consumer != null)
            consumer.callBack(downTime);
        downTime--;
        if (downTime >= 0)
            sendEmptyMessageDelayed(0, 1000);
    }

    public void start() {
        sendEmptyMessage(0);
    }
}
