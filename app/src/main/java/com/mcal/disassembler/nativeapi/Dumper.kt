package com.mcal.disassembler.nativeapi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Vector

object Dumper {
    @JvmField
    var symbols = Vector<DisassemblerSymbol>()

    @JvmField
    var exploed = Vector<DisassemblerVtable>()

    @JvmField
    var classes = Vector<DisassemblerClass>()
    suspend fun readData(listener: DumperListener) {
        symbols.clear()
        exploed.clear()
        classes.clear()
        val size = DisassemblerDumper.getSize()
        for (i in 0 until size) {
            withContext(Dispatchers.Main) {
                listener.updateDialogProgress(i.toInt(), size.toInt())
            }
            var demangledName = DisassemblerDumper.getDemangledNameAt(i)
            if (demangledName == null || demangledName.isEmpty() || demangledName == " ") {
                demangledName = DisassemblerDumper.getNameAt(i)
            }
            val newSymbol = DisassemblerSymbol(
                DisassemblerDumper.getNameAt(i), demangledName, DisassemblerDumper.getTypeAt(
                    i
                ), DisassemblerDumper.getBindAt(i)
            )
            symbols.addElement(newSymbol)
        }
    }

    interface DumperListener {
        fun updateDialogProgress(last: Int, total: Int)
    }
}