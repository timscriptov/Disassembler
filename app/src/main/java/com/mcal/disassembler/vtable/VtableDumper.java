package com.mcal.disassembler.vtable;

import android.annotation.SuppressLint;

import com.mcal.disassembler.nativeapi.DisassemblerSymbol;
import com.mcal.disassembler.nativeapi.DisassemblerVtable;
import com.mcal.disassembler.nativeapi.Dumper;

import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class VtableDumper {
    public static DisassemblerVtable dump(String path, String classn) {
        for (DisassemblerVtable ztv : Dumper.exploed)
            if (ztv.getName().contains(classn))
                return ztv;

        Dump d = new Dump(path);

        symbol sym = null;
        section symsec = null;

        for (section sec : d.elf.sections) {
            if (sec.type == 2 || sec.type == 11) {
                for (int i = 0; i < d.getSymNum(sec); ++i) {
                    symbol sym_ = d.getSym(sec, i);
                    if (sym_.name.equals(classn)) {
                        sym = sym_;
                        symsec = sec;
                        break;
                    }
                }
            }
        }

        if (sym == null)
            return null;

        @SuppressLint("UseSparseArrays") HashMap<Integer, symbol> map = new HashMap<>();//为了排序
        int c = 0;

        for (section sec : d.elf.sections) {
            if (sec.name.equals(".rel.dyn")) {
                for (int i = 0; i < d.getRelNum(sec); ++i) {
                    relocation rel = d.getRel(sec, i);
                    for (int j = 0; j < sym.size / 4 - 2; ++j) {
                        if (sym.value + 8 + j * 4 == rel.offset) {
                            ++c;
                            symbol vsym = d.getSym(symsec, rel.info >> 8);

                            map.put(rel.offset, vsym);
                        }
                    }
                    if (map.size() == sym.size / 4 - 2) {
                        break;
                    }
                }
            }
        }

        Vector<DisassemblerSymbol> virtual_table_symbols = new Vector<DisassemblerSymbol>();

        for (int j = 0; j < sym.size / 4 - 2; ++j) {
            if (map.get(sym.value + 8 + j * 4) != null)
                if (getSymbol(Objects.requireNonNull(map.get(sym.value + 8 + j * 4)).name) != null)
                    virtual_table_symbols.addElement(getSymbol(Objects.requireNonNull(map.get(sym.value + 8 + j * 4)).name));
        }
        DisassemblerVtable vtable__ = new DisassemblerVtable(classn, virtual_table_symbols);
        Dumper.exploed.addElement(vtable__);
        return vtable__;
    }

    private static DisassemblerSymbol getSymbol(String name) {
        for (DisassemblerSymbol symbol : Dumper.symbols)
            if (symbol.getName().equals(name))
                return symbol;
        return null;
    }
}