package com.mcal.disassembler.vtable;

import java.util.Vector;

public class Elf {
    public Vector<section> sections = new Vector<>();
    header hdr = new header();
    Vector<segment> segments = new Vector<>();

    Elf() {
    }
}

class relocation {
    public int info;
    int offset;
}

class symbol {
    public String name;
    public int value;
    public int size;
    public byte other;
    public int type;
    int shndx;
    int bind;
}

class header {
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

class segment {
    public int type;
    public int flags;
    public Vector<section> sections = new Vector<>();
    int offset;
    int vaddr;
    int paddr;
    int filesz;
    int memsz;
    int align;
}

class section {
    public String name;
    public int type;
    public int flags;
    public int size;
    public int info;
    int addr;
    int offset;
    int link;
    int addralign;
    int entsize;
}
