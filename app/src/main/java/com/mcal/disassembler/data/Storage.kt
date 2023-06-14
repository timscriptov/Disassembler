package com.mcal.disassembler.data

import android.content.Context
import java.io.File

class Storage(private val context: Context) {
    fun getHomeDir(): File {
        return context.getExternalFilesDir(null) as File
    }

    fun getDisassemblerDir(): File {
        val dir = File(getHomeDir(), "Disassembler")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getHeadersDir(): File {
        val dir = File(getDisassemblerDir(), "headers")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getSymbolsDir(): File {
        val dir = File(getDisassemblerDir(), "symbols")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getVTablesDir(): File {
        val dir = File(getDisassemblerDir(), "vtables")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}
