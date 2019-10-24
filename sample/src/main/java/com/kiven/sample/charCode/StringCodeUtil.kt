package com.kiven.sample.charCode

import java.nio.charset.Charset
import kotlin.experimental.and

object StringCodeUtil {

    fun hexStr2Str(hexStr: String, charset: Charset = Charsets.UTF_8): String {
        if (hexStr.isBlank())
            return ""

        val str = "0123456789abcdef"

        val hexs = hexStr.toCharArray()
        val bytes = ByteArray(hexs.size / 2)

        for (i in bytes.indices) {
            var n = str.indexOf(hexs[2 * i]) * 16
            n += str.indexOf(hexs[2 * i + 1])

            bytes[i] = (n and 0xff).toByte()
        }

        return String(bytes, charset)
    }

    fun str2HexStr(str: String, charset: Charset = Charsets.UTF_8): String {
        val chars = "0123456789abcdef".toCharArray()

        val sb = StringBuilder()
        val bs = str.toByteArray(charset)

        for (i in bs.indices) {
            val bit = (bs[i].toLong() and 0x0f0) shr 4
            sb.append(chars[bit.toInt()])

            val bit2 = bs[i] and 0x0f
            sb.append(chars[bit2.toInt()])
        }

        return sb.toString().trim()
    }
}