package com.mcal.disassembler;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Handler;
import android.view.View;

import com.gc.materialdesign.widgets.SnackBar;

import com.mcal.disassembler.nativeapi.Dumper;
import com.mcal.disassembler.util.FileSaver;
import android.support.v7.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity
{
	private String path;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_activity);

		path = getIntent().getExtras().getString("filePath");
	}

	public void toNameDemangler(View view)
	{
		startActivity(new Intent(this, NameDemanglerActivity.class));
	}

	public void showFloatingMenu(View view)
	{
		new FloatingButton(this, path).show();
	}

	private void _saveSymbols()
	{
		String [] strings=new String[Dumper.symbols.size()];
		for (int i=0;i < Dumper.symbols.size();++i)
			strings[i] = Dumper.symbols.get(i).getName();

		FileSaver saver=new FileSaver(this, Environment.getExternalStorageDirectory().toString() + "/Disassembler/symbols/", "Symbols.txt", strings);
		saver.save();

		String [] strings_=new String[Dumper.symbols.size()];
		for (int i=0;i < Dumper.symbols.size();++i)
			strings_[i] = Dumper.symbols.get(i).getDemangledName();
		FileSaver saver_=new FileSaver(this, Environment.getExternalStorageDirectory().toString() + "/Disassembler/symbols/", "Symbols_demangled.txt", strings_);
		saver_.save();


	}

	private com.gc.materialdesign.widgets.ProgressDialog mDialog;
	private SnackBar mBar;
	private Handler mHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (mDialog != null)
				mDialog.dismiss();
			mDialog = null;
			if (mBar != null)
				mBar.show();
			else
				new SnackBar(MenuActivity.this, MenuActivity.this.getString(R.string.done)).show();
		}
	};
	public void saveSymbols(View view)
	{
		mDialog = new com.gc.materialdesign.widgets.ProgressDialog(this, getString(R.string.saving));
		mDialog.show();
		mBar = new SnackBar(this, getString(R.string.done));
		new Thread()
		{
			public void run()
			{
				_saveSymbols();
				Message msg=new Message();
				mHandler.sendMessage(msg);
			}
		}.start();
	}
}
