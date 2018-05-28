package com.kiven.sample.gl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10

class AHGLSample : AHGLSuper() {

    // 顶点 3个 可组成一个面
    var vertexArray = floatArrayOf(
            0f, 0f, 1f,
            0f, 0f, 0f,
            0f, 1f, 0f,
            1f, 0f, 0f)
    // 面的顶点颜色
    var colors = floatArrayOf(
            1f, 0f, 0f, 1f,
            0f, 0f, 1f, 1f,
            0f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f
    )
    var indices = shortArrayOf(// 面构造，2个面，由第一二三个顶点构成
            0, 1, 2,
            1, 2, 3
    )
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
        gl.glCullFace(GL10.GL_BACK)// 明确指明“忽略“哪个面

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
        gl.glDrawArrays(GL10.GL_POINTS, 0, vertexArray.size)

        // 顶点绿色连续
        gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f)
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, vertexArray.size)

        // 面-蓝色三角形
//            gl.glColor4f(0.0f, 0.0f, 1.0f, 0.1f)
//            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3)
        // 面-自动渐变颜色
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY)

        // 球，http://wiki.jikexueyuan.com/project/opengl-es-guide/sphere.html
//            drawBoll(gl)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)// 关闭管道

        angle += 1f

    }
}