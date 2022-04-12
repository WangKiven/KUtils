package com.kiven.sample.service

import android.graphics.Color
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.kiven.kutils.logHelper.KLog

/**
 * 使用opengl: https://github.com/hanschencoder/GLWallpaperService
 */
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
            KLog.i("clickk ${event?.action} x=${event?.x} y=${event?.y}")
            change()
        }

        private fun change() {
            if (!isVisible) {
                KLog.i("isVisible = $isVisible")
                return
            }

            var hasCanvas = false
            try {
                val canvas = surfaceHolder.lockCanvas() ?: return KLog.i("hasCanvas = $hasCanvas")
                canvas.drawColor(Color.parseColor("#51313B"))

                surfaceHolder.unlockCanvasAndPost(canvas)
                hasCanvas = true
            } catch (e: Throwable) {
                KLog.e(e)
            }

            KLog.i("hasCanvas = $hasCanvas")
        }
    }
}