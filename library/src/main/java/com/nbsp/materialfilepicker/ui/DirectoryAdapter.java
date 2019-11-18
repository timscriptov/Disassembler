package com.nbsp.materialfilepicker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nbsp.materialfilepicker.R;
import com.nbsp.materialfilepicker.utils.FileTypeUtils;

import java.io.File;
import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {
    private List<File> mFiles;
    private OnItemClickListener mOnItemClickListener;
    DirectoryAdapter(Context context, List<File> files) {
        mFiles = files;
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);

        return new DirectoryViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(DirectoryViewHolder holder, int position) {
        File currentFile = mFiles.get(position);

        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);
        holder.mFileImage.setImageResource(fileType.getIcon());
        holder.mFileSubtitle.setText(fileType.getDescription());
        if (!fileType.equals(FileTypeUtils.FileType.DIRECTORY))
            holder.mFileSize.setText(SpaceFormatter.format(currentFile.length()));
        holder.mFileTitle.setText(currentFile.getName());
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    File getModel(int index) {
        return mFiles.get(index);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class DirectoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView mFileImage;
        private TextView mFileTitle;
        private TextView mFileSubtitle;
        private TextView mFileSize;

        DirectoryViewHolder(View itemView, final OnItemClickListener clickListener) {
            super(itemView);

            itemView.setOnClickListener(v -> clickListener.onItemClick(v, getAdapterPosition()));

            mFileImage = itemView.findViewById(R.id.item_file_image);
            mFileTitle = itemView.findViewById(R.id.item_file_title);
            mFileSubtitle = itemView.findViewById(R.id.item_file_subtitle);
            mFileSize = itemView.findViewById(R.id.item_file_size);
        }
    }
}
