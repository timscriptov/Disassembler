package com.mcal.disassembler.nativeapi;

import java.util.Vector;

public class DisassemblerVtable
{
	private String name=new String();
	private Vector<DisassemblerSymbol> vtables=new Vector<DisassemblerSymbol>();
	
	public DisassemblerVtable(String name, Vector<DisassemblerSymbol> vtables)
	{
		this.name = name;
		this.vtables = vtables;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setVtables(Vector<DisassemblerSymbol> vtables)
	{
		this.vtables = vtables;
	}

	public Vector<DisassemblerSymbol> getVtables()
	{
		return vtables;
	}
}
