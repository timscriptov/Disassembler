package com.mcal.disassembler.vtable;

import java.util.Vector;

public class Elf {
    header hdr = new header();
    Vector<segment> segments = new Vector<>();
    public Vector<section> sections = new Vector<>();

    Elf() {
    }
}

class relocation {
    int offset;
    public int info;
}

class symbol {
    public String name;
    public int value;
    public int size;
    public byte other;
    int shndx;
    int bind;
    public int type;
}

class header {
    byte[] ident = new byte[16];
    public int type;
    int machine;
    public int version;
    int entry;
    int phoff;
    int shoff;
    public int flags;
    int ehsize;
    int phentsize;
    int phnum;
    int shentsize;
    int shnum;
    int shstrndx;
}

class segment {
    public int type;
    int offset;
    int vaddr;
    int paddr;
    int filesz;
    int memsz;
    public int flags;
    int align;
    public Vector<section> sections = new Vector<>();
}

class section {
    public String name;
    public int type;
    public int flags;
    int addr;
    int offset;
    public int size;
    int link;
    public int info;
    int addralign;
    int entsize;
}