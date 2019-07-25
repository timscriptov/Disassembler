package com.mcal.disassembler.nativeapi;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher
{
	public static Vector<DisassemblerSymbol> search(String key)
	{
		Vector<DisassemblerSymbol> returnValue=new Vector<DisassemblerSymbol>();
		if (key == null || key.isEmpty() || key == "" || key == " ")
			return returnValue;

		for (DisassemblerSymbol symbol:Dumper.symbols)
		{
			if (symbol.getDemangledName() != null && symbol.getDemangledName().indexOf(key) != -1)
			{
				returnValue.addElement(symbol);
			}
		}

		return returnValue;
	}

	public static Vector<DisassemblerSymbol> searchWithPattern(String role)
	{
		Vector<DisassemblerSymbol> returnValue=new Vector<DisassemblerSymbol>();
		try
		{
			if (role == null || role.isEmpty() || role == "" || role == " ")
				return returnValue;
			Pattern p = Pattern.compile(role);

			for (DisassemblerSymbol symbol:Dumper.symbols)
			{
				if (symbol.getDemangledName() != null)
				{
					Matcher m = p.matcher(symbol.getDemangledName());
					if (m.find())
						returnValue.addElement(symbol);
				}
			}
		}
		catch (Exception e)
		{
			return returnValue;
		}
		return returnValue;
	}
}
