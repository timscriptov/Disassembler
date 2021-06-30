package com.mcal.disassembler.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.disassembler.R;
import com.mcal.disassembler.nativeapi.DisassemblerVtable;
import com.mcal.disassembler.nativeapi.Dumper;
import com.mcal.disassembler.view.CenteredToolBar;
import com.mcal.disassembler.vtable.Tables;
import com.mcal.disassembler.vtable.VtableDumper;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SymbolActivity extends AppCompatActivity {
    ProgressDialog dialog;
    private CenteredToolBar toolbar;
    private String path;
    private String name;
    private String className;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symbol_activity);
        setupToolbar(getString(R.string.app_symbol));
        int type = Objects.requireNonNull(getIntent().getExtras()).getInt("type");
        name = getIntent().getExtras().getString("name");
        String demangledName = getIntent().getExtras().getString("demangledName");
        path = getIntent().getExtras().getString("filePath");

        AppCompatImageView imageTitile = findViewById(R.id.symbolactivityImageView);
        if (type == 1)
            imageTitile.setImageResource(R.drawable.ic_box_blue);
        else if (type == 2)
            imageTitile.setImageResource(R.drawable.ic_box_red);
        else
            imageTitile.setImageResource(R.drawable.ic_box_green);

        AppCompatTextView textName = findViewById(R.id.symbolactivityTextViewName);
        textName.setText(name);

        AppCompatTextView textDemangledName = findViewById(R.id.symbolactivityTextViewDemangledName);
        textDemangledName.setText(demangledName);

        String arguments;
        if (Objects.requireNonNull(demangledName).contains("(") && demangledName.lastIndexOf(")") != -1)
            arguments = demangledName.substring(demangledName.indexOf("(") + 1, demangledName.lastIndexOf(")"));
        else
            arguments = "NULL";
        AppCompatTextView textArguments = findViewById(R.id.symbolactivityTextViewArguments);
        textArguments.setText(arguments);

        String symbolMainName;
        if (demangledName.contains("("))
            symbolMainName = demangledName.substring(0, demangledName.indexOf("("));
        else
            symbolMainName = demangledName;
        className = "";
        if (symbolMainName.lastIndexOf("::") != -1)
            className = symbolMainName.substring(0, symbolMainName.lastIndexOf("::"));
        else if (symbolMainName.startsWith("vtable"))
            className = symbolMainName.substring(symbolMainName.lastIndexOf(" ") + 1);
        else
            className = "NULL";
        AppCompatTextView textClassName = findViewById(R.id.symbolactivityTextClass);
        textClassName.setText(className);

        String symbolName;
        if (symbolMainName.lastIndexOf("::") != -1)
            symbolName = symbolMainName.substring(symbolMainName.lastIndexOf("::") + 2);
        else
            symbolName = symbolMainName;

        AppCompatTextView textSymbolName = findViewById(R.id.symbolactivityTextViewSymbolMainName);
        textSymbolName.setText(symbolName);

        String typeName = Tables.symbol_type.get(type);

        AppCompatTextView textTypeName = findViewById(R.id.symbolactivityTextViewType);
        textTypeName.setText(typeName);

        if (name.startsWith("_ZTV")) {
            findViewById(R.id.symbolactivityButtonFloat).setVisibility(View.VISIBLE);
        }

        if (!className.equals("NULL")) {
            findViewById(R.id.symbolactivityButtonFloatClass).setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void toVtableActivity(View view) {
        showProgressDialog();
        new Thread() {
            public void run() {
                DisassemblerVtable vtable = VtableDumper.dump(SymbolActivity.this.path, SymbolActivity.this.name);
                if (vtable != null)
                    SymbolActivity.this.toVtableActivity_(vtable);
                dismissProgressDialog();
            }
        }.start();
    }

    public void toClassActivity(View view) {
        showProgressDialog();
        new Thread() {
            public void run() {
                DisassemblerVtable vtable = VtableDumper.dump(SymbolActivity.this.path, SymbolActivity.this.name);
                if (vtable != null)
                    SymbolActivity.this.toClassActivity_();
                dismissProgressDialog();
            }
        }.start();
    }

    public void toClassActivity_() {
        if (className == null || className.equals("") || className.equals(" ") || className.isEmpty() || className.equals("NULL"))
            return;
        Bundle bundle = new Bundle();
        bundle.putString("name", className);
        bundle.putString("path", path);
        Intent intent = new Intent(this, ClassActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void toVtableActivity_(DisassemblerVtable vtable) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("path", path);
        Dumper.exploed.addElement(vtable);
        Intent intent = new Intent(this, VtableActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.loading));
        dialog.show();
    }

    public void dismissProgressDialog() {
        if (dialog != null)
            dialog.dismiss();
        dialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}