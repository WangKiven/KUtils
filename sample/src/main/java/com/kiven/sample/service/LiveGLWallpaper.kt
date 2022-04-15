package com.kiven.sample.service

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.gl.body.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class LiveGLWallpaper: WallpaperService() {
    private val renderer = MyRenderer()

    override fun onCreateEngine(): Engine {
        return MyEngine()
    }

    private inner class MyRenderer : GLSurfaceView.Renderer {
        lateinit var tle: Triangle
        var dragx = 0f
        var dragy = 0f
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.7f, 0.4f, 0.2f, 1.0f)
            //创建三角形对对象
            tle = Triangle()
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            //设置视窗大小及位置
            GLES20.glViewport(0, 0, width, height)
            //计算GLSurfaceView的宽高比
            val ratio = width.toFloat() / height
            //调用此方法计算产生透视投影矩阵
            Matrix.frustumM(Triangle.mProjMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
            //调用此方法产生摄像机9参数位置矩阵
            Matrix.setLookAtM(Triangle.mVMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        }

        override fun onDrawFrame(gl: GL10) {
            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
            tle.xAngle = dragy*0.2f
            //绘制三角形对
            tle.drawSelf()
        }
    }

    private inner class MyView(context: Context, val engine: Engine?): GLSurfaceView(context) {
        init {
            holder?.removeCallback(this)
            setEGLContextClientVersion(2)
            setRenderer(renderer)
            renderMode = RENDERMODE_CONTINUOUSLY
        }

        override fun getHolder(): SurfaceHolder? {
            return engine?.surfaceHolder ?: super.getHolder()
        }

        fun onDestroy() {
            super.onDetachedFromWindow()
        }
    }

    private inner class MyEngine : Engine() {
        private val mGLSurfaceView: MyView = MyView(this@LiveGLWallpaper, this)

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            mGLSurfaceView.surfaceCreated(holder!!)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            mGLSurfaceView.surfaceChanged(holder!!, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            mGLSurfaceView.surfaceDestroyed(holder!!)
            mGLSurfaceView.onDestroy()
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)
        }

        var startx = 0f
        var starty = 0f
        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
            KLog.i("clickk ${event?.action} x=${event?.x} y=${event?.y}")
            if (event == null) return
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startx = event.x
                    starty = event.y
                }
                else -> {
                    renderer.dragx += event.x - startx
                    renderer.dragy += event.y - starty

                    startx = event.x
                    starty = event.y
                }
            }
        }
    }
}