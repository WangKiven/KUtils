package com.kiven.pushlibrary

import android.app.Service
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.IBinder
import com.kiven.kutils.logHelper.KLog

/**
 * context.startService(Intent(context, PushService::class.java))
 */
class PushService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        KLog.i("PushService-onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        KLog.i("PushService-onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        KLog.i("PushService-onStartCommand")
        return START_STICKY
    }

    override fun onLowMemory() {
        KLog.i("PushService-onLowMemory")
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        KLog.i("PushService-onTrimMemory-$level")
        ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
        super.onTrimMemory(level)
    }

    override fun onRebind(intent: Intent?) {
        KLog.i("PushService-onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        KLog.i("PushService-onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        KLog.i("PushService-onDestroy")
        super.onDestroy()
    }
}