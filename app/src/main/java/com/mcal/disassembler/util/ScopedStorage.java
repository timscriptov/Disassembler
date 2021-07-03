package com.mcal.disassembler.util;

import android.os.Build;
import android.os.Environment;

import com.mcal.disassembler.App;

import java.io.File;

public class ScopedStorage {
    public static File getStorageDirectory() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !Environment.isExternalStorageManager()) {
            return App.getContext().getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath());
        } else {
            return Environment.getExternalStorageDirectory();
        }
    }

    public static File getRootDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    public static String getDirPath() {
        return App.getContext().getFilesDir().getAbsolutePath();
    }
}
