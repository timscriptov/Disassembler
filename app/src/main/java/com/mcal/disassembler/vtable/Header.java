package com.mcal.disassembler.vtable;

public class Header {
    public int type;
    public int version;
    public int flags;
    byte[] ident = new byte[16];
    int machine;
    int entry;
    int phoff;
    int shoff;
    int ehsize;
    int phentsize;
    int phnum;
    int shentsize;
    int shnum;
    int shstrndx;
}
