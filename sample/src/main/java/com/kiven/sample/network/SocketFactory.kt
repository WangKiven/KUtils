package com.kiven.sample.network

import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket

object SocketFactory {
    private val serviceSockets = mutableListOf<ServerSocket>()
    private val sockets = mutableListOf<Socket>()
    fun createServiceSocket(port: Int): ServerSocket {
        for (socket in serviceSockets) {
            if (socket.localPort == port) return socket
        }

        val socket = ServerSocket(port)
        serviceSockets.add(socket)
        Thread {
            while (!socket.isClosed) {
                val ns = socket.accept()
                sockets.add(ns)
                Thread {
                    val inputStream = ns.getInputStream()

                    val buff = ByteArray(1024)
                    var r = inputStream.read(buff, 0, 10)
                    while (r >= 0) {
                        val head = String(buff, 0, 10, Charsets.US_ASCII)
                        val type = head.substring(0, 1).toInt()
                        val contextLength = head.substring(1, 10)

                        val fileNameLength = inputStream.read()
                        val fileName:String = if (fileNameLength > 0) {
                            inputStream.read(buff, 0, fileNameLength)
                            String(buff, 0, fileNameLength, Charsets.UTF_8)
                        } else {
                            System.currentTimeMillis().toString()
                        }

                        r = inputStream.read(buff, 0, 10)
                    }

                }.run()
            }
        }.run()

        return socket
    }

    fun sendSocketMessage(ip: String, port: Int, type: FileType, data: Any, force: Boolean = false) {
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
                    val pre = String.format("%d%09d", type.ordinal, data.size)
                    write(pre.toByteArray(Charsets.US_ASCII))
                    write(0)
                    write(data)
                }
                data is String -> {
                    when (type) {
                        FileType.String -> {
                            val byteData = data.toByteArray(Charsets.UTF_8)

                            val pre = String.format("%d%09d", type.ordinal, byteData.size)
                            write(pre.toByteArray(Charsets.US_ASCII))
                            write(0)
                            write(byteData)
                        }
                        else -> {
                            val file = File(data)
                            if (file.exists() && file.isFile) {
                                // 文件名称及长度
                                var fileName = file.name
                                var fileNameLength = fileName.toByteArray(Charsets.UTF_8).size
                                while (fileNameLength > Byte.MAX_VALUE) {
                                    fileName = fileName.substring(1)

                                    fileNameLength = fileName.toByteArray(Charsets.UTF_8).size
                                }

                                // 头部信息
                                val pre = String.format("%d%09d", type.ordinal, file.length().toInt())
                                write(pre.toByteArray(Charsets.US_ASCII))
                                write(fileNameLength)
                                write(fileName.toByteArray(Charsets.UTF_8))

                                // 发送文件
                                val fileInputStream = FileInputStream(file)

                                val readData = ByteArray(1024)
                                var readLenght = fileInputStream.read(readData)
                                while (readLenght > -1) {
                                    write(readData, 0, readLenght)
                                    readLenght = fileInputStream.read(readData)
                                }
                            }
                        }
                    }
                }
            }
        }

        sockets.removeAll { it.isClosed || !it.isConnected || it.isOutputShutdown }
    }

    enum class FileType {
        String, Image, File
    }
}