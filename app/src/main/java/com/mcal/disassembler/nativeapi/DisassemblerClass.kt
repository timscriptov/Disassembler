package com.mcal.disassembler.nativeapi

import java.util.Vector

class DisassemblerClass {
    var name: String
    var symbols: Vector<DisassemblerSymbol>

    constructor(name: String, symbols: Vector<DisassemblerSymbol>) {
        this.name = name
        this.symbols = symbols
    }

    constructor(name: String) {
        this.name = name
        symbols = Vector()
    }

    fun addNewSymbol(sym: DisassemblerSymbol) {
        symbols.addElement(sym)
    }
}