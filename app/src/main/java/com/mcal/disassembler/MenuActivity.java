package com.mcal.disassembler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.gc.materialdesign.widgets.SnackBar;
import com.mcal.disassembler.nativeapi.Dumper;
import com.mcal.disassembler.util.FileSaver;

public class MenuActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler
{
	private static final String URI_GITHUB = "https://github.com/TimScriptov/Disassembler.git";

	private String path;
	private BillingProcessor bp;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_activity);

		path = getIntent().getExtras().getString("filePath");
		
		bp = new BillingProcessor(this, null, this);

		findViewById(R.id.about_view_github_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View p1) {
				openUri(URI_GITHUB);
			}
		});
	}

	private void openUri(String uri) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(uri);
		intent.setData(content_url);
		startActivity(intent);
	}

	public void donate(View v) {
        bp.purchase(this, "donate_disassembler");
    }

    public void onPurchaseHistoryRestored() {
    }

    public void onBillingError(int p1, Throwable p2) {
    }

    public void onBillingInitialized() {
    }
	
    public void onProductPurchased(String p1, TransactionDetails p2) {
        Toast.makeText(this, R.string.thanks, Toast.LENGTH_LONG).show();
        bp.consumePurchase(p1);
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

	@SuppressLint("HandlerLeak")
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
