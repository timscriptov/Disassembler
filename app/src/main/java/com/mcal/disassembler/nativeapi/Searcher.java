package com.mcal.disassembler.nativeapi;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher {
    public static Vector<DisassemblerSymbol> search(String key) {
        Vector<DisassemblerSymbol> returnValue = new Vector<DisassemblerSymbol>();
        if (key == null || key.isEmpty() || key.equals(" "))
            return returnValue;

        for (DisassemblerSymbol symbol : Dumper.symbols) {
            if (symbol.getDemangledName() != null && symbol.getDemangledName().contains(key)) {
                returnValue.addElement(symbol);
            }
        }
        return returnValue;
    }

    public static Vector<DisassemblerSymbol> searchWithPattern(String role) {
        Vector<DisassemblerSymbol> returnValue = new Vector<DisassemblerSymbol>();
        try {
            if (role == null || role.isEmpty() || role.equals(" "))
                return returnValue;
            Pattern p = Pattern.compile(role);

            for (DisassemblerSymbol symbol : Dumper.symbols) {
                if (symbol.getDemangledName() != null) {
                    Matcher m = p.matcher(symbol.getDemangledName());
                    if (m.find())
                        returnValue.addElement(symbol);
                }
            }
        } catch (Exception e) {
            return returnValue;
        }
        return returnValue;
    }
}