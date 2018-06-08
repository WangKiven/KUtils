package com.kiven.sample.gl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import com.kiven.sample.R
import com.kiven.sample.gl.body.Cuboid
import com.kiven.sample.gl.body.Point
import javax.microedition.khronos.opengles.GL10
import android.R.attr.order
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocateDirect
import java.nio.ByteOrder


class AHGLShine : AHGLSuper() {
    override fun onDrawFrame(gl: GL10) {
        super.onDrawFrame(gl)

        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)



        gl.glLoadIdentity()// 将当前矩阵回复最初的无变换的矩阵
        gl.glTranslatef(0f, 0f, -4f)
        gl.glRotatef(dragx*0.2f, 0f, 1f, 0f)// 旋转
        gl.glRotatef(dragy*0.2f, 1f, 0f, 0f)// 旋转


        // 前面后面配置-似乎仅对面的绘制有用
        gl.glFrontFace(GL10.GL_CCW)// 设置逆时针方法为面的“前面”
        gl.glEnable(GL10.GL_CULL_FACE)// 打开 忽略“后面”设置
        gl.glCullFace(GL10.GL_BACK)// 明确指明“忽略“哪个面

        // 光源配置 , http://wiki.jikexueyuan.com/project/opengl-es-guide/set-lighting.html
        gl.glEnable(GL10.GL_LIGHTING)// 首先要开光源的总开关
        gl.glEnable(GL10.GL_LIGHT0)// 打开某个光源如0号光源

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, floatArrayOf(0f, 0f, -1f), 0)// 光照方向
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, floatArrayOf(0f, 0f, 2f, 0f), 0)

//        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_POSITION, 0.345f)
        // 设置颜色光，GL10.GL_AMBIENT：环境光，GL10.GL_DIFFUSE：散射光，GL_SPECULAR：反射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, floatArrayOf(0.5f, 0.21f, 0.4f, 1f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, floatArrayOf(0.7f, 0.3f, 0.4f, 1f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, floatArrayOf(1f, 1f, 0.4f, 1f), 0)

        // 设置材质：GL10.GL_AMBIENT：环境光，GL10.GL_DIFFUSE：散射光，GL_SPECULAR：反射光
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, floatArrayOf(0.5f, 0.21f, 0.4f, 1f), 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, floatArrayOf(0.7f, 0.3f, 0.4f, 1f), 0)
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, floatArrayOf(1f, 0.7f, 0.5f, 1f), 0)
        // 高光反射区域，params[]数越大，高光区域越小，越暗
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, floatArrayOf(10.5f), 0)




        // todo 加载纹理
        // 创建材质
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val textures = IntArray(1)
        // 分配材质id, n:请求分配n个材质id
        gl.glGenTextures(1, textures, 0)
//        // 将要使用的id绑定到opengl
//        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
        // 給 Texture 填充设置参数. 需要比较清晰的图像使用 GL10.GL_NEAREST, 而使用 GL10.GL_LINEAR 则会得到一个较模糊的图像
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
        // 告诉 OpenGL 库如何去渲染这些不存在的 Texture 部分. GL_REPEAT 重复 Texture, GL_CLAMP_TO_EDGE 只靠边线复制一次
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT)
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT)
        // 定义 UV 坐标
        val textureCoordinates = floatArrayOf(
                0f, 1f,
                1f, 1f,
                0f, 0f
        )
        // 将 Bitmap 资源和 Texture 绑定起來
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)

        //
        val byteBuf = ByteBuffer.allocateDirect(textureCoordinates.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        val textureBuffer = byteBuf.asFloatBuffer()
        textureBuffer.put(textureCoordinates)
        textureBuffer.position(0)




        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)// 开启管道, 启用顶点坐标数组
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)

        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])// 将要使用的id绑定到opengl
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        Cuboid(Point(), 1f, 1f, 1f, a = dragy*0.2f).drawSelf(gl)

        loadTexture(gl)

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glDisable(GL10.GL_TEXTURE_2D)

        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)// 关闭管道
    }

    /**
     * 加载纹理
     */
    private fun loadTexture(gl: GL10) {

    }
}
