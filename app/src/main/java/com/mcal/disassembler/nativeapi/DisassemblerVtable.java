package com.mcal.disassembler.nativeapi;

import java.util.Vector;

public class DisassemblerVtable {
    private String name;
    private Vector<DisassemblerSymbol> vtables;

    public DisassemblerVtable(String name, Vector<DisassemblerSymbol> vtables) {
        this.name = name;
        this.vtables = vtables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector<DisassemblerSymbol> getVtables() {
        return vtables;
    }

    public void setVtables(Vector<DisassemblerSymbol> vtables) {
        this.vtables = vtables;
    }
}