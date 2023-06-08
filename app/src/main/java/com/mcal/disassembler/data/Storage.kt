package com.mcal.disassembler.data

import android.content.Context
import java.io.File

object Storage {
    @JvmStatic
    fun getHomeDir(context: Context): File {
        return context.getExternalFilesDir(null) as File
    }
}
