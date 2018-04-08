package com.kiven.sample.service

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder

class LiveWallpaper:WallpaperService() {
    override fun onCreateEngine(): Engine {
        return MyEngine()
    }

    private inner class MyEngine : Engine() {
        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

        }

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)

            val canvas = surfaceHolder.lockCanvas() ?: return

        }
    }
}