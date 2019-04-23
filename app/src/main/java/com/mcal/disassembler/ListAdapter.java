package com.mcal.disassembler;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import java.util.ArrayList;
import android.view.View.OnClickListener;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<String> paths;
    private MainView mainView;

    public ListAdapter(ArrayList<String> paths, MainView mainView) {
        this.paths = paths;
        this.mainView = mainView;
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position) {
        final String text = paths.get(position);

        holder.itemText.setText(text);
        holder.item.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					mainView.loadSo(paths.get(holder.getAdapterPosition()));
				}
			});

        holder.remove.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					RecentsManager.remove(paths.get(holder.getAdapterPosition()));
					paths.remove(holder.getAdapterPosition());
					notifyItemRemoved(holder.getAdapterPosition());
				}
		});
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int p2) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        AppCompatTextView itemText;
        AppCompatImageView remove;

        ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.list_item);
            itemText = view.findViewById(R.id.item_text);
            remove = view.findViewById(R.id.item_remove);
        }
    }
}
 
