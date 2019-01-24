package com.mcal.disassembler.vtable;

import java.util.Vector;

public class Elf
{
	public header hdr=new header();
	public Vector<segment> segments = new Vector<segment>();
	public Vector<section> sections = new Vector<section>();

	public Elf()
	{
	}
}

class relocation
{
	public int offset;
	public int info;
}
class symbol
{
	public String name;
	public int value;
	public int size;
	public byte other;
	public int shndx ;
	public int bind;
	public int type;
}
class header
{
	public byte[] ident = new byte[16];
	public int type ;
	public int machine ;
	public int version;
	public int entry;
	public int phoff;
	public int shoff;
	public int flags;
	public int ehsize;
	public int phentsize;
	public int phnum;
	public int shentsize;
	public int shnum;
	public int shstrndx;
}
class segment
{
	public int type;
	public int offset;
	public int vaddr;
	public int paddr;
	public int filesz;
	public int memsz;
	public int flags;
	public int align;
	public Vector<section>sections=new Vector<section>();
}
class section
{
	public String name;
	public int type;
	public int flags;
	public int addr;
	public int offset;
	public int size;
	public int link;
	public int info;
	public int addralign;
	public int entsize;
}
