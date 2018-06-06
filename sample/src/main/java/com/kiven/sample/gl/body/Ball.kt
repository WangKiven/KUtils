package com.kiven.sample.gl.body

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10


class Ball(scale: Int) {
    private val vertexBuffer: IntBuffer  //顶点坐标数据缓冲
    private val nomalBuffer: IntBuffer  //顶点法向量数据缓冲
    private val indexBuffer: ByteBuffer //顶点构建索引数据缓冲
    var angleX: Float = 0.toFloat()  //沿x轴旋转角度
    internal var vCount = 0
    internal var iCount = 0

    init {
        //顶点坐标初始化数据
        val UNIT_SIZE = 10000
        val alVertex = ArrayList<Int>()
        val angleSpan = 18          //将小球进行单位切分的角度
        var vAngle = -90
        while (vAngle <= 90) {  //垂直方向angleSpan度一份
            var hAngle = 0
            while (hAngle < 360) { //水平方向angleSpan度一份
                //纵向横向各到一个角度后计算对应的此点在球面上的坐标
                val xozLength = scale.toDouble() * UNIT_SIZE.toDouble() * Math.cos(Math.toRadians(vAngle.toDouble()))
                val x = (xozLength * Math.cos(Math.toRadians(hAngle.toDouble()))).toInt()
                val y = (xozLength * Math.sin(Math.toRadians(hAngle.toDouble()))).toInt()
                val z = (scale.toDouble() * UNIT_SIZE.toDouble() * Math.sin(Math.toRadians(vAngle.toDouble()))).toInt()
                alVertex.add(x)
                alVertex.add(y)
                alVertex.add(z)
                hAngle += angleSpan
            }
            vAngle += angleSpan
        }
        vCount = alVertex.size / 3  //顶点数量为坐标值数量的三分之一，因为一个顶点有三个坐标
        //将alVertix中的坐标值转存到一个int数组中
        val vertices = IntArray(alVertex.size)
        for (i in 0 until alVertex.size) {
            vertices[i] = alVertex[i]
        }
        //创建顶点坐标数据缓冲
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) //设置字节顺序
        vertexBuffer = vbb.asIntBuffer()  //转换成int型缓冲
        vertexBuffer.put(vertices)   //向缓冲区放入顶点坐标数据
        vertexBuffer.position(0)  //设置缓冲区起始位置

        //创建顶点坐标数据缓冲
        val nbb = ByteBuffer.allocateDirect(vertices.size * 4) //一个整型是4个字节
        nbb.order(ByteOrder.nativeOrder())  //设置字节顺序   由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        nomalBuffer = nbb.asIntBuffer() //转换成int型缓冲
        nomalBuffer.put(vertices)      //想缓冲区放入顶点坐标数据
        nomalBuffer.position(0)         //设置缓冲区起始位置

        val alIndex = ArrayList<Int>()
        val row = 180 / angleSpan + 1 //球面切分的行数
        val col = 360 / angleSpan  //球面切分的列数
        for (i in 0 until row) {  //对每一行循环
            if (i > 0 && i < row - 1) {
                //中间行
                for (j in -1 until col) {
                    //中间行的两个相邻点与下一行的对应点构成三角形
                    val k = i * col + j
                    alIndex.add(k + col)
                    alIndex.add(k + 1)
                    alIndex.add(k)
                }
                for (j in 0 until col + 1) {
                    //中间行的两个相邻点与上一行的对应点构成三角形
                    val k = i * col + j
                    alIndex.add(k - col)
                    alIndex.add(k - 1)
                    alIndex.add(k)
                }
            }
        }
        iCount = alIndex.size
        val indices = ByteArray(iCount)
        for (i in 0 until iCount) {
            indices[i] = alIndex[i].toByte()
        }
        //三角形构造数据索引缓冲
        indexBuffer = ByteBuffer.allocateDirect(iCount)  //由于indices是byte型的，索引不用乘以4
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    fun drawSelf(gl: GL10) {
        gl.glRotatef(angleX, 1f, 0f, 0f)  //沿x轴旋转
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)  //启用顶点坐标数组
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)  //启用顶点向量数组

        //为画笔指定顶点坐标数据
        gl.glVertexPointer(
                3, //顶点坐标数量，三个坐标一个顶点
                GL10.GL_FIXED, //顶点坐标数据类型
                0, //连续顶点之间的数据间隔
                vertexBuffer //顶点坐标数据
        )
        //为画笔指定顶点向量数据
        gl.glNormalPointer(GL10.GL_FIXED, 0, nomalBuffer)
        //绘制图形
        gl.glDrawElements(
                GL10.GL_TRIANGLES, //以三角形的方式填充
                iCount, GL10.GL_UNSIGNED_BYTE, indexBuffer)

    }

}