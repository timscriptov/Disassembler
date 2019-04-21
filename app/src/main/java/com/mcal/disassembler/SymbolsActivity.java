package com.mcal.disassembler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.gc.materialdesign.widgets.SnackBar;
import com.mcal.disassembler.nativeapi.Dumper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.support.v7.app.AppCompatActivity;

public class SymbolsActivity extends AppCompatActivity
{
	private ListView list; 
    private List<Map<String, Object>> data; 
	private String path;
    @Override 
    public void onCreate(Bundle savedInstanceState)
	{ 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.symbols_activity);

        list = (ListView) findViewById(R.id.symbols_activity_list_view); 
		data = getData();
		SymbolsAdapter adapter = new SymbolsAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ItemClickListener());

		path = getIntent().getExtras().getString("filePath");
    }

    private List<Map<String, Object>> getData() 
    { 
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); 
        Map<String, Object> map;
        for (int i=0;i < Dumper.symbols.size();++i)
        { 
            map = new HashMap<String, Object>(); 
			if (Dumper.symbols.get(i).getType() == 1)
				map.put("img", R.drawable.box_blue); 
			else if (Dumper.symbols.get(i).getType() == 2)
				map.put("img", R.drawable.box_red);
			else map.put("img", R.drawable.box_pink);
			map.put("title", Dumper.symbols.get(i).getDemangledName());
            map.put("info", Dumper.symbols.get(i).getName());
			map.put("type", Dumper.symbols.get(i).getType());
            list.add(map);
        } 
        return list; 
    } 

	public void showSearch(View view)
	{
		Intent i=new Intent(this, SearchActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("filePath", path);
		i.putExtras(bundle);
		startActivity(i);
	}

	public void showMenu(View view)
	{
		Intent i=new Intent(this, MenuActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("filePath", path);
		i.putExtras(bundle);
		startActivity(i);
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
			Intent intent=new Intent(SymbolsActivity.this, SymbolActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

    public class SymbolsAdapter extends BaseAdapter 
    {     
		private LayoutInflater mInflater = null;
		private SymbolsAdapter(Context context) 
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
				holder.img = convertView.findViewById(R.id.symbolslistitemimg); 
				holder.title = convertView.findViewById(R.id.symbolslistitemTextViewtop); 
				holder.info = convertView.findViewById(R.id.symbolslistitemTextViewbottom); 
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

	SnackBar bar;

	@Override
	public void onBackPressed()
	{
		bar = new SnackBar(this, getString(R.string.againToExit));
		bar.show();
		bar.setDismissTimer(2500);
		bar.setOnBackPressedListener(new SnackBar.OnBackPressedListener()
			{

				@Override
				public void onBackPressed()
				{
					SymbolsActivity.this.finish();
				}

			});
	} 
}
