package com.kiven.sample.network.socket

data class Message(val fromIp: String, val localPort: Int, val dataType: SocketFactory.DataType, val data: Any, val fileName: String? = null)
