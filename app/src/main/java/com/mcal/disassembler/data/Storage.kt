package com.mcal.disassembler.data

import android.content.Context
import java.io.File

object Storage {
    @JvmStatic
    fun getHomeDir(context: Context): File {
        return context.getExternalFilesDir(null) as File
    }

    @JvmStatic
    fun getHeadersDir(context: Context): File {
        val dir = File(getHomeDir(context).path + "/Disassembler/headers/")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    @JvmStatic
    fun getSymbolsDir(context: Context): File {
        val dir = File(getHomeDir(context).path + "/Disassembler/symbols/")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    @JvmStatic
    fun getVTablesDir(context: Context): File {
        val dir = File(getHomeDir(context).path + "/Disassembler/vtables/")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}
