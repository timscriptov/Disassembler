package com.mcal.disassembler.vtable;

import java.util.Vector;

public class Elf {
    public Vector<Section> sections = new Vector<>();
    Header hdr = new Header();
    Vector<Segment> segments = new Vector<>();
}
