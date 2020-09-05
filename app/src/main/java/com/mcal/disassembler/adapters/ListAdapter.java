package com.mcal.disassembler.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mcal.disassembler.R;
import com.mcal.disassembler.data.RecentsManager;
import com.mcal.disassembler.interfaces.MainView;

import java.util.ArrayList;

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
        holder.itemName.setText(text.replaceAll(".*/(\\w+?\\.so)", "$1"));
        holder.item.setOnClickListener(p1 -> mainView.loadSo(paths.get(holder.getAdapterPosition())));

        holder.remove.setOnClickListener(p1 -> {
            RecentsManager.remove(paths.get(holder.getAdapterPosition()));
            paths.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
        });
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int p2) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        AppCompatTextView itemText, itemName;
        AppCompatImageView remove;

        ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.list_item);
            itemText = view.findViewById(R.id.item_path);
            itemName = view.findViewById(R.id.item_name);
            remove = view.findViewById(R.id.item_remove);
        }
    }
}