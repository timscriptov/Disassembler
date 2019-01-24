package com.mcal.disassembler.util;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSaver
{
	private Context context;
	private String name;
	private String[] file;
	private String path;
	public FileSaver(Context context,String path,String name,String[] file)
	{
		this.context=context;
		this.name=name;
		this.file=file;
		this.path=path;
	}
	
	public void save()
	{
		File file_=new File(path);
		try
		{
			file_.mkdirs();
			File file__=new File(file_,name);
			file__.createNewFile();
			FileOutputStream writer=new FileOutputStream(file__);
			for(String str:file)
			{
				writer.write(str.getBytes());
				writer.write(new String("\n").getBytes());
			}
			writer.close();
		}
		catch (IOException e)
		{
			
		}
	}
}
