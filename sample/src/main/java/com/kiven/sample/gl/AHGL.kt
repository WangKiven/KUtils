package com.kiven.sample.gl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.os.Bundle
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * opengl
 * Created by wangk on 2018/5/12.
 */
class AHGL : KActivityHelper() {
    private val surfaceView: GLSurfaceView by lazy { CameraGLSurfaceView(mActivity) }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(surfaceView)
    }

    class CameraGLSurfaceView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
        init {
            // 设置OpenGl ES的版本为2.0，需在manifests配置gl版本
            setEGLContextClientVersion(2)
            // 设置与当前GLSurfaceView绑定的Renderer
            setRenderer(this)
            // 设置渲染的模式
            renderMode = RENDERMODE_WHEN_DIRTY
        }

        // SurfaceTexture.OnFrameAvailableListener
        override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {

        }

        // GLSurfaceView.Renderer
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
            // Set the background color to black ( rgba ).
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
            // Enable Smooth Shading, default not really needed.
            gl.glShadeModel(GL10.GL_SMOOTH);
            // Depth buffer setup.
            gl.glClearDepthf(1.0f);
            // Enables depth testing.
            gl.glEnable(GL10.GL_DEPTH_TEST);
            // The type of depth testing to do.
            gl.glDepthFunc(GL10.GL_LEQUAL);
            // Really nice perspective calculations.
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                    GL10.GL_NICEST);
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            // Sets the current view port to the new size.
            gl.glViewport(0, 0, width, height)
            // Select the projection matrix
            gl.glMatrixMode(GL10.GL_PROJECTION)
            // Reset the projection matrix
            gl.glLoadIdentity()
            // Calculate the aspect ratio of the window
            GLU.gluPerspective(gl, 45.0f,
                    width.toFloat() / height.toFloat(),
                    0.1f, 100.0f)
            // Select the modelview matrix
            gl.glMatrixMode(GL10.GL_MODELVIEW)
            // Reset the modelview matrix
            gl.glLoadIdentity()
        }

        override fun onDrawFrame(gl: GL10) {
            gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
            // Clears the screen and depth buffer.
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        }
    }
}