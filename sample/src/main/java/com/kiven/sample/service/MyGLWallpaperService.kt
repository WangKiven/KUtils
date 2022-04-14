package com.kiven.sample.service

import com.kiven.sample.service.gl.GLWallpaperService
import com.kiven.sample.service.gl.GLWallpaperService.GLEngine
import com.kiven.sample.service.gl.EglConfigChooser
import com.kiven.sample.service.MyGLWallpaperService.MyRenderer
import android.opengl.GLSurfaceView
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
import android.opengl.Matrix
import com.kiven.sample.gl.body.Triangle
import javax.microedition.khronos.egl.EGLConfig

/**
 */
class MyGLWallpaperService : GLWallpaperService() {
    override fun createGLEngine(): GLEngine {
        return object : GLEngine() {
            override fun setupGLSurfaceView(isPreview: Boolean) {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(EglConfigChooser(8, 8, 8, 0, 0, 0, 0))
                setRenderer(MyRenderer())
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        }
    }

    class MyRenderer : GLSurfaceView.Renderer {
        lateinit var tle: Triangle
        var dragy = 0f
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
//            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.7f, 0.4f, 0.2f, 1.0f)
            //创建三角形对对象
            tle = Triangle()
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
//            GLES20.glViewport(0, 0, width, height)

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
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
            tle.xAngle = dragy*0.2f
            //绘制三角形对
            tle.drawSelf()
        }
    }
}