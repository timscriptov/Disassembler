package com.mcal.disassembler.util;

import com.mcal.disassembler.nativeapi.DisassemblerClass;
import com.mcal.disassembler.nativeapi.DisassemblerSymbol;
import com.mcal.disassembler.nativeapi.Dumper;

public class ClassGeter {
    public static DisassemblerClass getClass(String name) {
        for (DisassemblerClass clasz : Dumper.classes)
            if (clasz.getName().equals(name))
                return clasz;
        DisassemblerClass clasz = new DisassemblerClass(name);
        for (DisassemblerSymbol symbol : Dumper.symbols)
            if (hasClass(symbol.getDemangledName()))
                if (getClassName(symbol.getDemangledName()).equals(name))
                    clasz.addNewSymbol(symbol);
        if (clasz.getSymbols().isEmpty())
            return null;
        Dumper.classes.addElement(clasz);
        return clasz;
    }

    private static boolean hasClass(String name) {
        String symbolMainName;
        if (name.contains("("))
            symbolMainName = name.substring(0, name.indexOf("("));
        else
            symbolMainName = name;

        if (symbolMainName.lastIndexOf("::") != -1)
            return true;
        else return symbolMainName.startsWith("vtable");
    }

    private static String getClassName(String demangledName) {
        String symbolMainName;
        if (demangledName.contains("("))
            symbolMainName = demangledName.substring(0, demangledName.indexOf("("));
        else
            symbolMainName = demangledName;

        if (symbolMainName.lastIndexOf("::") != -1)
            return symbolMainName.substring(0, symbolMainName.lastIndexOf("::"));
        else if (symbolMainName.startsWith("vtable"))
            return symbolMainName.substring(symbolMainName.lastIndexOf(" ") + 1);
        else
            return "NULL";
    }
}