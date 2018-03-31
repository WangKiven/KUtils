package com.kiven.sample.service

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class LiveWallpaper:WallpaperService() {
    override fun onCreateEngine(): Engine {
        return Engine()
    }

    private inner class MyEngine : Engine() {
        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

        }
    }
}