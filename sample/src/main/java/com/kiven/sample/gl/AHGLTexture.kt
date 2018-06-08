package com.kiven.sample.gl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class AHGLTexture : AHGLSuper() {

    val vertexArray = floatArrayOf(
            -1f, -1f, -5f,
            1f, -1f, -5f,
            -1f, 1f, -5f,
            1f, 1f, -5f
    )
    val vertexBuffer: FloatBuffer

    init {
        val vbb = ByteBuffer.allocateDirect(vertexArray.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)


    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        gl.glLoadIdentity()// 将当前矩阵回复最初的无变换的矩阵

        loadLight(gl)
        // 前面后面配置-似乎仅对面的绘制有用
        gl.glFrontFace(GL10.GL_CCW)// 设置逆时针方法为面的“前面”
        gl.glEnable(GL10.GL_CULL_FACE)// 打开 忽略“后面”设置
        gl.glCullFace(GL10.GL_BACK)// 明确指明“忽略“哪个面


        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)// 开启管道, 启用顶点坐标数组
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)

        // 放入顶点 和 法线
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)

        gl.glNormal3x(0, 0, -1)
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertexArray.size)

        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)// 关闭管道
    }

    private fun loadLight(gl: GL10) {
        // 光源配置 , http://wiki.jikexueyuan.com/project/opengl-es-guide/set-lighting.html
        gl.glEnable(GL10.GL_LIGHTING)// 首先要开光源的总开关
        gl.glEnable(GL10.GL_LIGHT0)// 打开某个光源如0号光源

        // 光源的位置GL_POSITION,值为(x,y,z,w). 平行光将 w 设为0.0, 对于点光源，将 w 设成非0值，通常设为1.0. (x,y,z)为点光源的坐标位置
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, floatArrayOf(0f, 0f, 0f, 1f), 0)
        // 将点光源设置成聚光灯，需要同时设置 GL_SPOT_DIRECTION,GL_SPOT_CUTOFF 等参数
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, floatArrayOf(0f, 0f, -1f), 0)// 光照方向
        // GL_SPOT_CUTOFF 参数设置聚光等发散角度（0到90度）
        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_CUTOFF, 9.0f)
        // GL_SPOT_EXPONENT 给出了聚光灯光源汇聚光的程度，值越大，则聚光区域越小（聚光能力更强）。
        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_EXPONENT, 10.0f)


//        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_POSITION, 0.345f)
        // 设置颜色光，GL10.GL_AMBIENT：环境光，GL10.GL_DIFFUSE：散射光，GL_SPECULAR：反射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, floatArrayOf(1f, 1f, 1f, 1f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, floatArrayOf(1f, 0.3f, 0.4f, 1f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, floatArrayOf(1f, 1f, 1f, 1f), 0)
    }
}