package com.mcal.disassembler;

import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.mcal.disassembler.nativeapi.*;
import android.support.v7.app.AppCompatActivity;

public class NameDemanglerActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.name_demangler_activity);
		int width = getWindowManager().getDefaultDisplay().getWidth();

		EditText editText1=findViewById(R.id.namedemangleractivityEditText1);
		EditText editText2=findViewById(R.id.namedemangleractivityEditText2);

		ViewGroup.LayoutParams params1=editText1.getLayoutParams();
		params1.width = width / 2 - 1;
		editText1.setLayoutParams(params1);

		ViewGroup.LayoutParams params2=editText2.getLayoutParams();
		params2.width = width / 2 - 1;
		editText2.setLayoutParams(params2);
	}

	public void demangle(View view)
	{
		EditText editText1=findViewById(R.id.namedemangleractivityEditText1);
		EditText editText2=findViewById(R.id.namedemangleractivityEditText2);
		if (editText1.getText() == null || editText1.getText().toString() == null)
			return;
		String toName=DisassemblerDumper.demangle(editText1.getText().toString());
		editText2.getText().clear();
		editText2.getText().append(toName);
	}
}
