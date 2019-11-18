package com.mcal.disassembler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gc.materialdesign.widgets.SnackBar;
import com.mcal.disassembler.nativeapi.DisassemblerDumper;
import com.mcal.disassembler.nativeapi.DisassemblerVtable;
import com.mcal.disassembler.nativeapi.Dumper;
import com.mcal.disassembler.util.FileSaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VtableActivity extends AppCompatActivity {
    private List<Map<String, Object>> data;

    private String path;
    private String name;
    private DisassemblerVtable vtable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vtable_activity);

        path = Objects.requireNonNull(getIntent().getExtras()).getString("path");
        name = getIntent().getExtras().getString("name");
        for (DisassemblerVtable mvtable : Dumper.exploed)
            if (mvtable.getName().equals(name))
                vtable = mvtable;
        setTitle(DisassemblerDumper.demangle(name));

        ListView list = findViewById(R.id.vtable_activity_list_view);
        data = getData();
        VtablesAdapter adapter = new VtablesAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new ItemClickListener());
    }

    public void save(View view) {
        String[] strings = new String[vtable.getVtables().size()];
        for (int i = 0; i < vtable.getVtables().size(); ++i)
            strings[i] = vtable.getVtables().get(i).getName();

        FileSaver saver = new FileSaver(Environment.getExternalStorageDirectory().toString() + "/Disassembler/vtables/", name + ".txt", strings);
        saver.save();


        String[] strings_ = new String[vtable.getVtables().size()];
        for (int i = 0; i < vtable.getVtables().size(); ++i)
            strings_[i] = vtable.getVtables().get(i).getDemangledName();
        String demangledName = DisassemblerDumper.demangleOnly(name);
        String fileName = demangledName.substring(demangledName.lastIndexOf(" ") + 1);
        FileSaver saver_ = new FileSaver(Environment.getExternalStorageDirectory().toString() + "/Disassembler/vtables/", fileName + ".txt", strings_);
        saver_.save();

        new SnackBar(this, getString(R.string.done)).show();
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        if (vtable == null)
            return list;
        for (int i = 0; i < vtable.getVtables().size(); ++i) {
            map = new HashMap<>();
            map.put("img", R.drawable.box_blue);
            map.put("title", vtable.getVtables().get(i).getDemangledName());
            map.put("info", vtable.getVtables().get(i).getName());
            map.put("type", vtable.getVtables().get(i).getType());
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
            Intent intent = new Intent(VtableActivity.this, SymbolActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public class VtablesAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private VtablesAdapter(Context context) {
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

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

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