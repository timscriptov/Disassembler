package com.mcal.disassembler;

import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RelativeLayout;
import java.util.Vector;

import com.gc.materialdesign.views.ButtonFlat;

import com.mcal.disassembler.nativeapi.DisassemblerSymbol;
import com.mcal.disassembler.nativeapi.Searcher;

public class FloatingMenuView extends RelativeLayout
{
	private FloatingMenu menu;
	private Context context;
	private EditText editText;
	private TextView text;
	private String path;

	FloatingMenuView(Context c, FloatingMenu menu, String filePath, int width, int height)
	{
		super(c);
		this.menu = menu;
		this.context = c;
		this.path = filePath;
		View view = LayoutInflater.from(c).inflate(R.layout.floating_menu, null); 

		int buttonWidth=width / 6;

		ButtonFlat imageButtonClose=view.findViewById(R.id.floatingmenuButtonClose);
		imageButtonClose.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View p1)
				{
					FloatingMenuView.this.menu.dismiss();
					new FloatingButton(FloatingMenuView.this.context, path).show();
				}

			});
		setLayoutParams(imageButtonClose, buttonWidth);
		ButtonFlat imageButtonHide=view.findViewById(R.id.floatingmenuButtonHide);
		imageButtonHide.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View p1)
				{
					FloatingMenuView.this.menu.dismiss();
				}

			});
		setLayoutParams(imageButtonHide, buttonWidth);
		editText = view.findViewById(R.id.floatingmenuEditText);
		buttonWidth *= 0.75;
		ButtonFlat imageButtonClear=view.findViewById(R.id.floatingmenuButtonClear);
		imageButtonClear.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View p1)
				{
					FloatingMenuView.this.editText.getText().clear();
				}

			});
		setLayoutParams(imageButtonClear, buttonWidth);
		ButtonFlat imageButtonSearch=view.findViewById(R.id.floatingmenuButtonSearch);
		imageButtonSearch.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View p1)
				{
					search(FloatingMenuView.this.editText.getText().toString());
				}

			});
		text = view.findViewById(R.id.floatingmenuTextView);
		setLayoutParams(imageButtonSearch, buttonWidth);
		this.addView(view);
	}

	private void search(String name)
	{
		try
		{
			String localText = new String();
			Vector<DisassemblerSymbol> symbols=new Vector<DisassemblerSymbol>();
			symbols = Searcher.search(name);
			for (DisassemblerSymbol symbol:symbols)
				localText = localText + symbol.getDemangledName() + "\n";
			text.setText(localText);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setLayoutParams(View view, int w)
	{
		ViewGroup.LayoutParams params=view.getLayoutParams();
		params.width = w;
		view.setLayoutParams(params);
	}
}
