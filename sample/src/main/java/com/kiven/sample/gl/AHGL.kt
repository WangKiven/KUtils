package com.kiven.sample.gl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.os.Bundle
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES10.glDisableClientState
import android.opengl.GLES10.glEnableClientState
import java.nio.FloatBuffer
import android.opengl.GLES10.glLoadIdentity
import android.opengl.GLES10.glLightf
import android.opengl.GLES10.glMaterialf






/**
 * opengl
 * Created by wangk on 2018/5/12.
 */
class AHGL : KActivityHelper() {
    private lateinit var surfaceView: GLSurfaceView

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        surfaceView = CameraGLSurfaceView(mActivity)
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

    class CameraGLSurfaceView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
        init {
            // 设置OpenGl ES的版本为2.0，需在manifests配置gl版本。配置后，居然不能绘图
//            setEGLContextClientVersion(2)
            // 设置与当前GLSurfaceView绑定的Renderer
            setRenderer(this)

            // 设置渲染的模式，
            // RENDERMODE_WHEN_DIRTY：只有在调用 requestRender() 在更新屏幕，
            // RENDERMODE_CONTINUOUSLY：连续不断的更新屏幕
            renderMode = RENDERMODE_CONTINUOUSLY
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
            // Reset the modelview matrix
            gl.glLoadIdentity()
        }


        // 顶点 3个 可组成一个面
        var vertexArray = floatArrayOf(
                -0.3f, -0.3f, 0.0f,
                0.3f, -0.3f, 0.0f,
                0.0f, 0.3f, 0.0f)
        // 面的顶点颜色
        var colors = floatArrayOf(
                1f, 0f, 0f, 1f,
                0f, 0f, 1f, 1f,
                0f, 1f, 0f, 1f
        )
        var indices = shortArrayOf(0, 1, 2)// 面构造，仅一个面，由第一二三个顶点构成
        private var angle = 0f

        override fun onDrawFrame(gl: GL10) {
            gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
            // Clears the screen and depth buffer.
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

            gl.glPointSize(20f)
            gl.glLineWidth(4f)
            gl.glLoadIdentity()// 将当前矩阵回复最初的无变换的矩阵
            gl.glTranslatef(0f, 0f, -4f)
            gl.glRotatef(angle, 0f, 1f, 0f)// 旋转
//            gl.glTranslatef(0.3f, 0.3f, -15f)// 移动

            // 前面后面配置-似乎仅对面的绘制有用
            gl.glFrontFace(GL10.GL_CCW)// 设置逆时针方法为面的“前面”
            gl.glEnable(GL10.GL_CULL_FACE)// 打开 忽略“后面”设置
//            gl.glCullFace(GL10.GL_BACK)// 明确指明“忽略“哪个面

            // 光源配置 , http://wiki.jikexueyuan.com/project/opengl-es-guide/set-lighting.html
//            gl.glEnable(GL10.GL_LIGHTING)// 首先要开光源的总开关
//            gl.glEnable(GL10.GL_LIGHT0)// 打开某个光源如0号光源
//            gl.glLightf(GL10.GL_LIGHT0, GL10.GL_POSITION, 0.345f)
            // 材质配置
//            gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 120f)

//            initScene(gl)
//            drawScene(gl)


            // 顶点构造
            val vbb = ByteBuffer.allocateDirect(vertexArray.size * 4)
            vbb.order(ByteOrder.nativeOrder())
            val vertex = vbb.asFloatBuffer()
            vertex.put(vertexArray)
            vertex.position(0)
            // 颜色构造-颜色
            val cbb = ByteBuffer.allocateDirect(colors.size * 4)
            cbb.order(ByteOrder.nativeOrder())
            val colorBuffer = cbb.asFloatBuffer()
            colorBuffer.put(colors)
            colorBuffer.position(0)
            // 颜色构造-绑定面
            val ibb = ByteBuffer.allocateDirect(indices.size * 2)
            ibb.order(ByteOrder.nativeOrder())
            val indexBuffer = ibb.asShortBuffer()
            indexBuffer.put(indices)
            indexBuffer.position(0)


            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)// 开启管道

            // 放入顶点
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex)

            // 顶点红色显示
            gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f)
            gl.glDrawArrays(GL10.GL_POINTS, 0, 3)

            // 顶点绿色连续
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f)
            gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 3)

            // 面-蓝色三角形
//            gl.glColor4f(0.0f, 0.0f, 1.0f, 0.1f)
//            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3)
            // 面-自动渐变颜色
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)
            gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY)

            // 球，http://wiki.jikexueyuan.com/project/opengl-es-guide/sphere.html
//            drawBoll(gl)

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)// 关闭管道

            angle += 1f

        }

        private fun drawBoll(gl:GL10) {
            var theta: Float
            var co: Float
            var si: Float
            var r1: Float
            var r2: Float
            var h1: Float
            var h2: Float
            val step = 2.0f
            val v = Array(32) { FloatArray(3) }
            val vbb: ByteBuffer
            val vBuf: FloatBuffer

            vbb = ByteBuffer.allocateDirect(v.size * v[0].size * 4)
            vbb.order(ByteOrder.nativeOrder())
            vBuf = vbb.asFloatBuffer()
//            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)// 打开法线数组


            var pai: Float = -90.0f
            while (pai < 90.0f) {
                var n = 0
                r1 = Math.cos(pai * Math.PI / 180.0).toFloat()
                r2 = Math.cos((pai + step) * Math.PI / 180.0).toFloat()
                h1 = Math.sin(pai * Math.PI / 180.0).toFloat()
                h2 = Math.sin((pai + step) * Math.PI / 180.0).toFloat()
                theta = 0.0f
                while (theta <= 360.0f) {
                    co = Math.cos(theta * Math.PI / 180.0).toFloat()
                    si = -Math.sin(theta * Math.PI / 180.0).toFloat()
                    v[n][0] = r2 * co
                    v[n][1] = h2
                    v[n][2] = r2 * si
                    v[n + 1][0] = r1 * co
                    v[n + 1][1] = h1
                    v[n + 1][2] = r1 * si
                    vBuf.put(v[n])
                    vBuf.put(v[n + 1])
                    n += 2
                    if (n > 31) {
                        vBuf.position(0)
                        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuf)
                        gl.glNormalPointer(GL10.GL_FLOAT, 0, vBuf)
                        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, n)
                        n = 0
                        theta -= step
                    }
                    theta += step
                }
                vBuf.position(0)
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuf)
                gl.glNormalPointer(GL10.GL_FLOAT, 0, vBuf)
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, n)
                pai += step
            }


//            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
        }


        private fun initScene(gl:GL10) {
            val amb = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
            val diff = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
            val spec = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
            val pos = floatArrayOf(0.0f, 5.0f, 5.0f, 1.0f)
            val spot_dir = floatArrayOf(0.0f, -1.0f, 0.0f)

            gl.glEnable(GL10.GL_DEPTH_TEST)
            gl.glEnable(GL10.GL_CULL_FACE)
            gl.glEnable(GL10.GL_LIGHTING)
            gl.glEnable(GL10.GL_LIGHT0)

            val abb = ByteBuffer.allocateDirect(amb.size * 4)
            abb.order(ByteOrder.nativeOrder())
            val ambBuf = abb.asFloatBuffer()
            ambBuf.put(amb)
            ambBuf.position(0)

            val dbb = ByteBuffer.allocateDirect(diff.size * 4)
            dbb.order(ByteOrder.nativeOrder())
            val diffBuf = dbb.asFloatBuffer()
            diffBuf.put(diff)
            diffBuf.position(0)

            val sbb = ByteBuffer.allocateDirect(spec.size * 4)
            sbb.order(ByteOrder.nativeOrder())
            val specBuf = sbb.asFloatBuffer()
            specBuf.put(spec)
            specBuf.position(0)

            val pbb = ByteBuffer.allocateDirect(pos.size * 4)
            pbb.order(ByteOrder.nativeOrder())
            val posBuf = pbb.asFloatBuffer()
            posBuf.put(pos)
            posBuf.position(0)

            val spbb = ByteBuffer.allocateDirect(spot_dir.size * 4)
            spbb.order(ByteOrder.nativeOrder())
            val spot_dirBuf = spbb.asFloatBuffer()
            spot_dirBuf.put(spot_dir)
            spot_dirBuf.position(0)

            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambBuf)
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffBuf)
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specBuf)
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, posBuf)
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, spot_dirBuf)
            gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_EXPONENT, 0.0f)
            gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_CUTOFF, 45.0f)
            gl.glLoadIdentity()
            GLU.gluLookAt(gl, 0.0f, 4.0f, 4.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f)
        }
        private fun drawScene(gl:GL10) {
            val mat_amb = floatArrayOf(0.2f * 0.4f, 0.2f * 0.4f, 0.2f * 1.0f, 1.0f)
            val mat_diff = floatArrayOf(0.4f, 0.4f, 1.0f, 1.0f)
            val mat_spec = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

            val mabb = ByteBuffer.allocateDirect(mat_amb.size * 4)
            mabb.order(ByteOrder.nativeOrder())
            val mat_ambBuf = mabb.asFloatBuffer()
            mat_ambBuf.put(mat_amb)
            mat_ambBuf.position(0)

            val mdbb = ByteBuffer.allocateDirect(mat_diff.size * 4)
            mdbb.order(ByteOrder.nativeOrder())
            val mat_diffBuf = mdbb.asFloatBuffer()
            mat_diffBuf.put(mat_diff)
            mat_diffBuf.position(0)

            val msbb = ByteBuffer.allocateDirect(mat_spec.size * 4)
            msbb.order(ByteOrder.nativeOrder())
            val mat_specBuf = msbb.asFloatBuffer()
            mat_specBuf.put(mat_spec)
            mat_specBuf.position(0)

            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,
                    GL10.GL_AMBIENT, mat_ambBuf)
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,
                    GL10.GL_DIFFUSE, mat_diffBuf)
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,
                    GL10.GL_SPECULAR, mat_specBuf)
            gl.glMaterialf(GL10.GL_FRONT_AND_BACK,
                    GL10.GL_SHININESS, 64.0f)
        }
    }
}