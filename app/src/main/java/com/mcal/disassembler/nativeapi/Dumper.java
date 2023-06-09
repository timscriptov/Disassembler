package com.mcal.disassembler.nativeapi;

import java.util.Vector;

public class Dumper {
    public static Vector<DisassemblerSymbol> symbols = new Vector<>();
    public static Vector<DisassemblerVtable> exploed = new Vector<>();
    public static Vector<DisassemblerClass> classes = new Vector<>();

    public static void readData(DumperListener listener) {
        symbols.clear();
        exploed.clear();
        classes.clear();
        final long size = DisassemblerDumper.getSize();
        for (int i = 0; i < size; ++i) {
            listener.updateProgress(i, (int) size);
            String demangledName = DisassemblerDumper.getDemangledNameAt(i);
            if (demangledName == null || demangledName.isEmpty() || demangledName.equals(" ")) {
                demangledName = DisassemblerDumper.getNameAt(i);
            }
            DisassemblerSymbol newSymbol = new DisassemblerSymbol(DisassemblerDumper.getNameAt(i), demangledName, DisassemblerDumper.getTypeAt(i), DisassemblerDumper.getBindAt(i));
            symbols.addElement(newSymbol);
        }
    }

    public interface DumperListener {
        void updateProgress(int last, int total);
    }
}