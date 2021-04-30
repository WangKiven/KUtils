package com.kiven.sample.network.socket

import android.net.Uri
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KFile
import com.kiven.kutils.tools.KPath
import com.kiven.kutils.tools.KUtil
import com.sxb.kutils_ktx.util.RxBus
import java.io.*
import java.net.ServerSocket
import java.net.Socket

object SocketFactory {
    const val newAccept = "SocketFactory.newAccept"

    private val bufferLength = 1024
    private val serviceSockets = mutableListOf<ServerSocket>()
    private val sockets = mutableListOf<Socket>()

    fun closeServiceSocket(port: Int) {
        sockets.removeAll {
            if (it.localPort == port) {
                if (!it.isConnected) it.close()
                true
            } else false
        }

        serviceSockets.removeAll {
            if (it.localPort == port) {
                if (!it.isClosed) it.close()
                true
            } else false
        }
    }
    fun createServiceSocket(port: Int): ServerSocket {
        for (socket in serviceSockets) {
            if (socket.localPort == port) return socket
        }

        val socket = ServerSocket(port)
        serviceSockets.add(socket)
        Thread {
            while (!socket.isClosed) {
                val ns = socket.accept()
                ns.apply {
                    KLog.i(inetAddress.toString())
                    KLog.i(localAddress.toString())
                    KLog.i(remoteSocketAddress.toString())
                    KLog.i(localSocketAddress.toString())
                }
                sockets.add(ns)
                Thread {
                    val inputStream = ns.getInputStream()

                    val buff = ByteArray(bufferLength)
                    // 头
                    var r = inputStream.read(buff, 0, 10)
                    while (r >= 0) {
                        // 头转字符串
                        val head = String(buff, 0, 10, Charsets.US_ASCII)
                        // 类型和长度
                        val typeInt = head.substring(0, 1).toInt()
                        val contextLength = head.substring(1, 10).toInt()

                        // 文件名
                        val fileNameLength = inputStream.read()
                        val fileName: String = if (fileNameLength > 0) {
                            inputStream.read(buff, 0, fileNameLength)
                            String(buff, 0, fileNameLength, Charsets.UTF_8)
                        } else {
                            System.currentTimeMillis().toString()
                        }
                        // 读取内容
                        val type = DataType.fromInt(typeInt)
                        when (type) {
                            DataType.String -> {
                                inputStream.read(buff, 0, contextLength)
                                val content = String(buff, 0, contextLength, Charsets.UTF_8)
                                RxBus.post(newAccept, Message(ns.inetAddress.hostAddress, port, type, content))
                            }
                            else -> {
                                val file = saveFile(inputStream, fileName, buff, contextLength)
                                RxBus.post(newAccept, Message(ns.inetAddress.hostAddress, port, type, file))
                            }
                        }

                        // 头
                        r = inputStream.read(buff, 0, 10)
                    }

                }.start()
            }
        }.start()

        return socket
    }

    private fun saveFile(inputStream: InputStream, fileName: String, buffer: ByteArray, length: Int): File {
        val file = KFile.createNameFile(fileName, KUtil.getApp().cacheDir)
        val outputStream = FileOutputStream(file)

        var readedLength = 0
        var surplusLength = length//剩余长度
        var curReadedLength = 0
        while (readedLength < length) {
            curReadedLength = if (surplusLength < buffer.size) {
                inputStream.read(buffer, 0, surplusLength)
            } else {
                inputStream.read(buffer, 0, buffer.size)
            }

            // 提前结束了，可能是对放出错了
            if (curReadedLength < 0) break

            outputStream.write(buffer, 0, curReadedLength)
            readedLength += curReadedLength

            surplusLength = length - readedLength
        }

        outputStream.close()

        return file
    }

    fun closeSocket(ip: String, port: Int?) {
        sockets.removeAll {
            if (it.inetAddress.hostAddress == ip && (port == null || it.localPort == port)) {
                if (!it.isConnected) it.close()
                true
            } else false
        }
    }

    fun sendSocketMessage(ip: String, port: Int, type: DataType, data: Any, force: Boolean = false) {
//        val socket = sockets.firstOrNull { it.inetAddress.hostAddress == ip } ?: Socket(ip, 8899)

        val socket: Socket
        val ss = sockets.firstOrNull { it.inetAddress.hostAddress == ip && !it.isClosed && it.isConnected }
        if (ss == null || (force && ss.port != port)) {
            socket = Socket(ip, port)
            sockets.add(socket)
        } else socket = ss

        socket.getOutputStream()?.apply {
            when {
                data is ByteArray -> {
                    val pre = String.format("%d%09d", type.value, data.size)
                    write(pre.toByteArray(Charsets.US_ASCII))
                    write(0)
                    write(data)
                }
                data is String && type == DataType.String -> {
                    val byteData = data.toByteArray(Charsets.UTF_8)
                    if (byteData.size > bufferLength) throw Throwable("字符串长度不能超过${bufferLength}字节")

                    val pre = String.format("%d%09d", type.value, byteData.size)
                    write(pre.toByteArray(Charsets.US_ASCII))
                    write(0)
                    write(byteData)
                }
                else -> {
                    val file = when (data) {
                        is String -> File(data)
                        is File -> data
                        is Uri -> File(KPath.getPath(data))
                        else -> throw Throwable("数据类型无法判断")
                    }
                    if (file.exists() && file.isFile) {
                        // 文件名称及长度
                        var fileName = file.name
                        var fileNameLength = fileName.toByteArray(Charsets.UTF_8).size
                        while (fileNameLength > Byte.MAX_VALUE) {
                            fileName = fileName.substring(1)

                            fileNameLength = fileName.toByteArray(Charsets.UTF_8).size
                        }

                        // 头部信息
                        val pre = String.format("%d%09d", type.value, file.length().toInt())
                        write(pre.toByteArray(Charsets.US_ASCII))
                        write(fileNameLength)
                        write(fileName.toByteArray(Charsets.UTF_8))

                        // 发送文件
                        val fileInputStream = if (data !is Uri) FileInputStream(file)
                        else FileInputStream(KUtil.getApp().contentResolver.openFileDescriptor(data, "r")!!.fileDescriptor)

                        val readData = ByteArray(bufferLength)
                        var readLenght = fileInputStream.read(readData)
                        while (readLenght > -1) {
                            write(readData, 0, readLenght)
                            readLenght = fileInputStream.read(readData)
                        }

                        fileInputStream.close()
                    }
                }
            }
        }

        sockets.removeAll { it.isClosed || !it.isConnected || it.isOutputShutdown }
    }

    enum class DataType(val value: Int) {
        String(0), Image(1), File(2);

        companion object {
            fun fromInt(value: Int) = values().first { it.value == value }
        }

    }
}