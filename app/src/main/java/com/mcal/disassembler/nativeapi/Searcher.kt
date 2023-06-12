package com.mcal.disassembler.nativeapi

import java.util.Vector
import java.util.regex.Matcher
import java.util.regex.Pattern

object Searcher {
    @JvmStatic
    fun search(key: String?): Vector<DisassemblerSymbol> {
        val returnValue = Vector<DisassemblerSymbol>()
        if (key.isNullOrEmpty() || key == " ") {
            return returnValue
        }
        var name: String?
        for (symbol in Dumper.symbols) {
            name = symbol.demangledName
            if (name != null && name.contains(key)) {
                returnValue.addElement(symbol)
            }
        }
        return returnValue
    }

    fun searchWithPattern(role: String?): Vector<DisassemblerSymbol> {
        val returnValue = Vector<DisassemblerSymbol>()
        try {
            if (role.isNullOrEmpty() || role == " ") {
                return returnValue
            }
            val pattern = Pattern.compile(role)
            var name: String?
            var matcher: Matcher?
            for (symbol in Dumper.symbols) {
                name = symbol.demangledName
                if (name != null) {
                    matcher = pattern.matcher(name)
                    if (matcher.find()) {
                        returnValue.addElement(symbol)
                    }
                }
            }
        } catch (e: Exception) {
            return returnValue
        }
        return returnValue
    }
}
