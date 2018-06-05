package com.kiven.sample.gl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AHGLLightFace : AHGLSuper() {

    // 顶点 3个 可组成一个面
    val vertexArray = floatArrayOf(
            0f, 0f, 1f,
            0f, 0f, 0f,
            0f, 1f, 0f,
            1f, 0f, 0f)
    // 面的顶点颜色
    val colors = floatArrayOf(
            1f, 0f, 0f, 1f,
            0f, 0f, 1f, 1f,
            0f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f
    )
    val indices = shortArrayOf(// 面构造，2个面，由第一二三个顶点构成
            0, 1, 2,
            1, 2, 3/*,
            0, 3, 1*/
    )
    private var angle = 0f

    override fun onDrawFrame(gl: GL10) {
        super.onDrawFrame(gl)

        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        gl.glLoadIdentity()// 将当前矩阵回复最初的无变换的矩阵
        gl.glTranslatef(0f, 0f, -4f)
        gl.glRotatef(angle, 0f, 1f, 1f)// 旋转


        // 前面后面配置-似乎仅对面的绘制有用
        gl.glFrontFace(GL10.GL_CCW)// 设置逆时针方法为面的“前面”
        gl.glEnable(GL10.GL_CULL_FACE)// 打开 忽略“后面”设置
        gl.glCullFace(GL10.GL_BACK)// 明确指明“忽略“哪个面

        // 光源配置 , http://wiki.jikexueyuan.com/project/opengl-es-guide/set-lighting.html
        gl.glEnable(GL10.GL_LIGHTING)// 首先要开光源的总开关
        gl.glEnable(GL10.GL_LIGHT0)// 打开某个光源如0号光源

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, floatArrayOf(1f, 1f, -1f), 0)// 光照方向

//        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_POSITION, 0.345f)
        // 设置颜色光，GL10.GL_AMBIENT：环境光，GL10.GL_DIFFUSE：散射光，GL_SPECULAR：反射光
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, floatArrayOf(0.1f, 0.21f, 0.4f, 1f), 0)

        // 设置材质：GL10.GL_AMBIENT：环境光，GL10.GL_DIFFUSE：散射光，GL_SPECULAR：反射光
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, floatArrayOf(0.5f, 0.21f, 0.4f, 1f), 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, floatArrayOf(0.7f, 0.3f, 0.4f, 1f), 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, floatArrayOf(1f, 0.4f, 0.4f, 1f), 0)
        // 高光反射区域，params[]数越大，高光区域越小，越暗
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, floatArrayOf(10.5f), 0)
        // 材质配置
//            gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 120f)


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



        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)// 开启管道, 启用顶点坐标数组

        // 放入顶点
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex)


        // 面-自动渐变颜色
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)// 关闭管道

        angle += 1f

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION) // 设置为投影矩阵
//        gl.glLoadIdentity() // 设置为单位矩阵
        val ratio = width.toFloat() / height // 设置视口比例
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 1f, 10f) // 设置为透视投影

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        // 设置为单位矩阵。
        gl.glLoadIdentity()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        gl.glDisable(GL10.GL_DITHER) // 关闭抗抖动
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST) // Hint模式

        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST) // 启用深度检测
    }
}