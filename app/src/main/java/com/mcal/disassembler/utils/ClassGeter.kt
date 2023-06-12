package com.mcal.disassembler.utils

import com.mcal.disassembler.nativeapi.DisassemblerClass
import com.mcal.disassembler.nativeapi.Dumper

object ClassGeter {
    fun getClass(name: String): DisassemblerClass? {
        for (clazz in Dumper.classes) {
            if (clazz.name == name) {
                return clazz
            }
        }
        val clazz = DisassemblerClass(name)
        for (symbol in Dumper.symbols) {
            if (hasClass(symbol.demangledName)) {
                if (getClassName(symbol.demangledName) == name) {
                    clazz.addNewSymbol(symbol)
                }
            }
        }
        if (clazz.symbols.isEmpty()) {
            return null
        }
        Dumper.classes.addElement(clazz)
        return clazz
    }

    private fun hasClass(name: String): Boolean {
        val symbolMainName = if (name.contains("(")) {
            name.substring(0, name.indexOf("("))
        } else {
            name
        }
        return if (symbolMainName.lastIndexOf("::") != -1) {
            true
        } else {
            symbolMainName.startsWith("vtable")
        }
    }

    private fun getClassName(demangledName: String): String {
        val symbolMainName = if (demangledName.contains("(")) {
            demangledName.substring(0, demangledName.indexOf("("))
        } else {
            demangledName
        }
        return if (symbolMainName.lastIndexOf("::") != -1) {
            symbolMainName.substring(0, symbolMainName.lastIndexOf("::"))
        } else if (symbolMainName.startsWith("vtable")) {
            symbolMainName.substring(symbolMainName.lastIndexOf(" ") + 1)
        } else {
            "NULL"
        }
    }
}