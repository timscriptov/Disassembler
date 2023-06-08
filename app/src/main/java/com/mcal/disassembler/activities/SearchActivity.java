package com.mcal.disassembler.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.mcal.disassembler.R;
import com.mcal.disassembler.nativeapi.DisassemblerSymbol;
import com.mcal.disassembler.nativeapi.Searcher;
import com.mcal.disassembler.view.CenteredToolBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class SearchActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    private TextInputEditText editText;
    private List<Map<String, Object>> data;
    private String path;
    private ListView list;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    ResultAdapter adapter = new ResultAdapter(SearchActivity.this);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new ItemClickListener());
                    break;
                case 1:
                    mProgressDialog = new ProgressDialog(SearchActivity.this);
                    mProgressDialog.setTitle(getString(R.string.loading));
                    mProgressDialog.show();
                    break;
                case 2:
                    if (mProgressDialog == null)
                        break;
                    mProgressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        setupToolbar(getString(R.string.app_search));
        editText = findViewById(R.id.searchactivityEditText);
        path = Objects.requireNonNull(getIntent().getExtras()).getString("filePath");
        list = findViewById(R.id.search_activity_list_view);
        data = search_datas("", false);
        ResultAdapter adapter = new ResultAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new ItemClickListener());
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        CenteredToolBar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void search(View view) {
        String key = editText.getText().toString();
        boolean usePattern = ((CheckBox) findViewById(R.id.searchactivityCheckBoxUsePattern)).isChecked();
        search(key, usePattern);
    }

    private void search(final String key, final boolean usePattern) {
        new Thread() {
            public void run() {
                mHandler.sendEmptyMessage(1);
                data = search_datas(key, usePattern);
                mHandler.sendEmptyMessage(0);
                mHandler.sendEmptyMessage(2);
            }
        }.start();
    }

    @NotNull
    private List<Map<String, Object>> search_datas(String key, boolean usePattern) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;
        Vector<DisassemblerSymbol> searchResult;
        if (key == null || key.isEmpty() || key.equals(" "))
            return list;
        if (usePattern)
            searchResult = Searcher.searchWithPattern(key);
        else
            searchResult = Searcher.search(key);
        if (searchResult == null)
            return list;
        for (int i = 0; i < searchResult.size(); ++i) {
            map = new HashMap<>();
            if (searchResult.get(i).getType() == 1)
                map.put("img", R.drawable.ic_box_blue);
            else if (searchResult.get(i).getType() == 2)
                map.put("img", R.drawable.ic_box_red);
            else map.put("img", R.drawable.ic_box_green);
            map.put("title", searchResult.get(i).getDemangledName());
            map.put("info", searchResult.get(i).getName());
            map.put("type", searchResult.get(i).getType());
            list.add(map);
        }
        return list;
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

    static class ViewHolder {
        public AppCompatTextView info;
        public int type;
        AppCompatImageView img;
        AppCompatTextView title;
    }

    private final class ItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, @NotNull View view, int arg2, long arg3) {
            Bundle bundle = new Bundle();
            bundle.putString("demangledName", (String) (((ViewHolder) view.getTag()).title.getText()));
            bundle.putString("name", (String) (((ViewHolder) view.getTag()).info.getText()));
            bundle.putInt("type", ((ViewHolder) view.getTag()).type);
            bundle.putString("filePath", path);
            Intent intent = new Intent(SearchActivity.this, SymbolActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public class ResultAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        private ResultAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.symbol_list_item, null);
                holder.img = convertView.findViewById(R.id.symbolslistitemimg);
                holder.title = convertView.findViewById(R.id.symbolslistitemTextViewtop);
                holder.info = convertView.findViewById(R.id.symbolslistitemTextViewbottom);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.img.setBackgroundResource((Integer) data.get(position).get("img"));
            holder.title.setText((String) data.get(position).get("title"));
            holder.info.setText((String) data.get(position).get("info"));
            holder.type = ((int) data.get(position).get("type"));

            return convertView;
        }
    }
}