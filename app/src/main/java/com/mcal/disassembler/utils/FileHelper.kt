package com.mcal.disassembler.utils

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FileHelper {
    @JvmStatic
    @Throws(IOException::class)
    fun copyFile(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } != -1) {
            target.write(buf, 0, length)
        }
    }
}