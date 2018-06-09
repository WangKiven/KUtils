package com.kiven.sample.gl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class AHGL2Sample : AHGL2Super() {

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
    // 用于渲染形状的顶点的OpenGLES 图形代码
    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}"
    // 用于渲染形状的外观（颜色或纹理）的OpenGLES 代码
    private val fragmentShaderCode = (
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}")

    val vertexBuffer: FloatBuffer
    val colorBuffer: FloatBuffer
    val indexBuffer: ShortBuffer

    val mProgram:Int

    init {
        // 顶点构造
        val vbb = ByteBuffer.allocateDirect(vertexArray.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)

        // 颜色构造-颜色
        val cbb = ByteBuffer.allocateDirect(colors.size * 4)
        cbb.order(ByteOrder.nativeOrder())
        colorBuffer = cbb.asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)

        // 绑定面
        val ibb = ByteBuffer.allocateDirect(indices.size * 2)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)


        //初始化shader
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(mProgram)

        val mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, vertexBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        val mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
        GLES20.glUniform4fv(mColorHandle, 1, floatArrayOf(1f, 0f, 1f, 1f), 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexArray.size/3)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }
}
