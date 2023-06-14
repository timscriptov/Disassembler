package com.mcal.disassembler.utils

import java.io.File
import java.io.FileOutputStream
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

    @JvmStatic
    fun writeSymbolsToFile(dir: File, name: String, symbolsList: Array<String?>) {
        try {
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val writer = FileOutputStream(File(dir, name))
            var item: String?
            for (i in symbolsList.indices) {
                item = symbolsList[i]
                if (item != null) {
                    writer.write("$item\n".toByteArray())
                }
            }
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun writeSymbolsToFile(path: String, name: String, symbolsList: Array<String?>) {
        writeSymbolsToFile(File(path), name, symbolsList)
    }
}