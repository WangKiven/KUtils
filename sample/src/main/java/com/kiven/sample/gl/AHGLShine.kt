package com.kiven.sample.gl

import com.kiven.sample.gl.body.Cuboid
import com.kiven.sample.gl.body.Point
import javax.microedition.khronos.opengles.GL10

class AHGLShine : AHGLSuper() {
    override fun onDrawFrame(gl: GL10) {
        super.onDrawFrame(gl)

        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)



        gl.glLoadIdentity()// 将当前矩阵回复最初的无变换的矩阵
        gl.glTranslatef(0f, 0f, -4f)
//        gl.glRotatef(dragx*0.2f, 0f, 1f, 0f)// 旋转
//        gl.glRotatef(dragy*0.2f, 1f, 0f, 0f)// 旋转


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
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, floatArrayOf(1f, 0.7f, 0.5f, 1f), 0)
        // 高光反射区域，params[]数越大，高光区域越小，越暗
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, floatArrayOf(10.5f), 0)





        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)// 开启管道, 启用顶点坐标数组
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)

        Cuboid(Point(), 1f, 1f, 1f, a = dragy*0.2f).drawSelf(gl)

        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)// 关闭管道
    }
}
