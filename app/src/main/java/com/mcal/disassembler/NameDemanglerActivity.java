package com.mcal.disassembler;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.mcal.disassembler.nativeapi.DisassemblerDumper;

public class NameDemanglerActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_demangler_activity);
        int width = getWindowManager().getDefaultDisplay().getWidth();

        EditText editText1 = findViewById(R.id.namedemangleractivityEditText1);
        EditText editText2 = findViewById(R.id.namedemangleractivityEditText2);

        ViewGroup.LayoutParams params1 = editText1.getLayoutParams();
        params1.width = width / 2 - 1;
        editText1.setLayoutParams(params1);

        ViewGroup.LayoutParams params2 = editText2.getLayoutParams();
        params2.width = width / 2 - 1;
        editText2.setLayoutParams(params2);
    }

    public void demangle(View view) {
        EditText editText1 = findViewById(R.id.namedemangleractivityEditText1);
        EditText editText2 = findViewById(R.id.namedemangleractivityEditText2);
        if (editText1.getText() == null) {
            return;
        } else {
            editText1.getText().toString();
        }
        String toName = DisassemblerDumper.demangle(editText1.getText().toString());
        editText2.getText().clear();
        editText2.getText().append(toName);
    }
}
