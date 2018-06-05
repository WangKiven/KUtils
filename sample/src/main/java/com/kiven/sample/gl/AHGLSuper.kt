package com.kiven.sample.gl

import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.os.Bundle
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class AHGLSuper : KActivityHelper(), GLSurfaceView.Renderer {
    override fun onDrawFrame(gl: GL10) {

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // Sets the current view port to the new size. 定义显示视窗的大小和位置
        gl.glViewport(0, 0, width, height)
        // Select the projection matrix, 设置当前 Matrix 模式为 Projection 投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION)
        // Reset the projection matrix
        gl.glLoadIdentity()

        // Calculate the aspect ratio of the window
        GLU.gluPerspective(gl, 45.0f,
                width.toFloat() / height.toFloat(),
                0.1f, 100.0f)

        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        // 重置当前的模型观察矩阵。
        gl.glLoadIdentity()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        // 设置清除屏幕时所用的颜色，参数对应(红,绿,蓝,Alpha值)。色彩值的范围从0.0f到1.0f。0.0f代表最黑的情况，1.0f就是最亮的情况。
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f)
        // 启用smooth shading（阴影平滑）。阴影平滑通过多边形精细的混合色彩，并对外部光进行平滑
        gl.glShadeModel(GL10.GL_SMOOTH)

        // 下面三行是关于depth buffer(深度缓存)的。将深度缓存设想为屏幕后面的层。深度缓存不断的对物体进入屏幕内部有多深进行跟踪。
        // Depth buffer setup.
        gl.glClearDepthf(1.0f)
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST)
        // The type of depth testing to do.
        gl.glDepthFunc(GL10.GL_LEQUAL)

        // 这里告诉OpenGL我们希望进行最好的透视修正。这会十分轻微的影响性能。但使得透视图看起来好一点。
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST)
    }

    protected lateinit var surfaceView: GLSurfaceView

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        surfaceView = GLSurfaceView(mActivity)

        // 设置OpenGl ES的版本为2.0，需在manifests配置gl版本。配置后，居然不能绘图
        // surfaceView.setEGLContextClientVersion(2)
        // 设置与当前GLSurfaceView绑定的Renderer
        surfaceView.setRenderer(this)

        // 设置渲染的模式，
        // RENDERMODE_WHEN_DIRTY：只有在调用 requestRender() 在更新屏幕，
        // RENDERMODE_CONTINUOUSLY：连续不断的更新屏幕
        surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY


        setContentView(surfaceView)
    }

    override fun onResume() {
        super.onResume()
        surfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        surfaceView.onPause()
    }
}