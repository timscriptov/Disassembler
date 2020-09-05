package com.mcal.disassembler.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.disassembler.R;
import com.mcal.disassembler.nativeapi.DisassemblerSymbol;
import com.mcal.disassembler.nativeapi.Searcher;

import java.util.Vector;

@SuppressLint("ViewConstructor")
public class FloatingMenuView extends RelativeLayout {
    private FloatingMenu menu;
    private Context context;
    private AppCompatEditText editText;
    private AppCompatTextView text;
    private String path;

    FloatingMenuView(Context c, FloatingMenu menu, String filePath, int width) {
        super(c);
        this.menu = menu;
        this.context = c;
        this.path = filePath;
        @SuppressLint("InflateParams") View view = LayoutInflater.from(c).inflate(R.layout.floating_menu, null);

        int buttonWidth = width / 6;

        ButtonFlat imageButtonClose = view.findViewById(R.id.floatingmenuButtonClose);
        imageButtonClose.setOnClickListener(p1 -> {
            FloatingMenuView.this.menu.dismiss();
            new FloatingButton(FloatingMenuView.this.context, path).show();
        });
        setLayoutParams(imageButtonClose, buttonWidth);
        ButtonFlat imageButtonHide = view.findViewById(R.id.floatingmenuButtonHide);
        imageButtonHide.setOnClickListener(p1 -> FloatingMenuView.this.menu.dismiss());
        setLayoutParams(imageButtonHide, buttonWidth);
        editText = view.findViewById(R.id.floatingmenuEditText);
        buttonWidth *= 0.75;
        ButtonFlat imageButtonClear = view.findViewById(R.id.floatingmenuButtonClear);
        imageButtonClear.setOnClickListener(p1 -> FloatingMenuView.this.editText.getText().clear());
        setLayoutParams(imageButtonClear, buttonWidth);
        ButtonFlat imageButtonSearch = view.findViewById(R.id.floatingmenuButtonSearch);
        imageButtonSearch.setOnClickListener(p1 -> search(FloatingMenuView.this.editText.getText().toString()));
        text = view.findViewById(R.id.floatingmenuTextView);
        setLayoutParams(imageButtonSearch, buttonWidth);
        this.addView(view);
    }

    private void search(String name) {
        try {
            StringBuilder localText = new StringBuilder();
            Vector<DisassemblerSymbol> symbols;
            symbols = Searcher.search(name);
            for (DisassemblerSymbol symbol : symbols)
                localText.append(symbol.getDemangledName()).append("\n");
            text.setText(localText.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLayoutParams(View view, int w) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = w;
        view.setLayoutParams(params);
    }
}
