package com.mcal.disassembler.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

object StringHelper {
    fun readFromClipboard(context: Context): String {
        return (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip?.getItemAt(
            0
        )?.text.toString()
    }

    fun copyToClipboard(copiedText: String, context: Context) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
            ClipData.newPlainText("text", copiedText)
        )
        Toast.makeText(context, copiedText, Toast.LENGTH_LONG).show()
    }
}