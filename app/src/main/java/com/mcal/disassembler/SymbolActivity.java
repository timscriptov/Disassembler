package com.mcal.disassembler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mcal.disassembler.nativeapi.DisassemblerVtable;
import com.mcal.disassembler.nativeapi.Dumper;
import com.mcal.disassembler.vtable.Tables;
import com.mcal.disassembler.vtable.VtableDumper;

public class SymbolActivity extends AppCompatActivity
{
	private String path;
	private String name;
	private int type;
	private String demangledName;
	private String className;
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


		setContentView(R.layout.symbol_activity);
		type = getIntent().getExtras().getInt("type");
		name = getIntent().getExtras().getString("name");
		demangledName = getIntent().getExtras().getString("demangledName");
		path = getIntent().getExtras().getString("filePath");

		ImageView imageTitile= findViewById(R.id.symbolactivityImageView);
		if (type == 1)
			imageTitile.setImageResource(R.drawable.box_blue);
		else if (type == 2)
			imageTitile.setImageResource(R.drawable.box_red);
		else
			imageTitile.setImageResource(R.drawable.box_pink);

		TextView textName= findViewById(R.id.symbolactivityTextViewName);
		textName.setText(name);

		TextView textDemangledName= findViewById(R.id.symbolactivityTextViewDemangledName);
		textDemangledName.setText(demangledName);

		String arguments=new String();
		if (demangledName.indexOf("(") != -1 && demangledName.lastIndexOf(")") != -1)
			arguments = demangledName.substring(demangledName.indexOf("(") + 1, demangledName.lastIndexOf(")"));
		else
			arguments = "NULL";
		TextView textArguments=(TextView)findViewById(R.id.symbolactivityTextViewArguments);
		textArguments.setText(arguments);

		String symbolMainName=new String();
		if (demangledName.indexOf("(") != -1)
			symbolMainName = demangledName.substring(0, demangledName.indexOf("("));
		else
			symbolMainName = demangledName;
		className = new String();
		if (symbolMainName.lastIndexOf("::") != -1)
			className = symbolMainName.substring(0, symbolMainName.lastIndexOf("::"));
		else if (symbolMainName.startsWith("vtable"))
			className = symbolMainName.substring(symbolMainName.lastIndexOf(" ") + 1, symbolMainName.length());
		else
			className = "NULL";
		TextView textClassName=(TextView)findViewById(R.id.symbolactivityTextClass);
		textClassName.setText(className);

		String symbolName=new String();
		if (symbolMainName.lastIndexOf("::") != -1)
			symbolName = symbolMainName.substring(symbolMainName.lastIndexOf("::") + 2, symbolMainName.length());
		else
			symbolName = symbolMainName;

		TextView textSymbolName=(TextView)findViewById(R.id.symbolactivityTextViewSymbolMainName);
		textSymbolName.setText(symbolName);

		String typeName=Tables.symbol_type.get(type);

		TextView textTypeName=(TextView)findViewById(R.id.symbolactivityTextViewType);
		textTypeName.setText(typeName);

		if (name.startsWith("_ZTV"))
		{
			findViewById(R.id.symbolactivityButtonFloat).setVisibility(View.VISIBLE);
			findViewById(R.id.symbolactivityTextViewButtonFloat).setVisibility(View.VISIBLE);
		}

		if (className != "NULL")
		{
			findViewById(R.id.symbolactivityButtonFloatClass).setVisibility(View.VISIBLE);
			findViewById(R.id.symbolactivityTextViewButtonFloatClass).setVisibility(View.VISIBLE);
		}
	}

	public void toVtableActivity(View view)
	{
		showProgressDialog();
		new Thread()
		{
			public void run()
			{
				DisassemblerVtable vtable=VtableDumper.dump(SymbolActivity.this.path, SymbolActivity.this.name);
				if (vtable != null)
					SymbolActivity.this.toVtableActivity_(vtable);
				dismissProgressDialog();
			}
		}.start();
	}

	public void toClassActivity(View view)
	{
		showProgressDialog();
		new Thread()
		{
			public void run()
			{
				DisassemblerVtable vtable=VtableDumper.dump(SymbolActivity.this.path, SymbolActivity.this.name);
				if (vtable != null)
					SymbolActivity.this.toClassActivity_(vtable);
				dismissProgressDialog();
			}
		}.start();
	}

	public void toClassActivity_(DisassemblerVtable vtable)
	{
		if (className == null || className == "" || className == " " || className.isEmpty() || className == "NULL")
			return;
		Bundle bundle=new Bundle();
		bundle.putString("name", className);
		bundle.putString("path", path);
		Intent intent=new Intent(this, ClassActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void toVtableActivity_(DisassemblerVtable vtable)
	{
		Bundle bundle=new Bundle();
		bundle.putString("name", name);
		bundle.putString("path", path);
		Dumper.exploed.addElement(vtable);
		Intent intent=new Intent(this, VtableActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	com.gc.materialdesign.widgets.ProgressDialog dialog;

	public void showProgressDialog()
	{
		dialog = new com.gc.materialdesign.widgets.ProgressDialog(this, getString(R.string.loading));
		dialog.show();
	}
	public void dismissProgressDialog()
	{
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}
}
