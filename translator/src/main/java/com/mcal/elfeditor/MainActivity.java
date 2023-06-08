/*
 * [The "BSD licence"]
 * Copyright (c) 2017 ZhaoHai
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author zhaohai
 * @time 2015.10
 * ARSC编辑器主界面
 */
package com.mcal.elfeditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.mcal.materialdesign.view.CenteredToolBar;
import com.mcal.materialdesign.widgets.SnackBar;
import com.mcal.translator.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UnknownFormatConversionException;

public class MainActivity extends AppCompatActivity {
    // 存储资源种类的集合
    public static List<String> mTypes;
    private final int TVERY_LIGHT_BLUE = Color.argb(100, 51, 204, 255);
    private final int TVERY_LIGHT_GREY = Color.argb(50, 204, 204, 204);
    private final List<Integer> filteredList = new ArrayList<>();
    // 存储字符串的集合
    public List<String> txtOriginal = new ArrayList<>();
    // 存储修改后的字符串的集合
    public List<String> txtTranslated = new ArrayList<>();
    // 列表控件
    public ListView stringListView;
    // 数据处理器
    public StringListAdapter mAdapter;
    // 字符串是否修改
    public boolean isChanged;
    private Elf elfParser;
    // 存储资源的集合
    private Map<String, ResourceHelper> RESOURCES;
    // 显示资源种类的文本控件
    private ExtendedFloatingActionButton textCategory;
    /**
     * 文本框内容改变的事件监听器
     *
     * @author zhaohai
     */
    private final TextWatcher textWatcher = new TextWatcher() {

        // 文本改变后的事件处理
        @Override
        public void afterTextChanged(Editable s) {
            // 初始化一个线程用来获取资源
            AsyncTask<String, Void, Void> task = new GetTask();
            // 开启该线程
            task.execute(textCategory.getText().toString());
        }

        // 文本改变之前的事件处理
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        // 文本改变的事件处理
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };
    private LinearLayout searchWrap;
    private AppCompatEditText searchField;
    private int searchPosition = 0;
    private final TextWatcher searchWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            filteredList.clear();
            searchPosition = 0;
            String text = s.toString().toLowerCase();
            if (text.equals("")) {
                return;
            }
            for (int i = 0; i < txtOriginal.size(); i++) {
                String str = txtOriginal.get(i);
                if (str.toLowerCase().contains(text)) {
                    filteredList.add(i);
                }
            }
            mAdapter.notifyDataSetChanged();
            if (!filteredList.isEmpty()) {
                scroll(filteredList.get(searchPosition));
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };
    private String openedFile;
    // 一些控件的点击事件监听器
    private final OnClickListener MyOnClickListener = new OnClickListener() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View arg0) {
            // 点击了资源类型的文本框
            if (arg0.getId() == R.id.textCategory) {// 弹出一个对话框，列出所有的资源类型
                if (mTypes.isEmpty()) {
                    processFile();
                    return;
                }
                // 对话框上的条目点击的事件监听器
                new AlertDialog.Builder(MainActivity.this).setTitle("")
                        .setItems(mTypes.toArray(new String[mTypes.size()]), (arg01, arg1) -> {
                            textCategory.setText(mTypes.get(arg1));
                            textCategory.setIcon(getResources().getDrawable(R.drawable.ic_box_green));
                        }).create().show();
            }
        }
    };
    private boolean toExit;

    /**
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] InputStream2ByteArray(InputStream is) throws IOException {
        int count;
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((count = is.read(buffer)) != -1) {
            bos.write(buffer, 0, count);
        }
        bos.close();
        return bos.toByteArray();
    }

    // 显示信息的方法
    public static AlertDialog.Builder showMessage(Context activity, String message) {
        return new AlertDialog.Builder(activity).setMessage(message).setNegativeButton(R.string.ok, null)
                .setCancelable(false).setTitle(R.string.error);
    }

    public static String base64decode(String text) {
        try {
            byte[] valueDecoded = Base64.decode(text.getBytes(), Base64.NO_WRAP);
            return new String(valueDecoded);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean checkChanged() {
        for (String str : txtTranslated) {
            if (!str.equals("")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化容器
     **/
    private void initList() {
        for (int i = 0; i < txtOriginal.size(); i++) {
            // 向储存修改后的字符串的列表中添加空成员
            txtTranslated.add("");
        }
    }

    /**
     * 返回事件
     */
    @Override
    public void onBackPressed() {
        if (searchWrap.getVisibility() == LinearLayout.VISIBLE) {
            searchWrap.setVisibility(LinearLayout.GONE);
            filteredList.clear();
            mAdapter.notifyDataSetChanged();
            return;
        }
        if (isChanged || checkChanged()) { // 保存文件
            showSaveDialog(true);
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.elf_string_list);
        setupToolbar(getString(R.string.app_translate));
        // 初始化列表控件
        stringListView = findViewById(R.id.list_res_string);
        // 初始化显示资源类型的文本框
        textCategory = findViewById(R.id.textCategory);
        // 为显示资源类型的文本框设置点击事件的监听器
        textCategory.setOnClickListener(MyOnClickListener);
        // 为显示资源类型的文本框设置文本内容改变的监听器
        textCategory.addTextChangedListener(textWatcher);
        // 初始化数据适配器
        mAdapter = new StringListAdapter(this);
        // 为列表控件设置数据适配器
        stringListView.setAdapter(mAdapter);

        mTypes = new ArrayList<>();

        searchWrap = findViewById(R.id.search_wrapper);
        searchField = findViewById(R.id.search_field);
        searchField.addTextChangedListener(searchWatcher);
        searchField.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (searchPosition + 1 < filteredList.size()) {
                    searchPosition++;
                } else {
                    searchPosition = 0;
                }
                if (filteredList.isEmpty()) {
                    st(R.string.not_found);
                } else {
                    scroll(filteredList.get(searchPosition));
                }
                return true;
            }
            return false;
        });
        if (savedInstanceState != null && savedInstanceState.containsKey("opened_file")) {
            openedFile = savedInstanceState.getString("opened_file");
            try {
                open(new FileInputStream(openedFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupToolbar(String title) {
        CenteredToolBar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void scroll(int pos) {
        scroll(pos, true);
    }

    private void scroll(final int pos, final boolean focus) {
        new Handler().postDelayed(() -> {
            stringListView.setSelection(pos);
            if (focus) {
                searchField.requestFocus();
                searchField.setSelection(searchField.getText().length());
            }
        }, 100);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (openedFile != null) {
            outState.putString("opened_file", openedFile);
        }
    }

    public void st(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    public void st(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void open(InputStream resInputStream) {
        // 初始化一个线程用来解析资源文件
        AsyncTask<InputStream, Integer, String> task = new ParseTask();
        try {
            // 开启该线程
            task.execute(resInputStream);
        } catch (OutOfMemoryError e) {
            showMessage(this, getString(R.string.out_of_memory)).show();
            return;
        } catch (Exception e) {
            showMessage(this, Log.getStackTraceString(e)).show();
            return;
        }
        // 初始化一个线程用来获取解析后的资源
        AsyncTask<String, Void, Void> getTask = new GetTask();
        // 开启该线程
        getTask.execute(textCategory.getText().toString());
    }

    /**
     * ELF解析器
     *
     * @param callBack 用来存放结果
     * @param is       文件输入流
     **/
    public void parseELF(ResourceCallBack callBack, InputStream is)
            throws UnknownFormatConversionException, IOException {
        elfParser = new Elf(new ByteArrayInputStream(InputStream2ByteArray(is)), callBack);
    }

    /**
     * 显示保存文件的对话框
     **/
    private void showSaveDialog(final boolean exit) {
        new AlertDialog.Builder(this).setMessage(R.string.ensure_save)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    File file = new File(openedFile);
                    File bak = new File(openedFile + ".bak");
                    if (file.renameTo(bak)) {
                        file.delete();
                        SaveFileTask saveTask = new SaveFileTask(exit);
                        saveTask.execute(openedFile);
                    } else {
                        showMessage(MainActivity.this, getString(R.string.err_rename, openedFile)).show();
                    }
                }).setNegativeButton(R.string.no, (dialog, which) -> {
            if (exit) {
                setResult(Activity.RESULT_CANCELED, getIntent());
                finish();
            }
        }).create().show();
    }

    // 保存ELF字符串
    @SuppressLint("DefaultLocale")
    public void writeELFString(String output) throws IOException {
        // 整理RoData
        if (textCategory.getText().toString().equals("rodata")) {
            elfParser.sortStrData(txtOriginal, txtTranslated, elfParser.ro_items);
        } else { // 整理Dynstr
            elfParser.sortStrData(txtOriginal, txtTranslated, elfParser.dy_items);
        }
        OutputStream fos = new FileOutputStream(output);
        elfParser.writeELF(fos);
        fos.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.elf_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } /*else if (itemId == R.id.open) {
            if (searchWrap.getVisibility() == LinearLayout.VISIBLE) {
                searchWrap.setVisibility(LinearLayout.GONE);
                filteredList.clear();
                mAdapter.notifyDataSetChanged();
            }
            if (isChanged || checkChanged()) { // 保存文件
                showSaveDialog(true);
            } else {
                processFile();
            }
        }*/ else if (itemId == R.id.save) {
            showSaveDialog(false);
        } else if (itemId == R.id.opened) {
            if (openedFile != null) {
                new AlertDialog.Builder(this).
                        setTitle(R.string.opened).
                        setMessage(openedFile).
                        setPositiveButton(R.string.ok, null).
                        setNegativeButton(R.string.copy, (p1, p2) -> setClipboard(openedFile)).
                        create().
                        show();
            }
        } else if (itemId == R.id.go_to) {
            View view = LayoutInflater.from(this).inflate(R.layout.text_item, null);
            AppCompatEditText et = view.findViewById(R.id.inputEditText);
            et.setSingleLine(true);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            TextInputLayout etl = view.findViewById(R.id.inputLayout);
            etl.setHint(1 + " - " + txtOriginal.size());

            new AlertDialog.Builder(this).
                    setView(view).
                    setTitle(R.string.go_to).
                    setPositiveButton(R.string.ok, (p1, p2) -> {
                        try {
                            int line = Math.abs(Integer.parseInt(et.getText().toString()));
                            if (line > 0 && line <= txtOriginal.size()) {
                                scroll(line - 1, false);
                            } else {
                                st(R.string.error);
                            }
                        } catch (Exception e) {
                            st(R.string.error);
                        }
                    }).
                    setNegativeButton(R.string.cancel, null).
                    create().
                    show();
        } else if (itemId == R.id.search) {
            if (searchWrap.getVisibility() == LinearLayout.GONE) {
                searchWrap.setVisibility(LinearLayout.VISIBLE);
                new Handler().postDelayed(() -> {
                    searchField.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);
                    searchField.selectAll();
                }, 100);
            } else {
                searchWrap.setVisibility(LinearLayout.GONE);
                searchField.clearFocus();
                filteredList.clear();
                mAdapter.notifyDataSetChanged();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setClipboard(String text) {
        if (text == null || text.equals("")) {
            return;
        }
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        st(R.string.copied);
    }

    private void processFile() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(ScopedStorage.getRootDirectory().getAbsolutePath());
        properties.extensions = new String[]{".so", ".SO"};
        //Instantiate FilePickerDialog with Context and DialogProperties.
        FilePickerDialog dialog = new FilePickerDialog(this, properties, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.pickSo);
        dialog.setPositiveBtnName(getString(R.string.choose_button_label));
        dialog.setNegativeBtnName(getString(R.string.cancel_button_label));
        dialog.setDialogSelectionListener(files -> {
            for (String path : files) {
                File file = new File(path);
                if (file.getName().endsWith(".so") || file.getName().endsWith(".SO")) {
                    openedFile = file.getAbsolutePath();
                    try {
                        open(new FileInputStream(openedFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        openedFile = null;
                        showMessage(MainActivity.this, e.toString()).show();
                    }
                } else {
                    new SnackBar(this, getString(R.string.noFile)).show();
                }
            }
        });
        dialog.show();
    }

    /**
     * 一个用来获取解析后的资源的线程
     *
     * @author zhaohai
     */
    @SuppressLint("StaticFieldLeak")
    class GetTask extends AsyncTask<String, Void, Void> {
        // 进度条
        private ProgressDialog dlg;

        // 执行耗时任务
        @Override
        protected Void doInBackground(String... params) {
            if (RESOURCES != null) {
                ////////////////////////////////////////////////////////////////
                if (checkChanged()) {
                    // 整理RoData
                    if (textCategory.getText().toString().equals("dynstr")) {
                        elfParser.sortStrData(txtOriginal, txtTranslated, elfParser.ro_items);
                    } else { // 整理Dynstr
                        elfParser.sortStrData(txtOriginal, txtTranslated, elfParser.dy_items);
                    }
                    isChanged = true;
                }
                ////////////////////////////////////////////////////////////

                for (ResourceHelper resource : RESOURCES.values()) {
                    // 获取资源的值
                    String VALUE = resource.VALUE;
                    // 获取资源类型
                    String TYPE = resource.TYPE;

                    if (TYPE.equals(params[0])) {
                        // 向储存字符串的列表中添加字符串成员
                        txtOriginal.add(VALUE);
                    }
                }
                initList();
            }
            return null;
        }

        // 耗时任务执行完毕后的事件处理
        @Override
        protected void onPostExecute(Void result) {
            // 隐藏进度条
            dlg.dismiss();
            // 通知数据适配器更新数据
            // mAdapter.notifyDataSetInvalidated();
            mAdapter.notifyDataSetInvalidated();
        }

        // 耗时任务开始前执行的任务
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlg = new ProgressDialog(MainActivity.this);
            dlg.setCancelable(false);
            dlg.setTitle(R.string.parsing);
            dlg.show();
            txtOriginal.clear();
            txtTranslated.clear();
        }
    }

    /**
     * @author zhaohai 一个用来解析ARSC的线程
     */
    @SuppressLint("StaticFieldLeak")
    class ParseTask extends AsyncTask<InputStream, Integer, String> {
        // 进度条
        private ProgressDialog dlg;
        // 资源回调接口
        private ResourceCallBack callback;

        // 执行耗时任务
        @Override
        protected String doInBackground(InputStream... params) {

            try {
                parseELF(callback, params[0]);
            } catch (UnknownFormatConversionException | IOException e) {
                e.printStackTrace();
                return getString(R.string.failure);
            }
            return getString(R.string.success);
        }

        // 耗时任务执行完毕后的事件处理
        @Override
        protected void onPostExecute(String result) {
            // 隐藏进度条
            dlg.dismiss();
            // 如果返回的结果不是成功
            if (!result.equals(getString(R.string.success))) {
                // 显示错误信息
                showMessage(MainActivity.this, result).show();
                return;
            }
            // 对资源种类列表排序
            Collections.sort(mTypes);
            // 开启新线程
            AsyncTask<String, Void, Void> getTask = new GetTask();
            getTask.execute(textCategory.getText().toString());
            invalidateOptionsMenu();
        }

        // 耗时任务开始前执行的任务
        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlg = new ProgressDialog(MainActivity.this);
            dlg.setCancelable(false);
            dlg.setTitle(R.string.parsing);
            dlg.show();
            textCategory.setText("dynstr");
            textCategory.setIcon(getResources().getDrawable(R.drawable.ic_box_green));
            // 如果储存资源类型的列表未初始化
            if (mTypes == null) {
                // 初始化储存资源类型的列表
                mTypes = new ArrayList<>();
            }
            // 实现资源回调接口
            callback = helper -> {
                if (RESOURCES == null) {
                    RESOURCES = new LinkedHashMap<>();
                }
                RESOURCES.put(helper.VALUE, helper);
                // 如果资源种类集合中不存在该种类
                if (!mTypes.contains(helper.TYPE)) {
                    // 向其中添加该种类
                    mTypes.add(helper.TYPE);
                }
            };
        }

        // 更新ui界面
        @Override
        protected void onProgressUpdate(Integer... values) {
            dlg.setMessage(String.valueOf(values[0]));
        }
    }

    /**
     * @author zhaohai 一个用来保存资源文件的线程
     */
    @SuppressLint("StaticFieldLeak")
    class SaveFileTask extends AsyncTask<String, String, String> {
        private final boolean exit;
        // 进度条
        private ProgressDialog dlg;

        public SaveFileTask(boolean e) {
            this.exit = e;
        }

        // 执行耗时任务
        @Override
        protected String doInBackground(String... params) {
            try {
                writeELFString(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
            return getString(R.string.success);
        }

        // 耗时任务执行完毕后的事件处理
        @Override
        protected void onPostExecute(String result) {
            // 隐藏进度条
            dlg.dismiss();
            // 如果返回的结果不是成功
            if (!result.equals(getString(R.string.success))) {
                // 显示错误信息
                showMessage(MainActivity.this, result).show();
                return;
            }
            st(R.string.success);
            if (exit) {
                finish();
            } else {
                isChanged = false;
                for (int i = 0; i < txtOriginal.size(); i++) {
                    String s = txtTranslated.get(i);
                    if (!s.equals("")) {
                        txtOriginal.set(i, s);
                        txtTranslated.set(i, "");
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }

        // 耗时任务开始前执行的任务
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 初始化进度条
            dlg = new ProgressDialog(MainActivity.this);
            // 设置进度条标题
            dlg.setTitle(R.string.saving);
            // 设置按进度条外部进度条不消失
            dlg.setCancelable(false);
            // 显示进度条
            dlg.show();
        }

    }

    // 数据适配器
    public class StringListAdapter extends BaseAdapter {

        // 上下文
        private final Context mContext;
        private int minWidth = 0;

        // 构造函数
        public StringListAdapter(Context context) {
            super();
            // 获取上下文
            this.mContext = context;
        }

        // 获取数据成员个数
        @Override
        public int getCount() {
            return txtOriginal.size();
        }

        // 获取指定条目的内容
        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        // 获取指定条目的文字
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(final int position, View v, ViewGroup arg2) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.elf_res_string_item, null);

            // 文本框内容改变的事件监听器
            TextWatcher textWatcher = new TextWatcher() {

                // 文本改变后的事件处理
                @Override
                public void afterTextChanged(Editable s) {
                    // 向当前位置添加新的内容，以此实现文本的更新
                    txtTranslated.set(position, s.toString());
                    isChanged = true;
                    if (!filteredList.contains(position)) {
                        if (s.length() > 0) {
                            view.setBackgroundColor(TVERY_LIGHT_GREY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                }

                // 文本改变之前的事件处理
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                // 文本改变的事件处理
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            };

            String trans = txtTranslated.get(position);
            if (filteredList.contains(position)) {
                view.setBackgroundColor(TVERY_LIGHT_BLUE);
            } else if (!trans.equals("")) {
                view.setBackgroundColor(TVERY_LIGHT_GREY);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            LinearLayout ln_wrap = view.findViewById(R.id.ln_wrap);
            AppCompatTextView num = view.findViewById(R.id.number);
            //if (lineNumbers) {
            ln_wrap.setVisibility(LinearLayout.VISIBLE);
            num.setText(String.valueOf(position + 1));
            if (minWidth == 0) {
                float width = num.getPaint().measureText(String.valueOf(txtOriginal.size())) + mContext.getResources().getDimension(R.dimen.padding_normal); //  + int = padding in dp
                minWidth = Math.round(width * mContext.getResources().getDisplayMetrics().density * 0.5f);
            }
            num.setMinimumWidth(minWidth);
            //} else {
            //    ln_wrap.setVisibility(LinearLayout.GONE);
            //}

            // 获取显示原来的字符串的控件
            final AppCompatTextView txtOriginalView = view.findViewById(R.id.txtOriginal);
            // 获取用来修改的文本框
            AppCompatEditText txtTranslatedView = view.findViewById(R.id.txtTranslated);

            final String originalStr = txtOriginal.get(position);
            // 显示原来的字符串
            txtOriginalView.setText(originalStr);
            // 显示修改后的字符串
            // txtTranslatedView.setText(originalStr);
            txtTranslatedView.setText(trans);
            txtTranslatedView.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
                int len = 0;
                boolean more = false;
                do {
                    SpannableStringBuilder builder = new SpannableStringBuilder(dest).replace(dstart, dend,
                            source.subSequence(start, end));
                    len = builder.toString().getBytes().length;
                    more = len > originalStr.getBytes().length;
                    if (more) {
                        end--;
                        source = source.subSequence(start, end);
                    }
                } while (more);
                return source;
            }});
            // 为文本框设置内容改变的监听器
            txtTranslatedView.addTextChangedListener(textWatcher);
            View.OnLongClickListener longclick_listener = v1 -> {
                setClipboard(txtOriginalView.getText().toString());
                return true;
            };
            txtOriginalView.setOnClickListener(p1 -> {
                PopupMenu popup = new PopupMenu(mContext, p1);
                MenuItem mi = popup.getMenu().add(0, 1010, 0, getString(R.string.copy));
                mi.setOnMenuItemClickListener(m -> {
                    setClipboard(txtOriginalView.getText().toString());
                    return true;
                });
                MenuItem mi2 = popup.getMenu().add(0, 1011, 0, getString(R.string.b64));
                mi2.setOnMenuItemClickListener(m -> {
                    String b64d = base64decode(txtOriginalView.getText().toString());
                    if (b64d != null) {
                        View views = LayoutInflater.from(mContext).inflate(R.layout.text_item, null);
                        AppCompatEditText et = views.findViewById(R.id.inputEditText);
                        et.setText(b64d);
                        new AlertDialog.Builder(mContext).
                                setView(views).
                                setTitle(R.string.b64).
                                setPositiveButton(R.string.ok, null).
                                create().
                                show();
                    } else {
                        st(R.string.error);
                    }
                    return true;
                });
                popup.show();
            });
            txtOriginalView.setOnLongClickListener(longclick_listener);
            return view;
        }
    }
}
