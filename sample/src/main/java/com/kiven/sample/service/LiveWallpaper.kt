package com.kiven.sample.service

import android.graphics.Color
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
            setTouchEventsEnabled(true)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            change()
        }

        override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
            change()
        }

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
            change()
        }

        private fun change() {
            if (!isVisible) {
                return
            }

            val canvas = surfaceHolder.lockCanvas() ?: return
            canvas.drawColor(Color.CYAN)
//            canvas.restore()
        }
    }
}