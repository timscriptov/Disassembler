package com.mcal.disassembler.vtable;

import android.annotation.SuppressLint;

import java.util.HashMap;

public class Tables {
    @SuppressLint("UseSparseArrays")
    private static final HashMap<Integer, String> section_type = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private static final HashMap<Integer, String> segment_type = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private static final HashMap<Integer, String> segment_flag = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private static final HashMap<Integer, String> symbol_bind = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private static final HashMap<Integer, String> dynamic_tag = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, String> version = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, String> type = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, String> symbol_type = new HashMap<>();

    static {
        version.put(0, "None");
        version.put(1, "Current");

        type.put(0, "No file type");
        type.put(1, "Relocatable file");
        type.put(2, "Executable file");
        type.put(3, "Shared object file");
        type.put(4, "Core file");

        section_type.put(0, "NULL");
        section_type.put(1, "PROGBITS");
        section_type.put(2, "SYMTAB");
        section_type.put(3, "STRTAB");
        section_type.put(4, "RELA");
        section_type.put(5, "HASH");
        section_type.put(6, "DYNAMIC");
        section_type.put(7, "NOTE");
        section_type.put(8, "NOBITS");
        section_type.put(9, "REL");
        section_type.put(10, "SHLIB");
        section_type.put(11, "DYNSYM");
        section_type.put(14, "INIT_ARRAY");
        section_type.put(15, "FINI_ARRAY");
        section_type.put(16, "PREINIT_ARRAY");
        section_type.put(17, "GROUP");
        section_type.put(18, "SYMTAB_SHNDX");
        section_type.put(1879048193, "ARM_EXIDX");
        section_type.put(1879048195, "ARM_ATTRIBUTES");

        segment_type.put(0, "NULL");
        segment_type.put(1, "LOAD");
        segment_type.put(2, "DYNAMIC");
        segment_type.put(3, "INTERP");
        segment_type.put(4, "NOTE");
        segment_type.put(5, "SHLIB");
        segment_type.put(6, "PHDR");
        segment_type.put(7, "TLS");

        segment_flag.put(0, "");
        segment_flag.put(1, "X");
        segment_flag.put(2, "W");
        segment_flag.put(3, "WX");
        segment_flag.put(4, "R");
        segment_flag.put(5, "RX");
        segment_flag.put(6, "RW");
        segment_flag.put(7, "RWX");

        symbol_bind.put(0, "LOCAL");
        symbol_bind.put(1, "GLOBAL");
        symbol_bind.put(2, "WEAK");
        symbol_bind.put(10, "LOOS");
        symbol_bind.put(12, "HIOS");
        symbol_bind.put(13, "LOPROC");
        symbol_bind.put(15, "HIPROC");

        symbol_type.put(0, "NOTYPE");
        symbol_type.put(1, "OBJECT");
        symbol_type.put(2, "FUNC");
        symbol_type.put(3, "SECTION");
        symbol_type.put(4, "FILE");
        symbol_type.put(5, "COMMON");
        symbol_type.put(6, "TLS");
        symbol_type.put(10, "LOOS");
        symbol_type.put(12, "HIOS");
        symbol_type.put(13, "LOPROC");
        symbol_type.put(15, "HIPROC");

        dynamic_tag.put(0, "NULL");
        dynamic_tag.put(1, "NEEDED");
        dynamic_tag.put(2, "PLTRELSZ");
        dynamic_tag.put(3, "PLTGOT");
        dynamic_tag.put(4, "HASH");
        dynamic_tag.put(5, "STRTAB");
        dynamic_tag.put(6, "SYMTAB");
        dynamic_tag.put(7, "RELA");
        dynamic_tag.put(8, "RELASZ");
        dynamic_tag.put(9, "RELAENT");
        dynamic_tag.put(10, "STRSZ");
        dynamic_tag.put(11, "SYMENT");
        dynamic_tag.put(12, "INIT");
        dynamic_tag.put(13, "FINI");
        dynamic_tag.put(14, "SONAME");
        dynamic_tag.put(15, "RPATH");
        dynamic_tag.put(16, "SYMBOLIC");
        dynamic_tag.put(17, "REL");
        dynamic_tag.put(18, "RELSZ");
        dynamic_tag.put(19, "RELENT");
        dynamic_tag.put(20, "PLTREL");
        dynamic_tag.put(21, "DEBUG");
        dynamic_tag.put(22, "TEXTREL");
        dynamic_tag.put(23, "JMPREL");
        dynamic_tag.put(24, "BIND_NOW");
        dynamic_tag.put(25, "INIT_ARRAY");
        dynamic_tag.put(26, "FINI_ARRAY");
        dynamic_tag.put(27, "INIT_ARRAYSZ");
        dynamic_tag.put(28, "FINI_ARRAYSZ");
        dynamic_tag.put(29, "RUNPATH");
        dynamic_tag.put(30, "FLAGS");
        dynamic_tag.put(31, "ENCODING");
        dynamic_tag.put(32, "PREINIT_ARRAY");
        dynamic_tag.put(33, "PREINIT_ARRAYSZ");
        dynamic_tag.put(34, "MAXPOSTAGS");
    }
}
