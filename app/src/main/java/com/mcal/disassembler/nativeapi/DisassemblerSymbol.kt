package com.mcal.disassembler.nativeapi

class DisassemblerSymbol(
    var name: String,
    var demangledName: String,
    var type: Int,
    var bind: Int
)