package com.kiven.sample.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 *
 * Created by kiven on 2016/11/17.
 */

public class ServiceWidget extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
