package com.mcal.disassembler.nativeapi;

public class DisassemblerSymbol {
    private String name;
    private String demangledName;
    private int type;

    public DisassemblerSymbol(String name, String demangledName, int type, int bind) {
        this.type = type;
        this.demangledName = demangledName;
        this.name = name;
    }

    public String getDemangledName() {
        return demangledName;
    }

    public void setDemangledName(String demangledName) {
        this.demangledName = demangledName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBind() {
        return type;
    }

    public void setBind(int type) {
        this.type = type;
    }
}