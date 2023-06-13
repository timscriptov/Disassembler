package com.mcal.disassembler.interfaces

interface DialogProgressListener {
    fun updateDialogProgress(last: Int, total: Int)
}