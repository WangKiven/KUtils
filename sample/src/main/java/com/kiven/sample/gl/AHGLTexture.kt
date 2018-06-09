package com.kiven.sample.gl

import android.graphics.BitmapFactory
import android.opengl.GLUtils
import com.kiven.sample.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AHGLTexture : AHGLSuper() {

    val vertexArray = floatArrayOf(
            -1f, -1f, -5f,
            1f, -1f, -5f,
            -1f, 1f, -5f,
            1f, 1f, -5f
    )
    val vertexBuffer: FloatBuffer

    val normalArray = floatArrayOf(
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f
    )

    val normalBuffer:FloatBuffer

    init {
        val vbb = ByteBuffer.allocateDirect(vertexArray.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)


        val nbb = ByteBuffer.allocateDirect(normalArray.size * 4)
        nbb.order(ByteOrder.nativeOrder())
        normalBuffer = nbb.asFloatBuffer()
        normalBuffer.put(normalArray)
        normalBuffer.position(0)
    }

    val textures = IntArray(1)
    val textureCoors = arrayListOf<FloatBuffer>()

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        // 加载纹理

        gl.glGenTextures(1, textures, 0)
        val currTextureId = textures[0]
        gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId)
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST)
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST)
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT)
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT)

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        val textureCoor = floatArrayOf(
                0f, 1f,
                1f, 1f,
                0f, 0f,
                1f, 0f
        )
        val textureCoorB = ByteBuffer.allocateDirect(textureCoor.size * 4)
        textureCoorB.order(ByteOrder.nativeOrder())
        val textureCoorBuff = textureCoorB.asFloatBuffer()
        textureCoorBuff.put(textureCoor)
        textureCoorBuff.position(0)
        textureCoors.add(textureCoorBuff)

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
        gl.glNormalPointer(GL10.GL_FIXED, 0, normalBuffer)
//        gl.glNormal3x(0, 0, 1)

        // 纹理
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoors[0])
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])


        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertexArray.size)

        gl.glDisable(GL10.GL_TEXTURE_2D)

        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)// 关闭管道
    }

    private fun loadLight(gl: GL10) {
        // 光源配置 , http://wiki.jikexueyuan.com/project/opengl-es-guide/set-lighting.html
        gl.glEnable(GL10.GL_LIGHTING)// 首先要开光源的总开关
        gl.glEnable(GL10.GL_LIGHT0)// 打开某个光源如0号光源

        // 光源的位置GL_POSITION,值为(x,y,z,w). 平行光将 w 设为0.0, 对于点光源，将 w 设成非0值，通常设为1.0. (x,y,z)为点光源的坐标位置
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, floatArrayOf(0f, 0f, 1f, 1f), 0)
        // 将点光源设置成聚光灯，需要同时设置 GL_SPOT_DIRECTION,GL_SPOT_CUTOFF 等参数
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, floatArrayOf(0f, 0f, -1f), 0)// 光照方向
        // GL_SPOT_CUTOFF 参数设置聚光等发散角度（0到90度）
        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_CUTOFF, 8.0f)
        // GL_SPOT_EXPONENT 给出了聚光灯光源汇聚光的程度，值越大，则聚光区域越小（聚光能力更强）。
        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_EXPONENT, 10.0f)


//        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_POSITION, 0.345f)
        // 设置颜色光，GL10.GL_AMBIENT：环境光，GL10.GL_DIFFUSE：散射光，GL_SPECULAR：反射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, floatArrayOf(0.7f, 0.5f, 1f, 1f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, floatArrayOf(1f, 0.3f, 0.4f, 1f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, floatArrayOf(1f, 1f, 1f, 1f), 0)


        // 设置材质：GL10.GL_AMBIENT：环境光，GL10.GL_DIFFUSE：散射光，GL_SPECULAR：反射光
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, floatArrayOf(1f, 0.9f, 0.4f, 1f), 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, floatArrayOf(1f, 0.9f, 0.4f, 1f), 0)
        // 与高光配合使用，作用于光照中心区域
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, floatArrayOf(1f, 1f, 0.8f, 1f), 0)
        // 高光反射区域，params[]数越大，高光区域越小，越暗
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 3.5f)
        // 材质自发光
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, floatArrayOf(0.7f, 0.5f, 0.1f, 0f), 0)
    }
}