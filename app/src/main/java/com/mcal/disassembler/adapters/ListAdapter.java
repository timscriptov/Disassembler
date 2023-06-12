package com.mcal.disassembler.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mcal.disassembler.R;
import com.mcal.disassembler.data.RecentsManager;
import com.mcal.disassembler.interfaces.MainView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements Filterable {
    private final SearchFilter filter;
    private final MainView mainView;
    private ArrayList<String> paths;

    public ListAdapter(ArrayList<String> paths, MainView mainView) {
        this.paths = paths;
        this.mainView = mainView;
        filter = new SearchFilter();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    @Override
    public void onBindViewHolder(@NotNull final ListAdapter.ViewHolder holder, final int position) {
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
    public ListAdapter.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int p2) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_files, parent, false);
        return new ViewHolder(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView itemText, itemName;
        ImageView remove;

        ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.list_item);
            itemText = view.findViewById(R.id.item_path);
            itemName = view.findViewById(R.id.item_name);
            remove = view.findViewById(R.id.item_remove);
        }
    }

    private class SearchFilter extends Filter {
        private final ArrayList<String> items_backup = paths;
        private final ArrayList<String> filteredItems = new ArrayList<>();

        @Nullable
        @Override
        protected Filter.FilterResults performFiltering(CharSequence p1) {
            filteredItems.clear();

            for (int x = 0; x < items_backup.size(); x++) {
                String query = p1.toString().toLowerCase();
                if (items_backup.get(x).toLowerCase().contains((query))) {
                    filteredItems.add(items_backup.get(x));
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence p1, Filter.FilterResults p2) {
            paths = filteredItems;
            notifyDataSetChanged();
        }
    }
}