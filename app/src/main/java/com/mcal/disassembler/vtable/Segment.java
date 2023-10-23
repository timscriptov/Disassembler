package com.mcal.disassembler.vtable;

import java.util.Vector;

public class Segment {
    public int type;
    public int flags;
    public Vector<Section> sections = new Vector<>();
    int offset;
    int vaddr;
    int paddr;
    int filesz;
    int memsz;
    int align;
}
