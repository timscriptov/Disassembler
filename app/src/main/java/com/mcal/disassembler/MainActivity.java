package com.mcal.disassembler;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.mcal.disassembler.nativeapi.DisassemblerDumper;
import com.mcal.disassembler.nativeapi.Dumper;
import com.mcal.disassembler.util.FileUtils;

public class MainActivity extends Activity
{
	private String path;
	private static final int FILE_SELECT_CODE = 0;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		/*if (Build.VERSION.SDK_INT >= 23)
		{
			if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			{
				requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
			}
		}*/
		
		TextView textViewSavePath=findViewById(R.id.mainactivityTextViewSavePath);
		textViewSavePath.setText(getString(R.string.savedIn) + Environment.getExternalStorageDirectory().toString() + "/Disassembler/*");
	}

	public void chooseSdcard(View view)
	{
		showFileChooser("*/*");
	}

	private void showFileChooser(String type)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		intent.setType(type); 
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try
		{
			startActivityForResult(Intent.createChooser(intent, getString(R.string.pickSo)), FILE_SELECT_CODE);
		} 
		catch (android.content.ActivityNotFoundException ex)
		{
			new SnackBar(this, getString(R.string.noFile)).show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		switch (requestCode)
		{
			case FILE_SELECT_CODE:      
				if (resultCode == RESULT_OK)
				{  
					Uri uri = data.getData();
					String path = FileUtils.getPath(this, uri);
					if (path.endsWith(".so") && DisassemblerDumper.hasFile(path))
						loadSo(path);
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void loadSo(final String path)
	{
		showProgressDialog();
		this.path = path;
		new Thread()
		{
			public void run()
			{
				DisassemblerDumper.load(path);
				Dumper.readData(path);
				MainActivity.this.toClassesActivity();
			}
		}.start();
	}

	ProgressDialog dialog;

	public void showProgressDialog()
	{
		dialog = new ProgressDialog(MainActivity.this, getString(R.string.loading));
		dialog.show();
	}
	public void dismissProgressDialog()
	{
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}
	public void toClassesActivity()
	{
		Bundle bundle=new Bundle();
		bundle.putString("filePath", path);
		Intent intent=new Intent(MainActivity.this, SymbolsActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		dismissProgressDialog();
	}

    static
	{
        System.loadLibrary("disassembler");
    }

}
