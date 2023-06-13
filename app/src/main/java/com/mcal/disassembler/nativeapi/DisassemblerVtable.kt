package com.mcal.disassembler.nativeapi

import java.util.Vector

class DisassemblerVtable(
    var name: String,
    var vtables: Vector<DisassemblerSymbol>
)