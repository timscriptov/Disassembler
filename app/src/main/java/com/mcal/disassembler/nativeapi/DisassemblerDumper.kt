package com.mcal.disassembler.nativeapi

object DisassemblerDumper {
    external fun load(path: String)
    external fun hasFile(path: String): Boolean
    external fun getNameAt(pos: Long): String
    external fun getDemangledNameAt(pos: Long): String?
    external fun getTypeAt(pos: Long): Int
    external fun getBindAt(pos: Long): Int
    external fun getSize(): Long
    external fun demangle(name: String): String
    external fun demangleOnly(name: String): String
}