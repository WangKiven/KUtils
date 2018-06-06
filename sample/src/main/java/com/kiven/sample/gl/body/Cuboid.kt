package com.kiven.sample.gl.body

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * 长方体
 */
open class Cuboid(center: Point, l: Float, w: Float, h: Float, a:Float = 0f) {
    val vertex: FloatBuffer
    val indexBuffer:ShortBuffer
    val indexCount:Int

    init {
        // 顶点
        /*val vertexArray = floatArrayOf(
                center.x + 0.5f * l, center.y - 0.5f * w, center.z - 0.5f * h,
                center.x + 0.5f * l, center.y + 0.5f * w, center.z - 0.5f * h,
                center.x + 0.5f * l, center.y - 0.5f * w, center.z + 0.5f * h,
                center.x - 0.5f * l, center.y - 0.5f * w, center.z - 0.5f * h,
                center.x - 0.5f * l, center.y + 0.5f * w, center.z - 0.5f * h,
                center.x - 0.5f * l, center.y - 0.5f * w, center.z + 0.5f * h,
                center.x - 0.5f * l, center.y + 0.5f * w, center.z + 0.5f * h,
                center.x + 0.5f * l, center.y + 0.5f * w, center.z + 0.5f * h
        )*/

        val vertexArray = floatArrayOf(
                center.x + 0.5f * l, center.y - 0.5f * w, center.z - 0.5f * h,
                center.x + 0.5f * l, center.y + 0.5f * w, center.z - 0.5f * h,
                center.x + 0.5f * l, center.y - 0.5f * w, center.z + 0.5f * h,
                center.x - 0.5f * l, center.y - 0.5f * w, center.z - 0.5f * h,
                center.x - 0.5f * l, center.y + 0.5f * w, center.z - 0.5f * h,
                center.x - 0.5f * l, center.y - 0.5f * w, center.z + 0.5f * h,
                center.x - 0.5f * l, center.y + 0.5f * w, center.z + 0.5f * h,
                center.x + 0.5f * l, center.y + 0.5f * w, center.z + 0.5f * h
        )
        Matrix.rotateM(vertexArray, 0, a, 1f, 0f, 0f)


        // 面 (前后左右上下)
        val indices = shortArrayOf(
                3, 0,5,
                0,2,5,

                1,4,6,
                6,7,1,

                4,3,5,
                4,5,6,

                0,1,7,
                0,7,2,

                6,5,2,
                6,2,7,

                3,4,1,
                3,1,0
        )
        indexCount = indices.size

        // 顶点构造
        val vbb = ByteBuffer.allocateDirect(vertexArray.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertex = vbb.asFloatBuffer()
        vertex.put(vertexArray)
        vertex.position(0)

        // 面构造
        val ibb = ByteBuffer.allocateDirect(indices.size * 2)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    fun drawSelf(gl: GL10) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex)
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indexCount, GL10.GL_UNSIGNED_SHORT, indexBuffer)
        gl.glNormalPointer(GL10.GL_FIXED, 0,vertex )
    }
}