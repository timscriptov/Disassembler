package com.mcal.disassembler.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.mcal.disassembler.R;
import com.mcal.disassembler.nativeapi.DisassemblerDumper;
import com.mcal.disassembler.view.CenteredToolBar;

import org.jetbrains.annotations.NotNull;

public class NameDemanglerActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_demangler_activity);
        setupToolbar(getString(R.string.app_symbols));
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        CenteredToolBar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void demangle(View view) {
        TextInputEditText editText1 = findViewById(R.id.namedemangleractivityEditText1);
        TextInputEditText editText2 = findViewById(R.id.namedemangleractivityEditText2);
        if (editText1.getText() == null) {
            return;
        } else {
            editText1.getText().toString();
        }
        String toName = DisassemblerDumper.demangle(editText1.getText().toString());
        editText2.getText().clear();
        editText2.getText().append(toName);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
