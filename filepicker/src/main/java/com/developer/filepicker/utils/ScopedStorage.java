package com.developer.filepicker.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class ScopedStorage {
    public static File getStorageDirectory(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !Environment.isExternalStorageManager()) {
            return context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath());
        } else {
            return Environment.getExternalStorageDirectory();
        }
    }

    public static File getRootDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    public static String getDirPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }
}