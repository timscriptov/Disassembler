package com.mcal.disassembler;

import android.os.*;
import android.widget.*;
import android.view.*;
import com.mcal.disassembler.nativeapi.*;
import java.util.*;
import android.widget.AdapterView.*;
import android.content.*;
import android.support.v7.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity
{
	private EditText editText;
	private List<Map<String, Object>> data; 
	private String path;
	private ListView list;
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);

		editText = (EditText)findViewById(R.id.searchactivityEditText);
		path = getIntent().getExtras().getString("filePath");
		list = (ListView)findViewById(R.id.search_activity_list_view); 
		data = search_datas("", false);
		ResultAdapter adapter = new ResultAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ItemClickListener());
	}

	public void search(View view)
	{
		String key=editText.getText().toString();
		boolean usePattern=((CheckBox)findViewById(R.id.searchactivityCheckBoxUsePattern)).isChecked();
		search(key, usePattern);
	}

	private void search(final String key, final boolean usePattern)
	{
		new Thread()
		{
			public void run()
			{
				SearchActivity.this.mHandler.sendEmptyMessage(1);
				data = search_datas(key, usePattern);
				SearchActivity.this.mHandler.sendEmptyMessage(0);
				SearchActivity.this.mHandler.sendEmptyMessage(2);
			}
		}.start();
	}
	com.gc.materialdesign.widgets.ProgressDialog mProgressDialog;
	Handler mHandler=new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch (msg.what)
			{
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

	private List<Map<String, Object>> search_datas(String key, boolean usePattern) 
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); 
        Map<String, Object> map;
		Vector<DisassemblerSymbol> searchResult=null;
		if (key == null || key.isEmpty() || key == " " || key == "")
			return list;
		if (usePattern)
			searchResult = Searcher.searchWithPattern(key);
		else
			searchResult = Searcher.search(key);
		if (searchResult == null)
			return list;
        for (int i=0;i < searchResult.size();++i)
        { 
            map = new HashMap<String, Object>(); 
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
			Intent intent=new Intent(SearchActivity.this, SymbolActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

    public class ResultAdapter extends BaseAdapter 
    {     
		private LayoutInflater mInflater = null;
		private ResultAdapter(Context context) 
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
