package com.mcal.disassembler.nativeapi;

import java.util.Vector;

public class DisassemblerClass
{
	String name;
	Vector<DisassemblerSymbol> symbols;

	public DisassemblerClass(String name, Vector<DisassemblerSymbol> symbols)
	{
		this.name = name;
		this.symbols = symbols;
	}

	public DisassemblerClass(String name)
	{
		this.name = name;
		this.symbols = new Vector<>();
	}


	public void setName(String name)
	{
		this.name = name;
	}

	public void addNewSymbol(DisassemblerSymbol sym)
	{
		symbols.addElement(sym);
	}

	public String getName()
	{
		return name;
	}

	public void setSymbols(Vector<DisassemblerSymbol> symbols)
	{
		this.symbols = symbols;
	}

	public Vector<DisassemblerSymbol> getSymbols()
	{
		return symbols;
	}
}
