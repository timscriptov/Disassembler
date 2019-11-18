package com.mcal.disassembler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mcal.disassembler.nativeapi.DisassemblerSymbol;
import com.mcal.disassembler.nativeapi.Searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class SearchActivity extends AppCompatActivity {
    com.gc.materialdesign.widgets.ProgressDialog mProgressDialog;
    private EditText editText;
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
                    SearchActivity.this.mProgressDialog = new com.gc.materialdesign.widgets.ProgressDialog(SearchActivity.this, SearchActivity.this.getString(R.string.loading));
                    SearchActivity.this.mProgressDialog.show();
                    break;
                case 2:
                    if (SearchActivity.this.mProgressDialog == null)
                        break;
                    SearchActivity.this.mProgressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        editText = findViewById(R.id.searchactivityEditText);
        path = Objects.requireNonNull(getIntent().getExtras()).getString("filePath");
        list = findViewById(R.id.search_activity_list_view);
        data = search_datas("", false);
        ResultAdapter adapter = new ResultAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new ItemClickListener());
    }

    public void search(View view) {
        String key = editText.getText().toString();
        boolean usePattern = ((CheckBox) findViewById(R.id.searchactivityCheckBoxUsePattern)).isChecked();
        search(key, usePattern);
    }

    private void search(final String key, final boolean usePattern) {
        new Thread() {
            public void run() {
                SearchActivity.this.mHandler.sendEmptyMessage(1);
                data = search_datas(key, usePattern);
                SearchActivity.this.mHandler.sendEmptyMessage(0);
                SearchActivity.this.mHandler.sendEmptyMessage(2);
            }
        }.start();
    }

    private List<Map<String, Object>> search_datas(String key, boolean usePattern) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;
        Vector<DisassemblerSymbol> searchResult;
        if (key == null || key.isEmpty() || key.equals(" ") || key.equals(""))
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
                map.put("img", R.drawable.box_blue);
            else if (searchResult.get(i).getType() == 2)
                map.put("img", R.drawable.box_red);
            else map.put("img", R.drawable.box_pink);
            map.put("title", searchResult.get(i).getDemangledName());
            map.put("info", searchResult.get(i).getName());
            map.put("type", searchResult.get(i).getType());
            list.add(map);
        }
        return list;
    }

    static class ViewHolder {
        ImageView img;
        TextView title;
        public TextView info;
        public int type;
    }

    private final class ItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
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
        private LayoutInflater mInflater;

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