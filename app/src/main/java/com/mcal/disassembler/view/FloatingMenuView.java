package com.mcal.disassembler.view;

import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;

import com.mcal.disassembler.R;
import com.mcal.disassembler.nativeapi.DisassemblerSymbol;
import com.mcal.disassembler.nativeapi.Searcher;

import java.util.Vector;

@SuppressLint("ViewConstructor")
public class FloatingMenuView extends RelativeLayout {
    private final Context context;
    private final EditText editText;
    private final TextView text;
    private final String path;

    FloatingMenuView(Context c, FloatingMenu menu, String filePath, int width, int height) {
        super(c);
        this.context = c;
        this.path = filePath;
        View view = LayoutInflater.from(c).inflate(R.layout.floating_menu, null);

        int buttonWidth = width / 6;

        AppCompatImageButton imageButtonClose = (AppCompatImageButton) view.findViewById(R.id.floatingmenuButtonClose);
        imageButtonClose.setOnClickListener(p1 -> {
            menu.dismiss();
            new FloatingButton(context, path).show();
        });
        setLayoutParams(imageButtonClose, buttonWidth);

        AppCompatImageButton imageButtonHide = (AppCompatImageButton) view.findViewById(R.id.floatingmenuButtonHide);
        imageButtonHide.setOnClickListener(p1 -> menu.dismiss());
        setLayoutParams(imageButtonHide, buttonWidth);
        editText = (EditText) view.findViewById(R.id.floatingmenuEditText);
        editText.postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(editText, 0);
        }, 50);


        buttonWidth *= 0.75;
        AppCompatImageButton imageButtonClear = (AppCompatImageButton) view.findViewById(R.id.floatingmenuButtonClear);
        imageButtonClear.setOnClickListener(p1 -> editText.getText().clear());
        setLayoutParams(imageButtonClear, buttonWidth);

        AppCompatImageButton imageButtonPaste = (AppCompatImageButton) view.findViewById(R.id.floatingmenuButtonPaste);
        imageButtonPaste.setOnClickListener(p1 -> {
            editText.setText(readFromClipboard());
        });
        setLayoutParams(imageButtonPaste, buttonWidth);

        AppCompatImageButton imageButtonSearch = (AppCompatImageButton) view.findViewById(R.id.floatingmenuButtonSearch);
        imageButtonSearch.setOnClickListener(p1 -> search(editText.getText().toString()));
        text = (TextView) view.findViewById(R.id.floatingmenuTextView);
        setLayoutParams(imageButtonSearch, buttonWidth);

        AppCompatImageButton imageButtonCopy = (AppCompatImageButton) view.findViewById(R.id.floatingmenuButtonCopy);
        imageButtonCopy.setOnClickListener(p1 -> {
            String stringYouExtracted = text.getText().toString();
            int startIndex = text.getSelectionStart();
            int endIndex = text.getSelectionEnd();
            stringYouExtracted = stringYouExtracted.substring(startIndex, endIndex);

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", stringYouExtracted);
            clipboard.setPrimaryClip(clip);
        });
        setLayoutParams(imageButtonCopy, buttonWidth);

        this.addView(view);
    }

    public String readFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        return null;
    }

    private void search(String name) {
        try {
            String localText = new String();
            Vector<DisassemblerSymbol> symbols = new Vector<DisassemblerSymbol>();
            symbols = Searcher.search(name);
            for (DisassemblerSymbol symbol : symbols)
                localText = localText + symbol.getDemangledName() + "\n";
            text.setText(localText);
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