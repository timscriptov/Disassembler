package com.mcal.disassembler;

import android.content.*;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.gc.materialdesign.widgets.*;
import com.mcal.disassembler.nativeapi.*;
import com.mcal.disassembler.util.*;
import java.util.*;

public class VtableActivity extends AppCompatActivity
{
	private ListView list; 
    private List<Map<String, Object>> data; 

	private String path;
	private String name;
	private DisassemblerVtable vtable=null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vtable_activity);

		path = getIntent().getExtras().getString("path");
		name = getIntent().getExtras().getString("name");
		for (DisassemblerVtable mvtable:Dumper.exploed)
			if (mvtable.getName().equals(name))
				vtable = mvtable;
		setTitle(DisassemblerDumper.demangle(name));

		list = (ListView)findViewById(R.id.vtable_activity_list_view); 
		data = getData();
		VtablesAdapter adapter = new VtablesAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ItemClickListener());
	}

	public void save(View view)
	{
		String [] strings=new String[vtable.getVtables().size()];
		for (int i=0;i < vtable.getVtables().size();++i)
			strings[i] = vtable.getVtables().get(i).getName();

		FileSaver saver=new FileSaver(this, Environment.getExternalStorageDirectory().toString() + "/Disassembler/vtables/", name + ".txt", strings);
		saver.save();


		String [] strings_=new String[vtable.getVtables().size()];
		for (int i=0;i < vtable.getVtables().size();++i)
			strings_[i] = vtable.getVtables().get(i).getDemangledName();
		String demangledName=DisassemblerDumper.demangleOnly(name);
		String fileName=demangledName.substring(demangledName.lastIndexOf(" ") + 1, demangledName.length());
		FileSaver saver_=new FileSaver(this, Environment.getExternalStorageDirectory().toString() + "/Disassembler/vtables/", fileName + ".txt", strings_);
		saver_.save();

		new SnackBar(this, getString(R.string.done)).show();
	}

	private List<Map<String, Object>> getData() 
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); 
        Map<String, Object> map;

		if (vtable == null)
			return list;
        for (int i=0;i < vtable.getVtables().size();++i)
        { 
            map = new HashMap<String, Object>(); 
			map.put("img", R.drawable.box_blue);
			map.put("title", vtable.getVtables().get(i).getDemangledName());
            map.put("info", vtable.getVtables().get(i).getName());
			map.put("type", vtable.getVtables().get(i).getType());
            list.add(map);
        } 
        return list; 
    } 

    static class ViewHolder 
    { 
		public ImageView img;
		public TextView title;
		public TextView info;
		public int type;
    }

	private final class ItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3)
		{
			Bundle bundle=new Bundle();
			bundle.putString("demangledName", (String)(((ViewHolder)view.getTag()).title.getText()));
			bundle.putString("name", (String)(((ViewHolder)view.getTag()).info.getText()));
			bundle.putInt("type", ((ViewHolder)view.getTag()).type);
			bundle.putString("filePath", path);
			Intent intent=new Intent(VtableActivity.this, SymbolActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

    public class VtablesAdapter extends BaseAdapter 
    {     
		private LayoutInflater mInflater = null;
		private VtablesAdapter(Context context) 
		{ 
			this.mInflater = LayoutInflater.from(context); 
		} 

		@Override 
		public int getCount()
		{ 
			return data.size(); 
		} 

		@Override 
		public Object getItem(int position)
		{ 
			return position; 
		} 

		@Override 
		public long getItemId(int position)
		{  
			return position; 
		} 

		@Override 
		public View getView(int position, View convertView, ViewGroup parent)
		{ 
			ViewHolder holder = null; 

			if (convertView == null) 
			{ 
				holder = new ViewHolder(); 
				convertView = mInflater.inflate(R.layout.symbol_list_item, null); 
				holder.img = (ImageView)convertView.findViewById(R.id.symbolslistitemimg); 
				holder.title = (TextView)convertView.findViewById(R.id.symbolslistitemTextViewtop); 
				holder.info = (TextView)convertView.findViewById(R.id.symbolslistitemTextViewbottom); 
				convertView.setTag(holder);
			}
			else 
			{ 
				holder = (ViewHolder)convertView.getTag(); 
			} 
			holder.img.setBackgroundResource((Integer)data.get(position).get("img")); 
			holder.title.setText((String)data.get(position).get("title")); 
			holder.info.setText((String)data.get(position).get("info"));
			holder.type = ((int)data.get(position).get("type"));

			return convertView; 
		}


    }
}
