package com.mcal.disassembler.nativeapi;

public class DisassemblerDumper
{
	public static native void load(String path);
	public static native boolean hasFile(String path);
	public static native String getNameAt(long pos);
	public static native String getDemangledNameAt(long pos);
	public static native int getTypeAt(long pos);
	public static native int getBindAt(long pos);
	public static native long getSize();
	public static native String demangle(String name);
	public static native String demangleOnly(String name);
}
