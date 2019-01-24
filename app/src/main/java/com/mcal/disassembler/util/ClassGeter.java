package com.mcal.disassembler.util;

import com.mcal.disassembler.nativeapi.DisassemblerClass;
import com.mcal.disassembler.nativeapi.Dumper;
import com.mcal.disassembler.nativeapi.DisassemblerSymbol;

public class ClassGeter
{
	public static DisassemblerClass getClass(String name)
	{
		for(DisassemblerClass clasz:Dumper.classes)
			if(clasz.getName()==name)
				return clasz;
		DisassemblerClass clasz=new DisassemblerClass(name);
		for(DisassemblerSymbol symbol:Dumper.symbols)
			if(hasClass(symbol.getDemangledName()))
				if(getClassName(symbol.getDemangledName()).equals(name))
					clasz.addNewSymbol(symbol);
		if(clasz.getSymbols().isEmpty())
			return null;
		Dumper.classes.addElement(clasz);
		return clasz;
	}
	
	public static boolean hasClass(String name)
	{
		String symbolMainName=new String();
		if (name.indexOf("(") != -1)
			symbolMainName = name.substring(0, name.indexOf("("));
		else
			symbolMainName = name;

		if (symbolMainName.lastIndexOf("::") != -1)
			return true;
		else if (symbolMainName.startsWith("vtable"))
			return true;
		return false;
	}

	public static String getClassName(String demangledName)
	{
		String symbolMainName=new String();
		if (demangledName.indexOf("(") != -1)
			symbolMainName = demangledName.substring(0, demangledName.indexOf("("));
		else
			symbolMainName = demangledName;

		if (symbolMainName.lastIndexOf("::") != -1)
			return symbolMainName.substring(0, symbolMainName.lastIndexOf("::"));
		else if (symbolMainName.startsWith("vtable"))
			return symbolMainName.substring(symbolMainName.lastIndexOf(" ") + 1, symbolMainName.length());
		else
			return "NULL";
	}
}
