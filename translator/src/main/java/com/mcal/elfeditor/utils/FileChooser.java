package com.mcal.elfeditor.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;

public class FileChooser {
    private static final String PARENT_DIR = "..";
    private final Activity activity;
    private final ListView list;
    private final Dialog dialog;
    private File currentPath;

    // filter on file extension
    private String extension = null;
    private FileSelectedListener fileListener;

    public FileChooser(Activity activity) {
        this.activity = activity;
        dialog = new Dialog(activity);
        list = new ListView(activity);
        list.setOnItemClickListener((parent, view, which, id) -> {
            SpannableStringBuilder fileChosen = (SpannableStringBuilder) list.getItemAtPosition(which);
            File chosenFile = getChosenFile(fileChosen.toString());
            if (chosenFile.isDirectory()) {
                refresh(chosenFile);
            } else {
                if (fileListener != null) {
                    fileListener.fileSelected(chosenFile);
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(list);
        dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        refresh(Environment.getExternalStorageDirectory());
    }

    public void setExtension(String extension) {
        this.extension = (extension == null) ? null : extension.toLowerCase();
    }

    public FileChooser setFileListener(FileSelectedListener fileListener) {
        this.fileListener = fileListener;
        return this;
    }

    public void showDialog() {
        dialog.show();
    }

    /**
     * Sort, filter and display the files for the given path.
     */
    private void refresh(File path) {
        this.currentPath = path;
        if (path.exists()) {
            File[] dirs = path.listFiles(file -> (file.isDirectory() && file.canRead()));
            File[] files = path.listFiles(file -> {
                if (!file.isDirectory()) {
                    if (!file.canRead()) {
                        return false;
                    } else if (extension == null) {
                        return true;
                    } else {
                        return file.getName().toLowerCase().endsWith(extension);
                    }
                } else {
                    return false;
                }
            });

            // convert to an array
            int i = 0;
            SpannableStringBuilder[] fileList;
            if (path.getParentFile() == null || !path.getParentFile().canRead()) {
                fileList = new SpannableStringBuilder[dirs.length + files.length];
            } else {
                fileList = new SpannableStringBuilder[dirs.length + files.length + 1];
                SpannableStringBuilder sb = new SpannableStringBuilder(PARENT_DIR);
                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                fileList[i++] = sb;
            }
            Arrays.sort(dirs);
            Arrays.sort(files);
            for (File dir : dirs) {
                SpannableStringBuilder sb = new SpannableStringBuilder(dir.getName());
                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                fileList[i++] = sb;
            }
            for (File file : files) {
                SpannableStringBuilder sb = new SpannableStringBuilder(file.getName());
                StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC);
                sb.setSpan(iss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                fileList[i++] = sb;
            }

            // refresh the user interface
            dialog.setTitle(currentPath.getPath());
            list.setAdapter(new ArrayAdapter(activity, android.R.layout.simple_list_item_1, fileList) {
                @Override
                public View getView(int pos, View view, ViewGroup parent) {
                    view = super.getView(pos, view, parent);
                    ((TextView) view).setSingleLine(true);
                    return view;
                }
            });
        }
    }

    /**
     * Convert a relative filename into an actual File object.
     */
    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) {
            return currentPath.getParentFile();
        } else {
            return new File(currentPath, fileChosen);
        }
    }


    // file selection event handling
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
}
